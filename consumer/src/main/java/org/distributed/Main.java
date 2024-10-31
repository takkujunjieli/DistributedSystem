package org.distributed;

public class Main {
  public static void main(String[] args) {
    SkierRecords skierRecords = new SkierRecords();
    LiftRideConsumer consumer = new LiftRideConsumer(skierRecords);

    try {
      consumer.startConsuming();
    } catch (Exception e) {
      System.err.println("Error starting consumer: " + e.getMessage());
    }
  }
}
