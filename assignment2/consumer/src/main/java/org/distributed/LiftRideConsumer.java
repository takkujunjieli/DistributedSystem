package org.distributed;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiftRideConsumer {
  private static final Logger log = LoggerFactory.getLogger(LiftRideConsumer.class);
  private final ConcurrentHashMap<Integer, LiftRideRecord> skierRecords;
  private final ConnectionFactory factory;
  private final String queueName;
  private final int numThreads;
  private final ExecutorService executorService;
  private final ObjectMapper objectMapper;
  private Connection connection;
  private ChannelPool channelPool;

  public LiftRideConsumer() {
    this.skierRecords = new ConcurrentHashMap<>();
    Properties properties = loadProperties();

    String host = properties.getProperty("rabbitmq.host");
    this.queueName = properties.getProperty("rabbitmq.queue");
    this.numThreads = Integer.parseInt(properties.getProperty("consumer.numThreads"));
    this.executorService = Executors.newFixedThreadPool(numThreads);
    this.objectMapper = new ObjectMapper();

    factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setUsername("guest");
    factory.setPassword("guest");
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
        channel.basicConsume(queueName, true, consumer);
      } catch (IOException e) {
        log.error("Error in consumer worker", e);
      } finally {
        if (channel != null) {
          channelPool.returnChannel(channel);
        }
      }
    }
  }

//  private Consumer createConsumer(Channel channel) {
//    return new DefaultConsumer(channel) {
//      @Override
//      public void handleDelivery(String consumerTag, Envelope envelope,
//          AMQP.BasicProperties properties, byte[] body)
//          throws IOException {
//        try {
//          LiftRideRequest request = objectMapper.readValue(body, LiftRideRequest.class);
//          processLiftRide(request);
//
//          channel.basicAck(envelope.getDeliveryTag(), false);
//          log.debug("Processed message for skier: {}", request.getSkierID());
//        } catch (Exception e) {
//          log.error("Error processing message", e);
//          channel.basicNack(envelope.getDeliveryTag(), false, true);
//        }
//      }
//    };
//  }
  private Consumer createConsumer(Channel channel) {
    return new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
          AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        try {
          LiftRideRequest request = objectMapper.readValue(body, LiftRideRequest.class);
          processLiftRide(request);

          // Acknowledge after processing
          //            channel.basicAck(envelope.getDeliveryTag(), false);
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


  private void processLiftRide(LiftRideRequest request) {
    skierRecords.computeIfAbsent(request.getSkierID(), k -> new LiftRideRecord())
        .addRide(request.getLiftRide());
  }

  public void shutdown() throws IOException, TimeoutException {
    if (channelPool != null) {
      channelPool.close();
    }

    if (connection != null) {
      try {
        connection.close();
      } catch (IOException e) {
        log.error("Error closing connection", e);
      }
    }

    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}