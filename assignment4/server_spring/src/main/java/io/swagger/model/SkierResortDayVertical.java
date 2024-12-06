package io.swagger.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "SkierResortDayVertical")
public class SkierResortDayVertical {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "skierId")
  private Integer skierId;

  @Column(name = "resortId")
  private Integer resortId;

  @Column(name = "seasonId")
  private Integer seasonId;

  @Column(name = "dayId")
  private Integer dayId;

  @Column(name = "totalVertical")
  private Integer totalVertical;

  public Integer getSkierId() {
    return skierId;
  }

  public Integer getResortId() {
    return resortId;
  }

  public Integer getSeasonId() {
    return seasonId;
  }

  public Integer getDayId() {
    return dayId;
  }

  public Integer getTotalVertical() {
    return totalVertical;
  }
}