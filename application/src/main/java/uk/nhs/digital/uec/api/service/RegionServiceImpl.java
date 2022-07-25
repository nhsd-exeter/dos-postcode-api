package uk.nhs.digital.uec.api.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;


import java.util.*;


@Service
@Slf4j
public class RegionServiceImpl implements RegionService {

  @Autowired
  private RegionMapper regionMapper;

  @Autowired
  private PostcodeMappingService postcodeMappingService;

  @Override
  public Map<String, List<String>> getAllRegions() {
    return regionMapper.getAllRegions();
  }


  @Override
  public List<PostcodeMapping> getRegionByPostCode(String postcode) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    return postcodeMappingService.getByPostCodes(Arrays.asList(postcode));

  }

}
