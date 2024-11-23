package io.swagger.client;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class Client {
    private static final int NUM_THREADS = 25;
    private static final int REQUESTS_PER_THREAD = 8000;
    private static final int TOTAL_EVENTS = 200000;
    public static final String BASE_URL = "http://52.26.163.233:8080";

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<SkierLiftRideEvent> eventQueue = new LinkedBlockingQueue<>();
        ConcurrentLinkedQueue<RequestStatistics> statistics = new ConcurrentLinkedQueue<>();

        EventSimulator generator = new EventSimulator(eventQueue, TOTAL_EVENTS);
        Thread generatorThread = new Thread(generator);
        generatorThread.start();

        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        int remainingEvents = TOTAL_EVENTS;
        while (remainingEvents > 0) {
            int eventsToSend = Math.min(remainingEvents, REQUESTS_PER_THREAD);
            PostWorker worker = new PostWorker(eventQueue, eventsToSend, statistics);
            executor.execute(worker);
            remainingEvents -= eventsToSend;
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        generatorThread.join();

        long endTime = System.currentTimeMillis();

        writeStatisticsToFile(statistics);
        printStatistics(endTime - startTime, statistics);
    }

    private static void writeStatisticsToFile(ConcurrentLinkedQueue<RequestStatistics> statistics) {
        String fileName = "request_statistics.csv";
        String filePath = Paths.get(System.getProperty("user.dir"), fileName).toString();

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("start time,request type,latency,response code\n");
            for (RequestStatistics stat : statistics) {
                writer.write(String.format("%d,POST,%d,%d\n",
                    stat.startTime, stat.latency, stat.responseCode));
            }
            System.out.println("Statistics saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void printStatistics(long wallTime, ConcurrentLinkedQueue<RequestStatistics> statistics) {
        int totalCompleted = PostWorker.getTotalCompleted();
        int totalFailed = PostWorker.getTotalFailed();

        List<Long> latencies = new ArrayList<>();
        for (RequestStatistics stat : statistics) {
            latencies.add(stat.latency);
        }
        Collections.sort(latencies);

        double mean = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long median = latencies.get(latencies.size() / 2);
        long p99 = latencies.get((int) (latencies.size() * 0.99));
        long min = latencies.get(0);
        long max = latencies.get(latencies.size() - 1);
        double throughput = totalCompleted / (wallTime / 1000.0);

        System.out.println("Number of successful requests: " + totalCompleted);
        System.out.println("Number of failed requests: " + totalFailed);
        System.out.println("Total run time (ms): " + wallTime);
        System.out.println("Throughput (req/sec): " + throughput);
        System.out.println("Mean response time (ms): " + mean);
        System.out.println("Median response time (ms): " + median);
        System.out.println("P99 response time (ms): " + p99);
        System.out.println("Min response time (ms): " + min);
        System.out.println("Max response time (ms): " + max);
    }
}