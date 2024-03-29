package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.model.Region;
import uk.nhs.digital.uec.api.service.RegionService;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class RegionControllerTest {

  @InjectMocks private RegionController regionController;

  @Mock private RegionService regionService;

  @Test
  @DisplayName("Get All Regions When No PostCode Given")
  void testGetAllRegions(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    Map<String, List<String>> regionsList = new HashMap<>();
    regionsList.put("region1", Arrays.asList("subregion1", "subregion11", "subregion111"));
    regionsList.put("region2", Arrays.asList("subregion2", "subregion22", "subregion222"));
    regionsList.put("region3", Arrays.asList("subregion3", "subregion333", "subregion333"));
    when(regionService.getAllRegions()).thenReturn(regionsList);

    // When
    ResponseEntity<Map<String, List<String>>> response = regionController.getAllRegions();

    // Then
    assertEquals(3, regionsList.size());
    verify(regionService, times(1)).getAllRegions();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(log.getOut().contains("Processing Get All Regions"));
  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testGetRegionsWithPostcode(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    String postcode = "XX1 1AX";
    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setPostcode("XX1 1AX");
    postcodeMapping.setSubRegion("Sub Region");
    postcodeMapping.setRegion(Region.LONDON);
    when(regionService.getRegionByPostCode(postcode)).thenReturn(postcodeMapping);

    // When
    ResponseEntity<PostcodeMapping> response =
        regionController.getRegionDetailsByPostCode(postcode);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(regionService, times(1)).getRegionByPostCode(postcode);
    assertTrue(log.getOut().contains("Processing Get Region Details By Given PostCode"));
  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testGetRegionsDetailsByPostCodes(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    List<String> postcodes = Arrays.asList("XX11XX", "YY11YY");
    List<PostcodeMapping> postcodeMappingsList = new ArrayList<>();
    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setPostcode("XX11XX");
    postcodeMapping.setSubRegion("Sub Region");
    postcodeMapping.setRegion(Region.LONDON);
    postcodeMappingsList.add(postcodeMapping);

    postcodeMapping = new PostcodeMapping();
    postcodeMapping.setPostcode("YY11YY");
    postcodeMapping.setSubRegion("Sub Region");
    postcodeMapping.setRegion(Region.LONDON);
    postcodeMappingsList.add(postcodeMapping);

    when(regionService.getRegionByPostCodes(postcodes)).thenReturn(postcodeMappingsList);

    // When
    ResponseEntity<?> response = regionController.getRegionDetailsByPostCodes(postcodes);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(postcodeMappingsList, response.getBody());
    verify(regionService, times(1)).getRegionByPostCodes(postcodes);
    assertTrue(log.getOut().contains("Processing Get Region Details By Given PostCodes"));
  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testShouldReturnOkEvenIfInvalidPostCode(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    String postcodes = "XX11XX";

    when(regionService.getRegionByPostCode(postcodes)).thenThrow(InvalidPostcodeException.class);
    // When
    ResponseEntity<PostcodeMapping> response =
        regionController.getRegionDetailsByPostCode(postcodes);
    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(regionService, times(1)).getRegionByPostCode(postcodes);
  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testShouldReturnOkEvenForInvalidParamPostCode(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    String postcodes = "XX11XX";

    when(regionService.getRegionByPostCode(postcodes)).thenThrow(InvalidParameterException.class);
    // When
    ResponseEntity<?> response = regionController.getRegionDetailsByPostCode(postcodes);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testShouldReturnOkEvenIfPostCodeNotFound(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    String postcodes = "XX11XX";

    when(regionService.getRegionByPostCode(postcodes)).thenThrow(NotFoundException.class);
    // When
    ResponseEntity<?> response = regionController.getRegionDetailsByPostCode(postcodes);
    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("Get Region Details For A Given PostCode")
  void testShouldReturnOkEvenForInvalidParamPostCodes(CapturedOutput log)
      throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    // Given
    List<String> postcodes = Arrays.asList("XX11XX", "YY11YY");

    when(regionService.getRegionByPostCodes(postcodes)).thenThrow(InvalidParameterException.class);
    // When
    ResponseEntity<?> response = regionController.getRegionDetailsByPostCodes(postcodes);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
