package uk.nhs.digital.uec.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostcodeMappingDTO {
  private String postcode;
  private Integer easting;
  private Integer northing;
  private String nhs_region;
  private String icb;
  private String email;
  private String region;
  private String authority;

}
