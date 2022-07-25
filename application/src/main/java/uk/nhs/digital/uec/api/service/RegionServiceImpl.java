package uk.nhs.digital.uec.api.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;


import java.util.*;


@Service
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
  public PostcodeMapping getRegionByPostCode(String postcode) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    return postcodeMappingService.getByPostCodes(Arrays.asList(postcode)).get(0);

  }

  @Override
  public List<PostcodeMapping> getRegionByPostCodes(List<String> postcodes) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    return postcodeMappingService.getByPostCodes(postcodes);

  }

}
