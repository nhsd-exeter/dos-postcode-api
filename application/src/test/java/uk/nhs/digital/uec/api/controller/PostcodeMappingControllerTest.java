package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.netty.util.internal.StringUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.service.PostcodeMappingService;

@ExtendWith(SpringExtension.class)
public class PostcodeMappingControllerTest {

  @InjectMocks PostcodeMappingController postcodeMappingController;
  @Mock PostcodeMappingService postcodeService;
  private static List<String> postcodes = null;
  private static PostcodeMapping postcodeMapping = null;
  private static String serviceName = "Nhs Halton CCG";

  @BeforeAll
  public static void initialise() {
    postcodes = Arrays.asList("EX11SR");
    postcodeMapping = new PostcodeMapping();
    postcodeMapping.setEasting(12766);
    postcodeMapping.setNorthing(456);
    postcodeMapping.setName(serviceName);
  }

  @Test
  public void testPostcodeMappingHome() {
    ResponseEntity<?> response = postcodeMappingController.getPostcodeMappingHome();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Welcome to postcode details search service", response.getBody());
  }

  @Test
  public void testPostcodesWithName() throws InvalidPostcodeException {
    when(postcodeService.getByPostCodesAndName(postcodes, serviceName))
        .thenReturn(Arrays.asList(postcodeMapping));
    ResponseEntity<?> response =
        postcodeMappingController.getPostcodeMapping(postcodes, serviceName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testPostcodes() throws InvalidPostcodeException {
    when(postcodeService.getByPostCodes(postcodes)).thenReturn(Arrays.asList(postcodeMapping));
    ResponseEntity<?> response =
        postcodeMappingController.getPostcodeMapping(postcodes, StringUtil.EMPTY_STRING);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testName() throws InvalidPostcodeException {
    when(postcodeService.getByName(serviceName)).thenReturn(Arrays.asList(postcodeMapping));
    ResponseEntity<?> response = postcodeMappingController.getPostcodeMapping(null, serviceName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testNameInvalidPostcode() {
    assertThrows(
        InvalidPostcodeException.class,
        () -> postcodeMappingController.getPostcodeMapping(null, null));
  }
}
