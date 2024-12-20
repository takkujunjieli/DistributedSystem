package org.distributed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.dbcp2.BasicDataSource;

public class LiftRideDAO {

  private static final Logger log = LoggerFactory.getLogger(LiftRideDAO.class);
  private static final BasicDataSource dataSource = DBCPDataSource.getDataSource();

  public void saveToDatabase(List<LiftRideRequest> requests) {
    String query = "INSERT INTO SkierLiftRides (resort_id, season_id, day_id, skier_id, lift_id, time) VALUES (?, ?, ?, ?, ?, ?)";
    long startTime = System.currentTimeMillis(); // Start timing
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {

      for (LiftRideRequest request : requests) {
        stmt.setInt(1, request.getResortID());
        stmt.setString(2, request.getSeasonID());
        stmt.setString(3, request.getDayID());
        stmt.setInt(4, request.getSkierID());
        stmt.setInt(5, request.getLiftRide().getLiftID());
        stmt.setInt(6, request.getLiftRide().getTime());
        stmt.addBatch();
      }

      int[] result = stmt.executeBatch();
      long endTime = System.currentTimeMillis(); // End timing
      // Log metrics
      System.out.println("Inserted " + result.length + " records into the database.");
      System.out.println("Time taken: " + (endTime - startTime) + " ms");
      System.out.println("Throughput: " + (result.length * 1000.0 / (endTime - startTime)) + " records/second");    } catch (SQLException e) {
      throw new RuntimeException("Error saving to database", e);
    }
  }
}
