package uk.nhs.digital.uec.api.service;


import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;

import java.util.List;
import java.util.Map;


public interface RegionService {
  Map<String, List<String>> getAllRegions();

  PostcodeMapping getRegionByPostCode(String postcode) throws InvalidParameterException, NotFoundException, InvalidPostcodeException;

  List<PostcodeMapping> getRegionByPostCodes(List<String> postcodes) throws InvalidParameterException, NotFoundException, InvalidPostcodeException;
}

