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
 * SkierVerticalResorts
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-10-11T23:58:45.751066896Z[GMT]")


public class SkierVerticalResorts   {
  @JsonProperty("seasonID")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private String seasonID = null;

  @JsonProperty("totalVert")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private Integer totalVert = null;


  public SkierVerticalResorts seasonID(String seasonID) { 

    this.seasonID = seasonID;
    return this;
  }

  /**
   * Get seasonID
   * @return seasonID
   **/
  
  @Schema(description = "")
  
  public String getSeasonID() {  
    return seasonID;
  }



  public void setSeasonID(String seasonID) { 
    this.seasonID = seasonID;
  }

  public SkierVerticalResorts totalVert(Integer totalVert) { 

    this.totalVert = totalVert;
    return this;
  }

  /**
   * Get totalVert
   * @return totalVert
   **/
  
  @Schema(description = "")
  
  public Integer getTotalVert() {  
    return totalVert;
  }



  public void setTotalVert(Integer totalVert) { 
    this.totalVert = totalVert;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SkierVerticalResorts skierVerticalResorts = (SkierVerticalResorts) o;
    return Objects.equals(this.seasonID, skierVerticalResorts.seasonID) &&
        Objects.equals(this.totalVert, skierVerticalResorts.totalVert);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seasonID, totalVert);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SkierVerticalResorts {\n");
    
    sb.append("    seasonID: ").append(toIndentedString(seasonID)).append("\n");
    sb.append("    totalVert: ").append(toIndentedString(totalVert)).append("\n");
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
