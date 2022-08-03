package uk.nhs.digital.uec.api.service;

import org.junit.jupiter.api.BeforeEach;
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

  Map<String, List<String>> regionsList = null;
  List<PostcodeMapping> postcodeMappingsList = null;
  PostcodeMappingDTO postcodeMappingDto = null;
  PostcodeMapping postcodeMapping = null;

  String postcode = "XX1 1AX";
  String name = "NAME";
  String region="Region";
  String subRegion = "Sub Region";



  @InjectMocks
  private RegionServiceImpl regionService;

  @Mock
  private RegionMapper regionMapper;

  @Mock
  private PostcodeMappingService postcodeMappingService;

  @Spy
  ModelMapper modelMapper=new ModelMapper();

  @BeforeEach
  public void initEach(){
    regionsList = new HashMap<>();
    regionsList.put("region1", Arrays.asList("subregion1", "subregion11", "subregion111"));
    regionsList.put("region2", Arrays.asList("subregion2", "subregion22", "subregion222"));
    regionsList.put("region3", Arrays.asList("subregion3", "subregion333", "subregion333"));

    postcodeMappingsList = new ArrayList<>();
    PostcodeMapping pcodeMapping = new PostcodeMapping();
    pcodeMapping.setPostCode(postcode);
    pcodeMapping.setName(name);
    pcodeMapping.setSubRegion(subRegion);
    pcodeMapping.setRegion(region);
    postcodeMappingsList.add(pcodeMapping);

    postcodeMapping = postcodeMappingsList.stream().findFirst().orElse(null);

    postcodeMappingDto =   new PostcodeMappingDTO();
    postcodeMappingDto.setPostcode(postcode);
    postcodeMappingDto.setRegion(region);
    postcodeMappingDto.setAuthority(subRegion);
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
    when(regionService.ConvertToDTO(postcodeMapping)).thenReturn(postcodeMappingDto);
    PostcodeMappingDTO postcodeMappingDTO = regionService.getRegionByPostCode(postcode);

    //Then
    assertEquals(postcodeMapping.getPostCode(), postcodeMappingDTO.getPostcode());
    assertEquals(null, postcodeMappingDTO.getEasting());
    assertEquals(null, postcodeMappingDTO.getNorthing());
    assertEquals(null, postcodeMappingDTO.getNhs_region());
    assertEquals(null, postcodeMappingDTO.getIcb());
    assertEquals(region, postcodeMappingDTO.getRegion());
    assertEquals(subRegion, postcodeMappingDTO.getAuthority());
    assertEquals(null, postcodeMappingDTO.getEmail());

  }

  @Test
  void testGetRegionByPostCodes() throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // When
    List<String> postcodes = Arrays.asList(postcode);
    when(postcodeMappingService.getByPostCodes(postcodes)).thenReturn(postcodeMappingsList);
    List<PostcodeMappingDTO> postcodeMappingDTOList = regionService.getRegionByPostCodes(postcodes);
    //Then
    assertEquals(1,postcodeMappingDTOList.size());
    assertEquals(postcodeMappingsList.get(0).getPostCode(),postcodeMappingDTOList.get(0).getPostcode());

  }




}
