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
 * ResortSkiers
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-10-11T23:58:45.751066896Z[GMT]")


public class ResortSkiers   {
  @JsonProperty("time")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String time = null;

  @JsonProperty("numSkiers")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Integer numSkiers = null;


  public ResortSkiers time(String time) { 

    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   **/
  
  @Schema(example = "Mission Ridge", description = "")
  
  public String getTime() {  
    return time;
  }



  public void setTime(String time) { 
    this.time = time;
  }

  public ResortSkiers numSkiers(Integer numSkiers) { 

    this.numSkiers = numSkiers;
    return this;
  }

  /**
   * Get numSkiers
   * @return numSkiers
   **/
  
  @Schema(example = "78999", description = "")
  
  public Integer getNumSkiers() {  
    return numSkiers;
  }



  public void setNumSkiers(Integer numSkiers) { 
    this.numSkiers = numSkiers;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResortSkiers resortSkiers = (ResortSkiers) o;
    return Objects.equals(this.time, resortSkiers.time) &&
        Objects.equals(this.numSkiers, resortSkiers.numSkiers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, numSkiers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResortSkiers {\n");
    
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    numSkiers: ").append(toIndentedString(numSkiers)).append("\n");
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
