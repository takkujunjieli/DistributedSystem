package io.swagger.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SkierTotalVertical")
public class SkierTotalVertical {

  @Id
  @Column(name = "skierId")
  private Integer skierId;

  @Column(name = "totalVertical")
  private Integer totalVertical;

  // Getters and Setters
  public Integer getSkierId() {
    return skierId;
  }

  public Integer getTotalVertical() {
    return totalVertical;
  }
}
