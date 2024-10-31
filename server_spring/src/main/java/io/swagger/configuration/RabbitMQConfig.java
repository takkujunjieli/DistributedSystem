package io.swagger.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String QUEUE_NAME = "liftRideQueue";

  @Bean
  public Queue liftRideQueue() {
    return new Queue(QUEUE_NAME, true); // Durable queue
  }
}
