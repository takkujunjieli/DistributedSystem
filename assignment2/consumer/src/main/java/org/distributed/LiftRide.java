package org.distributed;

import java.util.Objects;

public class LiftRide {

  private Integer time;
  private Integer liftID;

  // Constructor
  public LiftRide() {}

  public LiftRide(Integer time, Integer liftID) {
    this.time = time;
    this.liftID = liftID;
  }

  // Getter and Setter for time
  public Integer getTime() {
    return time;
  }

  // Getter and Setter for liftID
  public Integer getLiftID() {
    return liftID;
  }

  // Equals and HashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LiftRide liftRide = (LiftRide) o;
    return Objects.equals(time, liftRide.time) &&
        Objects.equals(liftID, liftRide.liftID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, liftID);
  }

  // toString method for debugging
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LiftRide {\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    liftID: ").append(toIndentedString(liftID)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  // Helper method for formatting toString output
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
