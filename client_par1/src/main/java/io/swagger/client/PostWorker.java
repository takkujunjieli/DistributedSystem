package io.swagger.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;

public class PostWorker implements Runnable {

    public static final String BASE_PATH = "http://54.200.173.2:8080/skiers";
    private final BlockingQueue<SkierLiftRideEvent> eventQueue;
    private final AtomicInteger completedRequests;
    private final AtomicInteger failedRequests;
    private static final int MAX_RETRIES = 5;

    private final HttpClient client;

    public PostWorker(BlockingQueue<SkierLiftRideEvent> eventQueue, AtomicInteger completedRequests,
            AtomicInteger failedRequests) {
        this.eventQueue = eventQueue;
        this.completedRequests = completedRequests;
        this.failedRequests = failedRequests;
        this.client = HttpClient.newHttpClient(); // Initialize the HttpClient
    }

    @Override
    public void run() {
        while (true) {
            try {
                SkierLiftRideEvent event = eventQueue.take(); // Blocking until an event is available

                String jsonPayload = createJsonPayload(event);

                int attempt = 0;
                boolean success = false;

                while (attempt < MAX_RETRIES && !success) {
                    try {
                        // Send the POST request asynchronously using HttpClient
                        success = sendPostRequest(jsonPayload, event, attempt);

                        if (success) {
                            System.out.println("Success! HTTP 201 for Skier ID: " + event.getSkierID());
                            completedRequests.incrementAndGet();
                        } else {
                            // Log detailed info for failed requests
                            System.err.println("Failed to POST for Skier ID: " + event.getSkierID()
                                    + ". Status code not 201. Retrying (" + (attempt + 1) + "/" + MAX_RETRIES + ")");
                        }

                    } catch (Exception e) {
                        attempt++;
                        System.err.println("Failed to POST for Skier ID: " + event.getSkierID()
                                + ". Retrying (" + attempt + "/" + MAX_RETRIES + ")");
                        System.err.println("Exception: " + e.getMessage()); // Log the exception message
                        try {
                            Thread.sleep(1000 * attempt); // Exponential backoff
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }

                if (!success) {
                    failedRequests.incrementAndGet();
                    System.err.println("Failed to POST event for Skier ID: " + event.getSkierID() + " after "
                            + MAX_RETRIES + " attempts.");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("PostWorker interrupted");
                return;
            }
        }
    }

    // Helper method to send POST request
    private boolean sendPostRequest(String jsonPayload, SkierLiftRideEvent event, int attempt) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                // Correct the URL path structure to match the server's mapping
                .uri(URI.create(BASE_PATH + "/" + event.getResortID() + "/seasons/" + event.getSeasonID()
                        + "/days/" + event.getDayID() + "/skiers/" + event.getSkierID()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        // Sending request asynchronously
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        // Block until response is received and log the status code
        HttpResponse<String> httpResponse = response.get();

        // Log the response body and status code for debugging
        System.out.println("Response for Skier ID: " + event.getSkierID() + " - Status: " + httpResponse.statusCode());
        System.out.println("Response body: " + httpResponse.body());

        return httpResponse.statusCode() == 201; // Check if the response is HTTP 201 Created
    }

    // A helper method to convert the SkierLiftRideEvent into a JSON string
    private String createJsonPayload(SkierLiftRideEvent event) {
        // Convert event into JSON string
        return String.format(
                "{\"liftID\": %d, \"time\": %d, \"resortID\": %d, \"seasonID\": %d, \"dayID\": %d, \"skierID\": %d}",
                event.getLiftID(), event.getTime(), event.getResortID(), event.getSeasonID(), event.getDayID(),
                event.getSkierID());
    }
}
