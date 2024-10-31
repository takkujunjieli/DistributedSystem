package org.distributed;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.ObjectMapper;



public class LiftRideConsumer {
  private final SkierRecords skierRecords;
  private final int THREAD_POOL_SIZE = 10;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public LiftRideConsumer(SkierRecords skierRecords) {
    this.skierRecords = skierRecords;
  }

  public void startConsuming() throws Exception {
    Connection connection = RabbitMQConfig.getConnection();
    Channel channel = connection.createChannel();
    channel.queueDeclare(RabbitMQConfig.getQueueName(), true, false, false, null);

    ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      executorService.submit(() -> {
        try {
          String message = new String(delivery.getBody(), "UTF-8");
          LiftRideRequest liftRideRequest = deserialize(message); // implement deserialization logic
          skierRecords.addLiftRide(liftRideRequest.getSkierID(), liftRideRequest.getLiftRide());
          System.out.println("Stored lift ride for skierID: " + liftRideRequest.getSkierID());
        } catch (Exception e) {
          System.err.println("Failed to process message: " + e.getMessage());
        }
      });
    };

    channel.basicConsume(RabbitMQConfig.getQueueName(), true, deliverCallback, consumerTag -> {});
  }

  private LiftRideRequest deserialize(String message) throws Exception {
    return objectMapper.readValue(message, LiftRideRequest.class);
  }
}
