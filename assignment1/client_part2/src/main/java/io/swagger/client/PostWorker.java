package io.swagger.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class PostWorker implements Runnable {

//    private static final String BASE_URL = "http://52.26.163.233:8080";
    private static final String BASE_URL = "http://Assignment2-164771622.us-west-2.elb.amazonaws.com";
    private final BlockingQueue<SkierLiftRideEvent> eventQueue;
    private final AtomicInteger completedRequests;
    private final AtomicInteger failedRequests;
    private final ConcurrentLinkedQueue<String> logEntries;
    private final CountDownLatch latch;
    private final HttpClient client;
    private static final int MAX_RETRIES = 2;

    public PostWorker(BlockingQueue<SkierLiftRideEvent> eventQueue,
            AtomicInteger completedRequests,
            AtomicInteger failedRequests,
            ConcurrentLinkedQueue<String> logEntries,
            CountDownLatch latch) {
        this.eventQueue = eventQueue;
        this.completedRequests = completedRequests;
        this.failedRequests = failedRequests;
        this.logEntries = logEntries;
        this.latch = latch;
        this.client = HttpClient.newHttpClient(); // Initialize the HttpClient
    }

    @Override
    public void run() {
        try {
            while (true) {
                SkierLiftRideEvent event = eventQueue.take(); // Blocking until an event is available
                String jsonPayload = createJsonPayload(event);

                int attempt = 0;
                boolean success = false;

                while (attempt < MAX_RETRIES && !success) {
                    try {
                        // Capture start time
                        long startTime = System.currentTimeMillis();

                        // Send the POST request
                        HttpResponse<String> response = sendPostRequest(jsonPayload, event);

                        // Capture end time and calculate latency
                        long endTime = System.currentTimeMillis();
                        long latency = endTime - startTime;

                        // Log to the concurrent queue for CSV writing
                        logEntries.add(startTime + ",POST," + latency + "," + response.statusCode());

                        if (response.statusCode() == 201) {
                            success = true;
                            completedRequests.incrementAndGet();
                        } else {
                            failedRequests.incrementAndGet();
                        }
                    } catch (Exception e) {
                        attempt++;
                    }
                }

                // Count down the latch after processing the event
                latch.countDown();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

//    @Override
//    public void run() {
//        try {
//            while (true) {
//                SkierLiftRideEvent event = eventQueue.take(); // Blocking until an event is available
//                String jsonPayload = createJsonPayload(event);
//
//                long startTime = System.currentTimeMillis();
//                sendPostRequest(jsonPayload, event)
//                    .thenAccept(response -> {
//                        // Capture end time and calculate latency
//                        long latency = System.currentTimeMillis() - startTime;
//
//                        // Log to the concurrent queue for CSV writing
//                        logEntries.add(startTime + ",POST," + latency + "," + response.statusCode());
//
//                        if (response.statusCode() == 201) {
//                            completedRequests.incrementAndGet();
//                        } else {
//                            failedRequests.incrementAndGet();
//                        }
//
//                        // Count down the latch after processing the event
//                        latch.countDown();
//                    })
//                    .exceptionally(ex -> {
//                        // Handle exceptions and increment failed requests
//                        failedRequests.incrementAndGet();
//                        latch.countDown();
//                        return null;
//                    });
//            }
//        } catch (Exception e) {
//            Thread.currentThread().interrupt();
//        }
//    }

    private HttpResponse<String> sendPostRequest(String jsonPayload, SkierLiftRideEvent event) throws Exception {
        // Construct the correct URI using the provided parameters
        String endpoint = String.format(
                "/skiers/%d/seasons/%s/days/%d/skiers/%d",
                event.getResortID(), event.getSeasonID(), event.getDayID(), event.getSkierID());

        // Build the HTTP request with the correct Content-Type header
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json") // Add this header
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String createJsonPayload(SkierLiftRideEvent event) {
        // Create a JSON payload with lift ride details
        return String.format("{\"liftID\": %d, \"time\": %d}", event.getLiftID(), event.getTime());
    }
}
