package org.distributed;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

public class RabbitMQConfig {
  private static final String HOST = "your-rabbitmq-host";
  private static final String QUEUE_NAME = "your-queue-name";

  public static Connection getConnection() throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    return factory.newConnection();
  }

  public static String getQueueName() {
    return QUEUE_NAME;
  }
}

