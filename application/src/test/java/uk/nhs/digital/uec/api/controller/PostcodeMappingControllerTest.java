package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
import uk.nhs.digital.uec.api.service.PostcodeMappingService;

@ExtendWith(SpringExtension.class)
public class PostcodeMappingControllerTest {

  @InjectMocks PostcodeMappingController postcodeMappingController;
  @Mock PostcodeMappingService postcodeService;
  private static List<String> postcodes = null;
  private static PostcodeMapping postcodeMapping = null;

  @BeforeAll
  public static void initialise() {
    postcodes = Arrays.asList("EX11SR");
    postcodeMapping = new PostcodeMapping();
    postcodeMapping.setEasting(12766);
    postcodeMapping.setNorthing(456);
    postcodeMapping.setName("NHS Halton CCG");
  }

  @Test
  public void testPostcodesWithName() throws Exception {
    when(postcodeService.getByPostCodesAndName(postcodes, "NHS Halton CCG"))
        .thenReturn(Arrays.asList(postcodeMapping));
    ResponseEntity<?> response =
        postcodeMappingController.getPostcodeMapping(postcodes, "NHS Halton CCG");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testPostcodes() throws Exception {
    when(postcodeService.getByPostCodes(postcodes)).thenReturn(Arrays.asList(postcodeMapping));
    ResponseEntity<?> response =
        postcodeMappingController.getPostcodeMapping(postcodes, "NHS Halton CCG");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void testName() throws Exception {
    when(postcodeService.getByName("NHS Halton CCG")).thenReturn(Arrays.asList(postcodeMapping));
    ResponseEntity<?> response =
        postcodeMappingController.getPostcodeMapping(null, "NHS Halton CCG");

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
