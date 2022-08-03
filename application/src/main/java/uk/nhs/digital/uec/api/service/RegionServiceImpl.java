package uk.nhs.digital.uec.api.service;


import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.dto.PostcodeMappingDTO;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;


import javax.xml.transform.Source;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RegionServiceImpl implements RegionService {

  @Autowired
  private RegionMapper regionMapper;

  @Autowired
  private PostcodeMappingService postcodeMappingService;


  @Autowired
  private ModelMapper modelMapper;

  @Override
  public Map<String, List<String>> getAllRegions() {
    return regionMapper.getAllRegions();
  }



  @Override
  public PostcodeMappingDTO getRegionByPostCode(String postcode) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    PostcodeMapping postcodeMapping = postcodeMappingService.getByPostCodes(Arrays.asList(postcode))
      .stream()
      .findFirst()
      .orElseThrow(NotFoundException::new);
    log.info(postcodeMapping.toString());
    return ConvertToDTO(postcodeMapping);

  }

  @Override
  public List<PostcodeMappingDTO> getRegionByPostCodes(List<String> postcodes) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    List<PostcodeMapping> postcodeMappings =  postcodeMappingService.getByPostCodes(postcodes);
    List<PostcodeMappingDTO> postcodeMappingDTOList = postcodeMappings
      .stream()
      .map(map -> ConvertToDTO(map))
      .collect(Collectors.toList());
    if (postcodeMappings.size() == 0 ){
      throw  new NotFoundException();
    }
    return postcodeMappingDTOList;
  }

  public PostcodeMappingDTO ConvertToDTO(PostcodeMapping postcodeMapping) {
    TypeMap<PostcodeMapping,PostcodeMappingDTO> typeMap = this.modelMapper.getTypeMap(PostcodeMapping.class,PostcodeMappingDTO.class);
    if (typeMap == null) {
      typeMap = this.modelMapper.createTypeMap(PostcodeMapping.class,PostcodeMappingDTO.class);
    }
    typeMap.addMapping(PostcodeMapping::getPostCode,PostcodeMappingDTO::setPostcode)
      .addMapping(PostcodeMapping::getSubRegion,PostcodeMappingDTO::setAuthority);
    PostcodeMappingDTO postcodeMappingDTO = modelMapper.map(postcodeMapping,PostcodeMappingDTO.class);
    return postcodeMappingDTO;
  }

}
