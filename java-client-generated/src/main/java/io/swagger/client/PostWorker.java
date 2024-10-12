package io.swagger.client;

import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PostWorker implements Runnable {

    private final BlockingQueue<SkierLiftRideEvent> eventQueue;
    private final AtomicInteger completedRequests;
    private final AtomicInteger failedRequests;
    private static final int MAX_RETRIES = 5;

    public PostWorker(BlockingQueue<SkierLiftRideEvent> eventQueue, AtomicInteger completedRequests,
            AtomicInteger failedRequests) {
        this.eventQueue = eventQueue;
        this.completedRequests = completedRequests;
        this.failedRequests = failedRequests;
    }

    @Override
    public void run() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(SkiersApiMultithreadedClient.BASE_PATH); // Use BASE_PATH from main class
        SkiersApi skiersApi = new SkiersApi(apiClient);

        while (true) {
            try {
                SkierLiftRideEvent event = eventQueue.take(); // Blocking until an event is available

                LiftRide liftRide = new LiftRide();
                liftRide.setLiftID(event.getLiftID());
                liftRide.setTime(event.getTime());

                int attempt = 0;
                boolean success = false;

                while (attempt < MAX_RETRIES && !success) {
                    try {
                        // Send the POST request
                        skiersApi.writeNewLiftRide(
                                liftRide,
                                event.getResortID(),
                                String.valueOf(event.getSeasonID()),
                                String.valueOf(event.getDayID()),
                                event.getSkierID());

                        // If successful
                        System.out.println("Success! HTTP 201 for Skier ID: " + event.getSkierID());
                        completedRequests.incrementAndGet();
                        success = true;

                    } catch (ApiException e) {
                        int statusCode = e.getCode();
                        if ((statusCode >= 400 && statusCode < 500) || (statusCode >= 500 && statusCode < 600)) {
                            attempt++;
                            System.err.println("Failed to POST for Skier ID: " + event.getSkierID() +
                                    ". Status code: " + statusCode + ". Retrying (" + attempt + "/" + MAX_RETRIES
                                    + ")");
                            try {
                                Thread.sleep(1000 * attempt); // Exponential backoff
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                return;
                            }

                        } else {
                            // Non-retriable error
                            System.err.println("Non-retriable error for Skier ID: " + event.getSkierID()
                                    + ". Status code: " + statusCode);
                            break;
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
}
