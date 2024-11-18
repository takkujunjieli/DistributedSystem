package org.distributed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiftRideDAO {
  private static final Logger log = LoggerFactory.getLogger(LiftRideDAO.class);
  private static final String URL = "jdbc:mysql://localhost:3306/skierdb";
  private static final String USER = "root";
  private static final String PASSWORD = "password";

  public void saveToDatabase(LiftRideRequest request) {
    String query = "INSERT INTO lift_rides (resort_id, season_id, day_id, skier_id, lift_id, time) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setInt(1, request.getResortID());
      stmt.setString(2, request.getSeasonID());
      stmt.setString(3, request.getDayID());
      stmt.setInt(4, request.getSkierID());
      stmt.setInt(5, request.getLiftRide().getLiftID());
      stmt.setInt(6, request.getLiftRide().getTime());

      stmt.executeUpdate();
    } catch (SQLException e) {
      log.error("Error saving to database", e);
    }
  }
}