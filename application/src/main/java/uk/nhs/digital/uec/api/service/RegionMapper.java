package uk.nhs.digital.uec.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import uk.nhs.digital.uec.api.model.CCGRecord;
import uk.nhs.digital.uec.api.model.ICBRecord;
import uk.nhs.digital.uec.api.model.RegionRecord;

public interface RegionMapper {
  RegionRecord getRegionRecord(String postcode);

  ICBRecord getICBRecord(String orgCode);

  Map<String, List<String>> getAllRegions();

  Optional<CCGRecord> getCCGRecord(String postcode, String region);
}
