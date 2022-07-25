package uk.nhs.digital.uec.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class RegionServiceTest {

  @InjectMocks
  private RegionServiceImpl regionService;

  @Mock
  private RegionMapper regionMapper;

  @Mock
  private PostcodeMappingService postcodeMappingService;


  @Test
  void testGetAllRegions() {
    // Given
    Map<String, List<String>> regionsList = new HashMap<>();
    regionsList.put("region1", Arrays.asList("subregion1", "subregion11", "subregion111"));
    regionsList.put("region2", Arrays.asList("subregion2", "subregion22", "subregion222"));
    regionsList.put("region3", Arrays.asList("subregion3", "subregion333", "subregion333"));
    when(regionMapper.getAllRegions()).thenReturn(regionsList);

    //When
    Map<String, List<String>> allRegions = regionService.getAllRegions();

    // Then
    assertEquals(3, allRegions.size());

  }

  @Test
  void testGetRegionByPostCode() throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    String postcode = "XX1 1AX";
    List<PostcodeMapping> postcodeMappingsList = new ArrayList<>();
    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setPostCode("XX1 1AX");
    postcodeMapping.setName("NAME");
    postcodeMapping.setSubregion("Sub Region");
    postcodeMapping.setRegion("Region");
    postcodeMappingsList.add(postcodeMapping);
    when(postcodeMappingService.getByPostCodes(Arrays.asList(postcode))).thenReturn(postcodeMappingsList);

    //When
    List<PostcodeMapping> postcodeMappings = regionService.getRegionByPostCode(postcode);

    //Then
    assertEquals(Arrays.asList(postcodeMapping), postcodeMappings);


  }
}
