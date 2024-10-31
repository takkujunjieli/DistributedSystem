import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.distributed.model.LiftRide;

public class LiftRideConsumer {
  private final SkierRecords skierRecords;
  private final int THREAD_POOL_SIZE = 10;

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
          LiftRide liftRide = deserialize(message); // implement deserialization logic
          skierRecords.addLiftRide(liftRide.getSkierID(), liftRide);
          System.out.println("Stored lift ride for skierID: " + liftRide.getSkierID());
        } catch (Exception e) {
          System.err.println("Failed to process message: " + e.getMessage());
        }
      });
    };

    channel.basicConsume(RabbitMQConfig.getQueueName(), true, deliverCallback, consumerTag -> {});
  }

  private LiftRide deserialize(String message) {
    // Implement JSON deserialization logic here
    return new LiftRide(); // replace with actual deserialization
  }
}
