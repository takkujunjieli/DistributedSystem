package org.distributed.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ResortDayVisitors")
public class ResortDayVisitors {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int resortId;

  @Column(nullable = false)
  private int dayId;

  @Column(nullable = false)
  private int uniqueSkiers; // Unique skiers count

  public Long getId() {
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

  public int getDayId() {
    return dayId;
  }

  public void setDayId(int dayId) {
    this.dayId = dayId;
  }

  public int getUniqueSkiers() {
    return uniqueSkiers;
  }

  public void setUniqueSkiers(int uniqueSkiers) {
    this.uniqueSkiers = uniqueSkiers;
  }
}
