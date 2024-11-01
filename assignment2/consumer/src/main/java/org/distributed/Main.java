package org.distributed;

public class Main {
  public static void main(String[] args) {
    int numThreads = 10;
    LiftRideConsumer consumer = new LiftRideConsumer("35.88.229.59", "test", numThreads);

    // Add shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(consumer::shutdown));

    consumer.startConsuming();
  }
}