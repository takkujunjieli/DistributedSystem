package org.distributed;

import java.util.List;

public class DatabaseTest {
  public static void main(String[] args) {
    LiftRideDAO dao = new LiftRideDAO();

    // Test Insert
    LiftRide liftRide = new LiftRide(10, 5000);
    LiftRideRequest request = new LiftRideRequest(1, "spring", "30", 455, liftRide);
    dao.saveToDatabase(List.of(request));

    System.out.println("Database connection and insert verified!");
  }
}

