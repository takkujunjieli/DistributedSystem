package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import org.openapitools.jackson.nullable.JsonNullable;
import io.swagger.configuration.NotUndefined;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * LiftRide
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-10-11T23:58:45.751066896Z[GMT]")


public class LiftRide   {

  @JsonProperty("time")
  @NotNull(message = "Time cannot be null")
  @Min(value = 0, message = "Time must be a positive integer")
  @Max(value = 1440, message = "Time must be within a valid range (0-1440 minutes)")
  private Integer time;


  @JsonProperty("liftID")
  @NotNull(message = "LiftID cannot be null")
  @Min(value = 1, message = "LiftID must be a positive integer")
  private Integer liftID;


  public LiftRide time(Integer time) { 

    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   **/
  
  @Schema(example = "217", description = "")
  
  public Integer getTime() {  
    return time;
  }



  public void setTime(Integer time) { 
    this.time = time;
  }

  public LiftRide liftID(Integer liftID) { 

    this.liftID = liftID;
    return this;
  }

  /**
   * Get liftID
   * @return liftID
   **/
  
  @Schema(example = "21", description = "")
  
  public Integer getLiftID() {  
    return liftID;
  }



  public void setLiftID(Integer liftID) { 
    this.liftID = liftID;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LiftRide liftRide = (LiftRide) o;
    return Objects.equals(this.time, liftRide.time) &&
        Objects.equals(this.liftID, liftRide.liftID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, liftID);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LiftRide {\n");
    
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    liftID: ").append(toIndentedString(liftID)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
