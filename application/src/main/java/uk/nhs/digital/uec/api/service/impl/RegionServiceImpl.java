package uk.nhs.digital.uec.api.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.service.PostcodeMappingService;
import uk.nhs.digital.uec.api.service.RegionService;

@Service
@Slf4j
public class RegionServiceImpl implements RegionService {

  @Autowired private RegionMapperImpl regionMapper;

  @Autowired private PostcodeMappingService postcodeMappingService;

  @Override
  public Map<String, List<String>> getAllRegions() {
    return regionMapper.getAllRegions();
  }

  @Override
  public PostcodeMapping getRegionByPostCode(String postcode)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    PostcodeMapping postcodeMapping =
        postcodeMappingService.getByPostCodes(Arrays.asList(postcode)).stream()
            .findFirst()
            .orElse(new PostcodeMapping());
    log.info("getRegionByPostCode {}", postcodeMapping);
    return postcodeMapping;
  }

  @Override
  public List<PostcodeMapping> getRegionByPostCodes(List<String> postcodes)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    List<PostcodeMapping> postcodeMappings = postcodeMappingService.getByPostCodes(postcodes);
    if (postcodeMappings.size() == 0) {
      throw new NotFoundException();
    }
    return postcodeMappings;
  }
}
