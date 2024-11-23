package org.distributed;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import java.util.concurrent.*;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiftRideConsumer {
  private static final Logger log = LoggerFactory.getLogger(LiftRideConsumer.class);
  private final ConcurrentHashMap<Integer, LiftRideRecord> skierRecords;
  private final ConnectionFactory factory;
  private final String queueName;
  private final int numThreads;
  private final ExecutorService executorService;
  private final ObjectMapper objectMapper;
  private final AtomicInteger counter = new AtomicInteger(0);
  private final List<BlockingQueue<LiftRideRequest>> batchQueues;
  private final int batchSize;
  private final List<ScheduledExecutorService> batchExecutors;
  private final AtomicLong totalRecordsInserted = new AtomicLong(0);
  private final ScheduledExecutorService statsExecutor = Executors.newSingleThreadScheduledExecutor();
  private final int queueSize;
  private final AtomicInteger[] queueMessageCounts;

  private com.rabbitmq.client.Connection connection;
  private ChannelPool channelPool;
  private LiftRideDAO liftRideDAO = new LiftRideDAO();

  public LiftRideConsumer() {
    this.skierRecords = new ConcurrentHashMap<>();
    Properties properties = loadProperties();

    String host = properties.getProperty("rabbitmq.host");
    this.queueName = properties.getProperty("rabbitmq.queue");
    this.numThreads = Integer.parseInt(properties.getProperty("consumer.numThreads"));
    this.executorService = Executors.newFixedThreadPool(numThreads);
    this.objectMapper = new ObjectMapper();
    this.batchSize = Integer.parseInt(properties.getProperty("consumer.batchSize"));
    this.queueSize = Integer.parseInt(properties.getProperty("consumer.queueSize"));
    int numBatchProcessors = Integer.parseInt(properties.getProperty("consumer.numBatchProcessors"));
    this.batchQueues = new ArrayList<>(numBatchProcessors);
    this.batchExecutors = new ArrayList<>(numBatchProcessors);
    this.statsExecutor.scheduleAtFixedRate(this::logThroughput, 10, 10, TimeUnit.SECONDS);
    this.statsExecutor.scheduleAtFixedRate(this::logQueueDistribution, 10, 10, TimeUnit.SECONDS);
    this.queueMessageCounts = new AtomicInteger[numBatchProcessors];

    for (int i = 0; i < numBatchProcessors; i++) {
      batchQueues.add(new LinkedBlockingQueue<>(queueSize));
      queueMessageCounts[i] = new AtomicInteger(0);
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      batchExecutors.add(executor);
      final int queueIndex = i;
      executor.scheduleAtFixedRate(
          () -> processBatch(queueIndex),
          100, // Initial delay
          100, // Period in milliseconds
          TimeUnit.MILLISECONDS
      );
    }

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
      log.info("Number of batch queues: {}", batchQueues.size());
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
          AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
//          LiftRideRequest request = objectMapper.readValue(body, LiftRideRequest.class);
//          int queueIndex = counter.getAndIncrement() % batchQueues.size();
//          queueMessageCounts[queueIndex].incrementAndGet(); // Update count
//          if (!batchQueues.get(queueIndex).offer(request, 100, TimeUnit.MILLISECONDS)) {
//            log.warn("Queue {} is full, message processing delayed", queueIndex);
//          }
          channel.basicAck(envelope.getDeliveryTag(), false);
        } catch (Exception e) {
          log.error("Error processing message", e);
          channel.basicNack(envelope.getDeliveryTag(), false, true);
        }
      }
    };
  }


  private void processBatch(int queueIndex) {
    List<LiftRideRequest> batch = new ArrayList<>(batchSize);
    BlockingQueue<LiftRideRequest> queue = batchQueues.get(queueIndex);
    queue.drainTo(batch, batchSize);

    if (!batch.isEmpty()) {
      try {
        liftRideDAO.saveToDatabase(batch);
        batch.forEach(request -> {
          skierRecords.computeIfAbsent(request.getSkierID(), k -> new LiftRideRecord())
              .addRide(request.getLiftRide());
        });

        totalRecordsInserted.addAndGet(batch.size());
      } catch (Exception e) {
        log.error("Error processing batch", e);
      }
    }
  }

  private void logThroughput() {
    long records = totalRecordsInserted.getAndSet(0);
    System.out.println("Database Throughput: " + records / 10.0 + " records/second");
  }

  private void logQueueDistribution() {
    StringBuilder stats = new StringBuilder("Queue Message Distribution: ");
    for (int i = 0; i < queueMessageCounts.length; i++) {
      stats.append(String.format("Queue %d: %d messages; ", i, queueMessageCounts[i].get()));
    }
    log.info(stats.toString());
  }


  public void shutdown() throws IOException, TimeoutException {
    if (channelPool != null) {
      channelPool.close();
    }

    if (connection != null) {
      connection.close();
    }

    // Drain and process remaining messages in the queue
    List<LiftRideRequest> remainingBatch = new ArrayList<>();
    for (BlockingQueue<LiftRideRequest> queue : batchQueues) {
      queue.drainTo(remainingBatch);
    }
    if (!remainingBatch.isEmpty()) {
      log.info("Processing remaining batch of size: {}", remainingBatch.size());
      liftRideDAO.saveToDatabase(remainingBatch);
    }

    executorService.shutdown();
    for (BlockingQueue<LiftRideRequest> queue : batchQueues) {
      queue.remove();
    }
    statsExecutor.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
      if (!statsExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
        statsExecutor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      statsExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

}





