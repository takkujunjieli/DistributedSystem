package io.swagger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class ChannelPool {
  private static final Logger log = LoggerFactory.getLogger(ChannelPool.class);
  private final BlockingQueue<Channel> pool;
  private final Connection connection;
  private final String queueName;
//  private final String exchangeName = "liftRideExchange";

  public ChannelPool(String host, int poolSize, String queueName) throws IOException, TimeoutException {
    this.pool = new LinkedBlockingQueue<>(poolSize);
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host);
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setPort(5672);
    this.connection = factory.newConnection();
    this.queueName = queueName;

    init(poolSize);
  }

  private void init(int poolSize) throws IOException {
    for (int i = 0; i < poolSize; i++) {
      Channel channel = connection.createChannel();
      // Declare exchange for routing messages
      channel.exchangeDeclare("myExchange", "direct", true);

      // Declare and bind server queue
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, "myExchange", "consumerKey");

//      channel.queueDeclare("consumerQueue", true, false, false, null);
//      channel.queueBind("consumerQueue", "myExchange", "consumerKey");

      pool.add(channel);
    }
    log.info("Initialized channel pool with {} channels.", poolSize);
  }

  public Channel borrowChannel() throws InterruptedException {
    return pool.take();
  }

  public void returnChannel(Channel channel) {
    if (channel != null) {
      pool.offer(channel);
    }
  }

  public void close() throws IOException, TimeoutException {
    for (Channel channel : pool) {
      channel.close();
    }
    connection.close();
  }
}
