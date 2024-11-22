package org.distributed.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SkiDayTotals")
public class SkiDayTotals {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int skierId;

  @Column(nullable = false)
  private int seasonId;

  @Column(nullable = false)
  private int dayId;

  @Column(nullable = false)
  private int vertical; // Total vertical for the day

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getSkierId() {
    return skierId;
  }

  public void setSkierId(int skierId) {
    this.skierId = skierId;
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

  public int getVertical() {
    return vertical;
  }

  public void setVertical(int vertical) {
    this.vertical = vertical;
  }
}
