package uk.nhs.digital.uec.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;


import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class RegionServiceImpl implements RegionService {

  @Autowired
  private RegionMapper regionMapper;

  @Override
  public Map<String, List<String>> getAllRegions() {
    return regionMapper.getAllRegions();
  }

}
