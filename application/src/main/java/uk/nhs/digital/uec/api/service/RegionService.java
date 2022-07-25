package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;

import java.util.List;
import java.util.Map;


public interface RegionService {
  Map<String,List<String>> getAllRegions();
  List<PostcodeMapping> getRegionByPostCode(String postcode) throws InvalidParameterException, NotFoundException, InvalidPostcodeException;
}

