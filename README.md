To locate deployed server file:

- spring-server-generated/target/swagger-spring-1.0.0.jar is the deployed file for server as I use Spring boot.

To run both clients:

- You may change the Url (`private static final String BASE_URL = "http://54.200.173.2:8080";`) in  `src\main\java\io\swagger\client\PostWorker.java`

- `mvn clean package`

- `java -jar target/swagger-java-client-1.0.0.jar`
