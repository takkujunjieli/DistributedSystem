package io.swagger.client;

import java.util.concurrent.BlockingQueue;

public class EventGenerator implements Runnable {

    private final BlockingQueue<SkierLiftRideEvent> eventQueue;
    private final int totalEvents;

    public EventGenerator(BlockingQueue<SkierLiftRideEvent> eventQueue, int totalEvents) {
        this.eventQueue = eventQueue;
        this.totalEvents = totalEvents;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalEvents; i++) {
            try {
                SkierLiftRideEvent event = new SkierLiftRideEvent();
                eventQueue.put(event); // Block if queue is full
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Event generation interrupted");
                return;
            }
        }
    }
}
