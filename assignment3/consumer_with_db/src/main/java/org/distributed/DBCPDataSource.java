package org.distributed;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBCPDataSource {
  private static BasicDataSource dataSource;

  // Static block to initialize the DataSource
  static {
    try {
      Properties properties = loadProperties();

      // Load MySQL connection details from properties
      String hostName = properties.getProperty("MySQL_IP_ADDRESS");
      String port = properties.getProperty("MySQL_PORT");
      String database = properties.getProperty("DB_NAME");
      String username = properties.getProperty("DB_USERNAME");
      String password = properties.getProperty("DB_PASSWORD");

      // Load connection pool configurations
      int initialSize = Integer.parseInt(properties.getProperty("dbcp2.initialSize", "5"));
      int maxTotal = Integer.parseInt(properties.getProperty("dbcp2.maxTotal", "50"));

      // Construct the JDBC URL
      String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", hostName, port, database);

      // Initialize the BasicDataSource
      dataSource = new BasicDataSource();
      dataSource.setUrl(url);
      dataSource.setUsername(username);
      dataSource.setPassword(password);
      dataSource.setInitialSize(initialSize);
      dataSource.setMaxTotal(maxTotal);

      // Ensure MySQL JDBC driver is loaded
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize DataSource", e);
    }
  }

  // Static method to load properties
  private static Properties loadProperties() throws IOException {
    Properties properties = new Properties();
    try (InputStream input = DBCPDataSource.class.getClassLoader().getResourceAsStream("application.properties")) {
      if (input == null) {
        throw new IOException("application.properties file is missing.");
      }
      properties.load(input);
    } catch (IOException ex) {
      throw new IOException("Error loading application.properties", ex);
    }
    return properties;
  }

  // Public method to retrieve the DataSource
  public static BasicDataSource getDataSource() {
    return dataSource;
  }
}




//package org.distributed;
//
//import java.io.IOException;
//import java.io.InputStream;
//import org.apache.commons.dbcp2.*;
//import java.util.Properties;
//
//public class DBCPDataSource {
//  private static BasicDataSource dataSource;
//  Properties properties = loadProperties();
//
//  // NEVER store sensitive information below in plain text!
//  private static final String HOST_NAME = properties.getProperty("MySQL_IP_ADDRESS");
//  private static final String PORT = properties.getProperty("MySQL_PORT");
//  private static final String DATABASE = properties.getProperty("DB_NAME");
//  private static final String USERNAME = properties.getProperty("DB_USERNAME");
//  private static final String PASSWORD = properties.getProperty("DB_PASSWORD");
//  private static final String INITIAL_SIZE = properties.getProperty("dbcp2.initialSize");
//  private static final String MAX_TOTAL = properties.getProperty("dbcp2.maxTotal");
//
//  static {
//    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
//    dataSource = new BasicDataSource();
//    try {
//      Class.forName("com.mysql.cj.jdbc.Driver");
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
//    String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
//    dataSource.setUrl(url);
//    dataSource.setUsername(USERNAME);
//    dataSource.setPassword(PASSWORD);
//    dataSource.setInitialSize(Integer.parseInt(INITIAL_SIZE));
//    dataSource.setMaxTotal(Integer.parseInt(MAX_TOTAL));
//  }
//
//  public DBCPDataSource() throws IOException {
//  }
//
//  private Properties loadProperties() throws IOException {
//    Properties properties = new Properties();
//    try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
//      if (input == null) {
//        throw new IOException("application.properties file is missing.");
//      }
//      properties.load(input);
//    } catch (IOException ex) {
//      throw new IOException("Error loading application.properties", ex);
//    }
//    return properties;
//  }
//
//  public static BasicDataSource getDataSource() {
//    return dataSource;
//  }
//}
