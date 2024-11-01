package io.swagger.client;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SkiersApiMultithreadedClient {

    private static final int TOTAL_EVENTS = 200000;
    private static final int INITIAL_THREADS = 32;
    private static final int QUEUE_CAPACITY = 5000;
    private static final String CSV_FILE_PREFIX = "performance_log_";

    public static void main(String[] args) throws InterruptedException, IOException {
        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        AtomicInteger completedRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);
        ConcurrentLinkedQueue<String> logEntries = new ConcurrentLinkedQueue<>();
        CountDownLatch latch = new CountDownLatch(TOTAL_EVENTS);

        // Generate unique CSV file name with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String csvFile = CSV_FILE_PREFIX + timestamp + ".csv";

        // Capture start time before any threads are started
        long startTime = System.currentTimeMillis();

        // Start the EventGenerator in its own thread
        Thread eventGeneratorThread = new Thread(new EventGenerator(eventQueue, TOTAL_EVENTS));
        eventGeneratorThread.start();

        // Use a cached thread pool for better resource utilization
        ExecutorService postingExecutor = Executors.newCachedThreadPool();

        // Submit PostWorker tasks to the thread pool
        for (int i = 0; i < INITIAL_THREADS; i++) {
            postingExecutor.submit(new PostWorker(eventQueue, completedRequests,
                    failedRequests, logEntries, latch));
        }

        // Wait for event generation to complete
        eventGeneratorThread.join();

        // Wait for all events to be processed
        latch.await();

        // Capture end time after all threads complete
        long endTime = System.currentTimeMillis();
        long totalRunTime = endTime - startTime; // Total wall time in milliseconds

        // Write logs to the CSV file
        writeLogsToCSV(csvFile, logEntries);

        // Load latencies from the log entries
        List<Long> latencies = extractLatencies(logEntries);

        // Print performance metrics
        printMetrics(latencies, completedRequests.get(), totalRunTime);

        // Print summary statistics
        double totalRunTimeSeconds = totalRunTime / 1000.0; // Convert to seconds
        double throughput = TOTAL_EVENTS / totalRunTimeSeconds;

        System.out.println("Number of successful requests: " + completedRequests.get());
        System.out.println("Number of unsuccessful requests: " + failedRequests.get());
        System.out.println("Total run time (wall time): " + totalRunTimeSeconds + " seconds");
        System.out.println("Total throughput: " + throughput + " requests/second");
    }

    private static void writeLogsToCSV(String fileName, ConcurrentLinkedQueue<String> logEntries) throws IOException {
        try (FileWriter csvWriter = new FileWriter(fileName)) {
            csvWriter.write("startTime,requestType,latency,responseCode\n"); // CSV header
            for (String entry : logEntries) {
                csvWriter.write(entry + "\n");
            }
            csvWriter.flush();
        }
    }

    private static List<Long> extractLatencies(ConcurrentLinkedQueue<String> logEntries) {
        List<Long> latencies = new ArrayList<>();
        for (String entry : logEntries) {
            String[] values = entry.split(",");
            latencies.add(Long.parseLong(values[2])); // Extract latency value
        }
        return latencies;
    }

    private static void printMetrics(List<Long> latencies, int totalRequests, long totalRunTime) {
        Collections.sort(latencies);

        long min = latencies.get(0);
        long max = latencies.get(latencies.size() - 1);
        long median = latencies.get(latencies.size() / 2);
        long p99 = latencies.get((int) (latencies.size() * 0.99));
        double mean = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);

        System.out.println("Min response time: " + min + " ms");
        System.out.println("Max response time: " + max + " ms");
        System.out.println("Median response time: " + median + " ms");
        System.out.println("P99 response time: " + p99 + " ms");
        System.out.println("Mean response time: " + mean + " ms");
    }
}

// package io.swagger.client;

// import java.util.concurrent.*;
// import java.util.concurrent.atomic.AtomicInteger;

// public class SkiersApiMultithreadedClient {

// private static final int TOTAL_EVENTS = 200000;
// private static final int INITIAL_THREADS = 32;
// private static final int QUEUE_CAPACITY = 5000;

// public static void main(String[] args) throws InterruptedException {
// BlockingQueue<SkierLiftRideEvent> eventQueue = new
// LinkedBlockingQueue<>(QUEUE_CAPACITY);
// AtomicInteger completedRequests = new AtomicInteger(0);
// AtomicInteger failedRequests = new AtomicInteger(0);

// long startTime = System.currentTimeMillis();

// // Start the EventGenerator in its own thread
// Thread eventGeneratorThread = new Thread(new EventGenerator(eventQueue,
// TOTAL_EVENTS));
// eventGeneratorThread.start();

// // Initialize a thread pool for posting events
// ExecutorService postingExecutor =
// Executors.newFixedThreadPool(INITIAL_THREADS);

// // Submit PostWorker tasks to the thread pool
// for (int i = 0; i < INITIAL_THREADS; i++) {
// postingExecutor.submit(new PostWorker(eventQueue, completedRequests,
// failedRequests));
// }

// // Wait for event generation to complete
// eventGeneratorThread.join();

// // Wait for all events to be processed
// while (completedRequests.get() + failedRequests.get() < TOTAL_EVENTS) {
// Thread.sleep(100); // Brief pause to prevent tight loop
// }

// postingExecutor.shutdownNow();
// postingExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

// long endTime = System.currentTimeMillis();
// long totalRunTime = endTime - startTime;
// double totalRunTimeSeconds = totalRunTime / 1000.0;

// // Print performance metrics
// System.out.println("Number of threads used: " + INITIAL_THREADS);
// System.out.println("Number of successful requests: " +
// completedRequests.get());
// System.out.println("Number of unsuccessful requests: " +
// failedRequests.get());
// System.out.println("Total run time (wall time): " + totalRunTimeSeconds + "
// seconds");
// System.out.println("Throughput: " + (TOTAL_EVENTS / totalRunTimeSeconds) + "
// requests/second");
// }
// }
