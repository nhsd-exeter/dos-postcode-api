package uk.nhs.digital.uec.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.dto.PostcodeMappingDTO;
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

  @Spy
  ModelMapper modelMapper=new ModelMapper();

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
    String name = "NAME";
    String region="Region";
    String subRegion = "Sub Region";
    List<PostcodeMapping> postcodeMappingsList = new ArrayList<>();
    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setPostCode(postcode);
    postcodeMapping.setName(name);
    postcodeMapping.setSubRegion(subRegion);
    postcodeMapping.setRegion(region);
    postcodeMappingsList.add(postcodeMapping);

    PostcodeMappingDTO postcodeMappingDto =   new PostcodeMappingDTO();
    postcodeMappingDto.setPostcode(postcode);
    postcodeMappingDto.setRegion(region);
    postcodeMappingDto.setAuthority(subRegion);

    when(postcodeMappingService.getByPostCodes(Arrays.asList(postcode))).thenReturn(postcodeMappingsList);
    when(regionService.ConvertToDTO(postcodeMapping)).thenReturn(postcodeMappingDto);

    //When
    PostcodeMappingDTO postcodeMappingDTO = regionService.getRegionByPostCode(postcode);

    //Then
    assertEquals(postcodeMapping.getPostCode(), postcodeMappingDTO.getPostcode());


  }

  @Test
  void testGetRegionByPostCodes() throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    List<String> postcodes = Arrays.asList("XX11XX");
    List<PostcodeMapping> postcodeMappingsList = new ArrayList<>();
    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setPostCode("XX11XX");
    postcodeMapping.setName("NAME");
    postcodeMapping.setSubRegion("Sub Region");
    postcodeMapping.setRegion("Region");
    postcodeMappingsList.add(postcodeMapping);
    when(postcodeMappingService.getByPostCodes(postcodes)).thenReturn(postcodeMappingsList);

    //When
    List<PostcodeMappingDTO> postcodeMappingDTOList = regionService.getRegionByPostCodes(postcodes);

    //Then
    assertEquals(1,postcodeMappingDTOList.size());
    assertEquals(postcodeMappingsList.get(0).getPostCode(),postcodeMappingDTOList.get(0).getPostcode());

  }




}
