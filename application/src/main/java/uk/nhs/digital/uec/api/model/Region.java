package uk.nhs.digital.uec.api.model;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Region {
  EAST_OF_ENGLAND("NHS East of England Region"),
  LONDON("NHS London Region"),
  NORTH_EAST_AND_YORKSHIRE("NHS North East and Yorkshire Region"),
  NORTHERN_IRELAND("Northern Ireland"),
  NORTH_WEST("NHS North West Region"),
  SOUTH_EAST("NHS South East Region"),
  SOUTH_WEST("NHS South West Region"),
  SCOTLAND("Scotland"),
  WALES("Wales"),
  MIDLANDS("NHS Midlands Region");

  private String name;

  public static Region getRegionEnum(String district) {
    return Arrays
      .stream(values())
      .sorted()
      .filter(region ->
        region.getName().equalsIgnoreCase(district) ||
        isMidlands(district, region.getName())
      )
      .findFirst()
      .orElse(null);
  }

  private static boolean isMidlands(String district, String name) {
    return district.substring(5).equalsIgnoreCase(name);
  }
}
