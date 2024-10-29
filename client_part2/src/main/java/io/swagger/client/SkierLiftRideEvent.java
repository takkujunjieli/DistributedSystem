package io.swagger.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;

public class SkierLiftRideEvent {

    private int skierID;
    private int resortID;
    private int liftID;
    private int seasonID;
    private int dayID;
    private int time;

    public SkierLiftRideEvent() {
        Random rand = new Random();
        this.skierID = rand.nextInt(100000) + 1;
        this.resortID = rand.nextInt(10) + 1;
        this.liftID = rand.nextInt(40) + 1;
        this.seasonID = 2024;
        this.dayID = 1;
        this.time = rand.nextInt(360) + 1;
    }

    public int getSkierID() {
        return skierID;
    }

    public int getResortID() {
        return resortID;
    }

    public int getLiftID() {
        return liftID;
    }

    public int getSeasonID() {
        return seasonID;
    }

    public int getDayID() {
        return dayID;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SkierLiftRideEvent{" +
                "skierID=" + skierID +
                ", resortID=" + resortID +
                ", liftID=" + liftID +
                ", seasonID=" + seasonID +
                ", dayID=" + dayID +
                ", time=" + time +
                '}';
    }
}
