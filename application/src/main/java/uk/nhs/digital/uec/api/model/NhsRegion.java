package uk.nhs.digital.uec.api.model;

import java.util.HashMap;
import java.util.Map;

public enum NhsRegion {
  EAST_OF_ENGLAND("NHS East of England Region"),
  LONDON("NHS London Region"),
  NORTH_EAST_AND_YORKSHIRE("NHS North East and Yorkshire Region"),
  NORTHERN_IRELAND("Northern Ireland"),

  NORTH_WEST("NHS North West Region"),
  SOUTH_EAST("NHS South East Region"),
  SOUTH_WEST("NHS South West Region"),
  SCOTLAND("Scotland"),
  WALES("Wales"),
  MIDLANDS("NHS Midlands Region"),
  NOT_FOUND("NotFound");

  private final String regionName;

  private static final Map<String, NhsRegion> lookup = new HashMap<>();

  static {
    for (NhsRegion d : NhsRegion.values()) {
      lookup.put(d.name(), d);
    }
  }

  private NhsRegion(String regionName) {
    this.regionName = regionName;
  }

  public String getRegion() {
    return regionName;
  }

  public static NhsRegion getRegionValue(final String regionCode) {
    return lookup.get(regionCode);
  }

  public static NhsRegion getNhsRegionByValue(final String value) {
    for (NhsRegion nhsRegion : NhsRegion.values()) {
      if (nhsRegion.regionName.equals(value)) {
        return nhsRegion;
      }
    }
    return NOT_FOUND; // or throw an exception if desired
  }
}
