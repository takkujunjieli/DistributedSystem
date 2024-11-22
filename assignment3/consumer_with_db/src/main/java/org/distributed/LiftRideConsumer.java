package org.distributed;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import java.util.concurrent.*;
import org.slf4j.LoggerFactory;

public class LiftRideConsumer {
  private static final Logger log = LoggerFactory.getLogger(LiftRideConsumer.class);
  private final ConcurrentHashMap<Integer, LiftRideRecord> skierRecords;
  private final ConnectionFactory factory;
  private final String queueName;
  private final int numThreads;
  private final ExecutorService executorService;
  private final ObjectMapper objectMapper;
  private com.rabbitmq.client.Connection connection;
  private ChannelPool channelPool;
  private LiftRideDAO liftRideDAO = new LiftRideDAO();

  private final BlockingQueue<LiftRideRequest> batchQueue;
  private final int batchSize;
  private final ScheduledExecutorService batchExecutor;

  public LiftRideConsumer() {
    this.skierRecords = new ConcurrentHashMap<>();
    Properties properties = loadProperties();

    String host = properties.getProperty("rabbitmq.host");
    this.queueName = properties.getProperty("rabbitmq.queue");
    this.numThreads = Integer.parseInt(properties.getProperty("consumer.numThreads"));
    this.executorService = Executors.newFixedThreadPool(numThreads);
    this.objectMapper = new ObjectMapper();
    this.batchSize = Integer.parseInt(properties.getProperty("consumer.batchSize"));

    this.batchQueue = new LinkedBlockingQueue<>(batchSize);
    this.batchExecutor = Executors.newSingleThreadScheduledExecutor();
    this.batchExecutor.scheduleAtFixedRate(this::processBatch,1, 1, TimeUnit.SECONDS);

    factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setConnectionTimeout(5000);
    factory.setAutomaticRecoveryEnabled(true);
  }

  private Properties loadProperties() {
    Properties properties = new Properties();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      if (input == null) {
        log.error("Sorry, unable to find application.properties");
        return properties;
      }
      properties.load(input);
    } catch (IOException ex) {
      log.error("Error loading application.properties", ex);
    }
    return properties;
  }

  public void startConsuming() throws IOException, TimeoutException {
    try {
      connection = factory.newConnection(executorService);
      channelPool = new ChannelPool(connection, numThreads);

      for (int i = 0; i < numThreads; i++) {
        executorService.submit(new ConsumerWorker());
      }

      log.info("Started {} consumer threads", numThreads);
    } catch (IOException | TimeoutException e) {
      log.error("Error starting consumer", e);
      shutdown();
    }
  }

  private class ConsumerWorker implements Runnable {
    @Override
    public void run() {
      Channel channel = null;
      try {
        channel = channelPool.borrowChannel();
//        channel.queueDeclare(queueName, true, false, false, null);
        channel.basicQos(20);

        Consumer consumer = createConsumer(channel);
        String consumerTag = channel.basicConsume(queueName, false, consumer);
        log.info("Consumer registered with tag: {}", consumerTag);
      } catch (IOException e) {
        log.error("Error in consumer worker", e);
      } finally {
        if (channel != null) {
          channelPool.returnChannel(channel);
        }
      }
    }
  }

  private Consumer createConsumer(Channel channel) {
    return new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
          AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        try {
          LiftRideRequest request = objectMapper.readValue(body, LiftRideRequest.class);
          batchQueue.offer(request);
//          processLiftRide(request);

          // Acknowledge after processing
          channel.basicAck(envelope.getDeliveryTag(), false);
          log.debug("Processed message for skier: {}", request.getSkierID());
        } catch (Exception e) {
          log.error("Error processing message", e);

          // Nack if processing fails
          try {
            channel.basicNack(envelope.getDeliveryTag(), false, true);
          } catch (IOException nackException) {
            log.error("Error sending nack for message", nackException);
          }
        }
      }
    };
  }


  private void processBatch() {
    List<LiftRideRequest> batch = new ArrayList<>();
    batchQueue.drainTo(batch, batchSize);

    if (!batch.isEmpty()) {
      try {
        liftRideDAO.saveToDatabase(batch);
        batch.forEach(request -> {
          skierRecords.computeIfAbsent(request.getSkierID(), k -> new LiftRideRecord())
              .addRide(request.getLiftRide());
        });

        log.info("Processed batch of size: {}", batch.size());
      } catch (Exception e) {
        log.error("Error processing batch", e);
      }
    }
  }

  public void shutdown() throws IOException, TimeoutException {
    if (channelPool != null) {
      channelPool.close();
    }

    if (connection != null) {
      connection.close();
    }

    executorService.shutdown();
    batchExecutor.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
      if (!batchExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
        batchExecutor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      batchExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}