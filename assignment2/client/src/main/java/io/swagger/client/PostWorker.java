package io.swagger.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

public class PostWorker implements Runnable {
    private final BlockingQueue<SkierLiftRideEvent> eventQueue;
    private final int requestsToSend;
    private static final int MAX_RETRIES = 5;
    private final ConcurrentLinkedQueue<RequestStatistics> statistics;

    private static final AtomicInteger totalCompleted = new AtomicInteger(0);
    private static final AtomicInteger totalFailed = new AtomicInteger(0);

    public PostWorker(BlockingQueue<SkierLiftRideEvent> eventQueue, int requestsToSend, ConcurrentLinkedQueue<RequestStatistics> statistics) {
        this.eventQueue = eventQueue;
        this.requestsToSend = requestsToSend;
        this.statistics = statistics;
    }

    @Override
    public void run() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(Client.BASE_URL);
        SkiersApi skiersApi = new SkiersApi(apiClient);

        try {
            for (int i = 0; i < requestsToSend; i++) {
                SkierLiftRideEvent event = eventQueue.take();
                LiftRide liftRide = new LiftRide();
                liftRide.setLiftID(event.getLiftID());
                liftRide.setTime(event.getTime());

                int attempt = 0;
                boolean success = false;
                long startTime = System.currentTimeMillis();
                int responseCode = 0;

                while (attempt < MAX_RETRIES && !success) {
                    try {
                        skiersApi.writeNewLiftRide(
                            liftRide, event.getResortID(),
                            String.valueOf(event.getSeasonID()),
                            String.valueOf(event.getDayID()),
                            event.getSkierID()
                        );
                        totalCompleted.incrementAndGet();
                        success = true;
                        responseCode = 200; // Assuming 200 for successful requests
                    } catch (ApiException e) {
                        attempt++;
                        responseCode = e.getCode();
                        if (attempt >= MAX_RETRIES) {
                            totalFailed.incrementAndGet();
                        }
                        Thread.sleep(1000L * attempt);  // Exponential backoff
                    }
                }

                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;

                statistics.add(new RequestStatistics(startTime, "POST", latency, responseCode));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static int getTotalCompleted() {
        return totalCompleted.get();
    }

    public static int getTotalFailed() {
        return totalFailed.get();
    }
}



