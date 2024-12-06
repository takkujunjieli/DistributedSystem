package io.swagger.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AggregatedLiftRides")
public class AggregatedLiftRides {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "skierId", nullable = false)
  private Integer skierId;

  @Column(name = "resortId", nullable = false)
  private Integer resortId;

  @Column(name = "seasonId", nullable = false)
  private Integer seasonId;

  @Column(name = "dayId", nullable = false)
  private Integer dayId;

  @Column(name = "totalVertical", nullable = false)
  private Integer totalVertical;

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getSkierId() {
    return skierId;
  }

  public void setSkierId(Integer skierId) {
    this.skierId = skierId;
  }

  public Integer getResortId() {
    return resortId;
  }

  public void setResortId(Integer resortId) {
    this.resortId = resortId;
  }

  public Integer getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(Integer seasonId) {
    this.seasonId = seasonId;
  }

  public Integer getDayId() {
    return dayId;
  }

  public void setDayId(Integer dayId) {
    this.dayId = dayId;
  }

  public Integer getTotalVertical() {
    return totalVertical;
  }


}
