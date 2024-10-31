package io.swagger;

import io.swagger.configuration.RabbitMQConfig;
import io.swagger.model.LiftRide;
import io.swagger.model.LiftRideRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class RabbitMQSender {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Async
  public void sendLiftRide(LiftRideRequest liftRideRequest) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, liftRideRequest);
  }
}