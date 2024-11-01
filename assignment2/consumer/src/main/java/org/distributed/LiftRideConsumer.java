package org.distributed;

import com.rabbitmq.client.*;
import java.io.IOException;
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

  public LiftRideConsumer(String host, String queueName, int numThreads) {
    this.skierRecords = new ConcurrentHashMap<>();
    this.queueName = queueName;
    this.numThreads = numThreads;
    this.executorService = Executors.newFixedThreadPool(numThreads);
    this.objectMapper = new ObjectMapper();

    factory = new ConnectionFactory();
    factory.setHost(host);
  }

  public void startConsuming() {
    try {
      Connection connection = factory.newConnection(executorService);

      for (int i = 0; i < numThreads; i++) {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        // Limit the number of unacknowledged messages per consumer
        channel.basicQos(10);

        Consumer consumer = createConsumer(channel);
        channel.basicConsume(queueName, false, consumer);

        log.info("Started consumer thread {}", i + 1);
      }
    } catch (IOException | TimeoutException e) {
      log.error("Error starting consumer", e);
      shutdown();
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
          processLiftRide(request);

          // Acknowledge the message
          channel.basicAck(envelope.getDeliveryTag(), false);
          log.debug("Processed message for skier: {}", request.getSkierID());
        } catch (Exception e) {
          log.error("Error processing message", e);
          // Reject the message and requeue it
          channel.basicNack(envelope.getDeliveryTag(), false, true);
        }
      }
    };
  }

  private void processLiftRide(LiftRideRequest request) {
    skierRecords.computeIfAbsent(request.getSkierID(), k -> new LiftRideRecord())
        .addRide(request.getLiftRide());
  }

//  public LiftRideRecord getSkierRecord(Integer skierId) {
//    return skierRecords.get(skierId);
//  }

  public void shutdown() {
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