package io.swagger.model;

//import jakarta.persistence.*;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SkierLiftRides")
public class SkierLiftRides {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "resortId", nullable = false)
  private int resortId;

  @Column(name = "seasonId", nullable = false)
  private int seasonId;

  @Column(name = "dayId",nullable = false)
  private int dayId;

  @Column(name = "skierId",nullable = false)
  private int skierId;

  @Column(name = "liftId",nullable = false)
  private int liftId;

  @Column(name = "time",nullable = false)
  private int time; // Store as seconds or an integer

  public long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getResortId() {
    return resortId;
  }

  public void setResortId(int resortId) {
    this.resortId = resortId;
  }

  public int getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(int seasonId) {
    this.seasonId = seasonId;
  }

  public int getDayId() {
    return dayId;
  }

  public void setDayId(int dayId) {
    this.dayId = dayId;
  }

  public int getSkierId() {
    return skierId;
  }

  public void setSkierId(int skierId) {
    this.skierId = skierId;
  }

  public int getLiftId() {
    return liftId;
  }

  public void setLiftId(int liftId) {
    this.liftId = liftId;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time) {
    this.time = time;
  }
}
