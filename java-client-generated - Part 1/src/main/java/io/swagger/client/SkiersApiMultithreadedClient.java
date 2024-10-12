package io.swagger.client;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SkiersApiMultithreadedClient {

    private static final int TOTAL_EVENTS = 10000;
    public static final int EVENTS_PER_THREAD = 10000;
    private static final int INITIAL_THREADS = 1;
    private static final int QUEUE_CAPACITY = 5000;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger completedRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // Start the EventGenerator in its own thread
        Thread eventGeneratorThread = new Thread(new EventGenerator(eventQueue, TOTAL_EVENTS));
        eventGeneratorThread.start();

        // Initialize a thread pool for posting events
        ExecutorService postingExecutor = Executors.newFixedThreadPool(INITIAL_THREADS);

        // Submit PostWorker tasks to the thread pool
        for (int i = 0; i < INITIAL_THREADS; i++) {
            postingExecutor.submit(new PostWorker(eventQueue, completedRequests, failedRequests));
        }

        // Wait for event generation to complete
        eventGeneratorThread.join();

        // Wait for all events to be processed
        while (completedRequests.get() + failedRequests.get() < TOTAL_EVENTS) {
            Thread.sleep(100); // Brief pause to prevent tight loop
        }

        postingExecutor.shutdownNow();
        postingExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        long endTime = System.currentTimeMillis();
        long totalRunTime = endTime - startTime;
        double totalRunTimeSeconds = totalRunTime / 1000.0;

        // Print performance metrics
        System.out.println("Number of successful requests: " + completedRequests.get());
        System.out.println("Number of unsuccessful requests: " + failedRequests.get());
        System.out.println("Total run time (wall time): " + totalRunTimeSeconds + " seconds");
        System.out.println("Throughput: " + (TOTAL_EVENTS / totalRunTimeSeconds) + " requests/second");
    }
}
