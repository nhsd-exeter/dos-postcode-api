package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.model.CCGRecord;
import uk.nhs.digital.uec.api.model.ICBRecord;
import uk.nhs.digital.uec.api.model.RegionRecord;

import java.util.List;
import java.util.Map;

public interface RegionMapper {
  RegionRecord getRegionRecord(String postcode);

  ICBRecord getICBRecord(String orgCode);

  Map<String, List<String>> getAllRegions();

  CCGRecord getCCGRecord(String postCode, String region);
}
