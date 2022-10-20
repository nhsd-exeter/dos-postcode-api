package uk.nhs.digital.uec.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.model.Region;
import uk.nhs.digital.uec.api.service.impl.RegionMapperImpl;
import uk.nhs.digital.uec.api.service.impl.RegionServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class RegionServiceTest {

  Map<String, List<String>> regionsList = null;
  List<PostcodeMapping> postcodeMappingsList = null;
  PostcodeMapping postcodeMapping = null;

  String postcode = "XX1 1AX";
  String name = "NAME";
  String region = "Region";
  String subRegion = "Sub Region";

  @InjectMocks
  private RegionServiceImpl regionService;
  @Mock
  private RegionMapperImpl regionMapper;
  @Mock
  private PostcodeMappingService postcodeMappingService;

  @BeforeEach
  public void initEach() {
    regionsList = new HashMap<>();
    regionsList.put("region1", Arrays.asList("subregion1", "subregion11", "subregion111"));
    regionsList.put("region2", Arrays.asList("subregion2", "subregion22", "subregion222"));
    regionsList.put("region3", Arrays.asList("subregion3", "subregion333", "subregion333"));

    postcodeMappingsList = new ArrayList<>();
    PostcodeMapping pcodeMapping = new PostcodeMapping();
    pcodeMapping.setPostcode(postcode);
    pcodeMapping.setName(name);
    pcodeMapping.setSubRegion(subRegion);
    pcodeMapping.setRegion(Region.LONDON);
    postcodeMappingsList.add(pcodeMapping);

    postcodeMapping = postcodeMappingsList.stream().findFirst().orElse(null);
  }


  @Test
  void testGetAllRegions() {
    // Given
    when(regionMapper.getAllRegions()).thenReturn(regionsList);
    Map<String, List<String>> allRegions = regionService.getAllRegions();
    // Then
    assertEquals(3, allRegions.size());

  }

  @Test
  void testGetRegionByPostCode() throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // When
    when(postcodeMappingService.getByPostCodes(Arrays.asList(postcode))).thenReturn(postcodeMappingsList);
    PostcodeMapping postcodeMappingDTO = regionService.getRegionByPostCode(postcode);

    //Then
    assertEquals(postcodeMapping.getPostcode(), postcodeMappingDTO.getPostcode());
    assertEquals(null, postcodeMappingDTO.getEasting());
    assertEquals(null, postcodeMappingDTO.getNorthing());
    assertEquals(null, postcodeMappingDTO.getNhs_region());
    assertEquals(null, postcodeMappingDTO.getIcb());
    assertEquals(Region.LONDON, postcodeMappingDTO.getRegion());
    assertEquals(null, postcodeMappingDTO.getEmail());

  }

  @Test
  void testGetRegionByPostCodes() throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // When
    List<String> postcodes = Arrays.asList(postcode);
    when(postcodeMappingService.getByPostCodes(postcodes)).thenReturn(postcodeMappingsList);
    List<PostcodeMapping> postcodeMappingDTOList = regionService.getRegionByPostCodes(postcodes);
    //Then
    assertEquals(1, postcodeMappingDTOList.size());
    assertEquals(postcodeMappingsList.get(0).getPostcode(), postcodeMappingDTOList.get(0).getPostcode());

  }


}
