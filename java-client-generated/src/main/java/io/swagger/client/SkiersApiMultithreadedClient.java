package io.swagger.client;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SkiersApiMultithreadedClient {

    public static final String BASE_PATH = "http://ec2-34-229-59-108.compute-1.amazonaws.com";
    private static final int TOTAL_EVENTS = 2000;
    public static final int EVENTS_PER_THREAD = 1000;
    private static final int INITIAL_THREADS = 2;
    private static final int QUEUE_CAPACITY = 5000;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger completedRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        Thread eventGeneratorThread = new Thread(new EventGenerator(eventQueue, TOTAL_EVENTS));
        eventGeneratorThread.start();

        ExecutorService postingExecutor = Executors.newFixedThreadPool(INITIAL_THREADS);

        for (int i = 0; i < INITIAL_THREADS; i++) {
            postingExecutor.submit(new PostWorker(eventQueue, completedRequests, failedRequests));
        }

        eventGeneratorThread.join();

        while (completedRequests.get() + failedRequests.get() < TOTAL_EVENTS) {
            // No need to submit new PostWorker tasks; they are continuously polling from
            // the queue
            Thread.sleep(100); // Brief pause to prevent tight loop
        }

        postingExecutor.shutdownNow();
        postingExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        long endTime = System.currentTimeMillis();
        long totalRunTime = endTime - startTime;
        double totalRunTimeSeconds = totalRunTime / 1000.0;

        System.out.println("Number of successful requests: " + completedRequests.get());
        System.out.println("Number of unsuccessful requests: " + failedRequests.get());
        System.out.println("Total run time (wall time): " + totalRunTimeSeconds + " seconds");
        System.out.println("Throughput: " + (TOTAL_EVENTS / totalRunTimeSeconds) + " requests/second");
    }
}
