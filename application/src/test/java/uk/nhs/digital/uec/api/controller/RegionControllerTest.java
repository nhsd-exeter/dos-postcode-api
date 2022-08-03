package uk.nhs.digital.uec.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.dto.PostcodeMappingDTO;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.RegionService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class RegionControllerTest {

  @InjectMocks
  private RegionController regionController;

  @Mock
  private RegionService regionService;


  @Test
  @DisplayName("Get All Regions When No PostCode Given")
  void testGetAllRegions(CapturedOutput log) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    //Given
    Map<String, List<String>> regionsList = new HashMap<>();
    regionsList.put("region1", Arrays.asList("subregion1", "subregion11", "subregion111"));
    regionsList.put("region2", Arrays.asList("subregion2", "subregion22", "subregion222"));
    regionsList.put("region3", Arrays.asList("subregion3", "subregion333", "subregion333"));
    when(regionService.getAllRegions()).thenReturn(regionsList);

    // When
    ResponseEntity<Map<String,List<String>>> response = regionController.getAllRegions();

    //Then
    assertEquals(3, regionsList.size());
    verify(regionService, times(1)).getAllRegions();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(log.getOut().contains("Processing Get All Regions"));

  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testGetRegionsWithPostcode(CapturedOutput log) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    String postcode = "XX1 1AX";
    PostcodeMappingDTO postcodeMapping = new PostcodeMappingDTO();
    postcodeMapping.setPostcode("XX1 1AX");
    postcodeMapping.setRegion("Sub Region");
    postcodeMapping.setRegion("Region");
    when(regionService.getRegionByPostCode(postcode)).thenReturn(postcodeMapping);

    // When
    ResponseEntity<PostcodeMappingDTO> response = regionController.getRegionDetailsByPostCode(postcode);

    //Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(regionService, times(1)).getRegionByPostCode(postcode);
    assertTrue(log.getOut().contains("Processing Get Region Details By Given PostCode"));

  }


  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testGetRegionsDetailsByPostCodes(CapturedOutput log) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    List<String> postcodes = Arrays.asList("XX11XX","YY11YY");
    List<PostcodeMappingDTO> postcodeMappingsList = new ArrayList<>();
    PostcodeMappingDTO postcodeMapping = new PostcodeMappingDTO();
    postcodeMapping.setPostcode("XX11XX");
    postcodeMapping.setRegion("Sub Region1");
    postcodeMapping.setRegion("Region1");
    postcodeMappingsList.add(postcodeMapping);

    postcodeMapping = new PostcodeMappingDTO();
    postcodeMapping.setPostcode("YY11YY");
    postcodeMapping.setRegion("Sub Region2");
    postcodeMapping.setRegion("Region2");
    postcodeMappingsList.add(postcodeMapping);


    when(regionService.getRegionByPostCodes(postcodes)).thenReturn(postcodeMappingsList);

    // When
    ResponseEntity<?> response = regionController.getRegionDetailsByPostCodes(postcodes);

    //Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(regionService, times(1)).getRegionByPostCodes(postcodes);
    assertTrue(log.getOut().contains("Processing Get Region Details By Given PostCodes"));

  }


}
