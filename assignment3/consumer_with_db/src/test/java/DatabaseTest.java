import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;
import org.distributed.DBCPDataSource;
import org.distributed.LiftRide;
import org.distributed.LiftRideDAO;
import org.distributed.LiftRideRequest;

public class DatabaseTest {
  public static void main(String[] args) {
    try {
      BasicDataSource dataSource = DBCPDataSource.getDataSource();
      System.out.println("Database connection successful!");
    } catch (Exception e) {
      e.printStackTrace();
    }
    LiftRideDAO dao = new LiftRideDAO();

    // Test Insert
    LiftRide liftRide = new LiftRide(10, 5000);
    LiftRideRequest request = new LiftRideRequest(1, "1", "30", 455, liftRide);
    dao.saveToDatabase(List.of(request));

    System.out.println("Database connection and insert verified!");
  }
}

