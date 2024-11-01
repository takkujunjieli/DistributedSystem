package io.swagger;

import io.swagger.configuration.RabbitMQConfig;
import io.swagger.model.LiftRide;
import io.swagger.model.LiftRideRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQSender {

  @Autowired
  private RabbitTemplate rabbitTemplate;
  private static final Logger log = LoggerFactory.getLogger(RabbitMQSender.class);

  @Value("${rabbitmq.queue.name}")
  private String queueName;

  public void sendLiftRide(LiftRideRequest liftRideRequest) {
    log.info("Sending message to queue: {}", queueName);
    rabbitTemplate.convertAndSend(queueName, liftRideRequest);
    log.info("Message sent: {}", liftRideRequest);
  }

//  public void sendLiftRide(String testMessage) {
//    rabbitTemplate.convertAndSend(queueName, testMessage);
//  }
}