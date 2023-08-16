package uk.nhs.digital.uec.api.model;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Region {
  EAST_OF_ENGLAND("East of England"),
  LONDON("London"),
  NORTH_EAST_AND_YORKSHIRE("Yorkshire and The Humber"),
  NORTHERN_IRELAND("Northern Ireland"),
  NORTH_WEST("North West"),
  SOUTH_EAST("South East"),
  SOUTH_WEST("South West"),
  SCOTLAND("Scotland"),
  WALES("Wales"),
  MIDLANDS("Midlands");

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
