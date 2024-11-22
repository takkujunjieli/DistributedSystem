package org.distributed;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.distributed.model")  // Scan for JPA entities in the model package
public class Main {
  public static void main(String[] args) throws IOException, TimeoutException {
    LiftRideConsumer consumer = new LiftRideConsumer();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        consumer.shutdown();
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (TimeoutException e) {
        throw new RuntimeException(e);
      }
    }));

    consumer.startConsuming();
  }
}

