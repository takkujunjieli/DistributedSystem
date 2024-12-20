package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.SkierVerticalResorts;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.openapitools.jackson.nullable.JsonNullable;
import io.swagger.configuration.NotUndefined;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * SkierVertical
 */
@Validated
@NotUndefined
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-10-11T23:58:45.751066896Z[GMT]")


public class SkierVertical   {
  @JsonProperty("resorts")
  @Size(min = 1, message = "Resorts list must contain at least one item")
  @NotNull(message = "Resorts list cannot be null")
  @Valid
  private List<SkierVerticalResorts> resorts;

  public SkierVertical resorts(List<SkierVerticalResorts> resorts) { 

    this.resorts = resorts;
    return this;
  }

  public SkierVertical addResortsItem(SkierVerticalResorts resortsItem) {
    if (this.resorts == null) {
      this.resorts = new ArrayList<SkierVerticalResorts>();
    }
    this.resorts.add(resortsItem);
    return this;
  }

  /**
   * Get resorts
   * @return resorts
   **/
  
  @Schema(description = "")
  @Valid
  public List<SkierVerticalResorts> getResorts() {  
    return resorts;
  }



  public void setResorts(List<SkierVerticalResorts> resorts) { 
    this.resorts = resorts;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SkierVertical skierVertical = (SkierVertical) o;
    return Objects.equals(this.resorts, skierVertical.resorts);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resorts);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SkierVertical {\n");
    
    sb.append("    resorts: ").append(toIndentedString(resorts)).append("\n");
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
