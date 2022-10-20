package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.impl.ValidationServiceImpl;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ValidationServiceTest {

  private static final String postCodeWithSpace = "AL8 6JL";
  @InjectMocks private ValidationServiceImpl validationServiceImpl;

  @Test
  public void testFormatPostCodeWithSpace() {
    String postCode = "AL86JL";
    String formatPostCode1 = validationServiceImpl.formatPostCodeWithSpace(postCode);
    assertEquals(postCodeWithSpace, formatPostCode1);
  }

  @Test
  public void testFormatPostCodeWithoutSpace() {
    String formatPostCode1 = validationServiceImpl.formatPostCodeWithoutSpace(postCodeWithSpace);
    assertEquals("AL86JL", formatPostCode1);
  }

  @Test
  public void testInvalidPostCode() {
    String postCode = "SL222 9PPP";
    boolean validatePostCode = validationServiceImpl.validatePostCode(postCode);
    assertFalse(validatePostCode);
  }

  @Test
  public void testInvalidPostCode2() {
    String postCode = "N4%2QZ";
    boolean validatePostCode = validationServiceImpl.validatePostCode(postCode);
    assertFalse(validatePostCode);
  }

  @Test
  public void invalidPostcodeTest() {
    InvalidPostcodeException invalidPostcodeException =
        assertThrows(
            InvalidPostcodeException.class,
            () -> validationServiceImpl.validatePostCodes(Arrays.asList("N4%2QZ")));
    assertNotNull(
        ErrorMessageEnum.INVALID_POSTCODE.getMessage(), invalidPostcodeException.getMessage());
  }

  @Test
  public void invalidPostcodeOnEmptyPostcodeListTest()
      throws InvalidPostcodeException, NotFoundException {
    List<String> postCodes =
        validationServiceImpl.validatePostCodes(Collections.<String>emptyList());
    assertEquals(Collections.emptyList(), postCodes);
  }

  @Test
  public void testValidPostCode() {
    boolean validatePostCode = validationServiceImpl.validatePostCode(postCodeWithSpace);
    assertTrue(validatePostCode);
  }

  @Test
  public void testValidPostCode2() {
    String postCode = "N42QZ";
    boolean validatePostCode = validationServiceImpl.validatePostCode(postCode);
    assertTrue(validatePostCode);
  }

  @Test
  public void validateAndReturnEmptyListTest() {
    NotFoundException notFoundException =
        assertThrows(
            NotFoundException.class,
            () -> validationServiceImpl.validateAndReturn(Collections.emptyList()));
    assertEquals(ErrorMessageEnum.NO_LOCATION_FOUND.getMessage(), notFoundException.getMessage());
  }

  @Test
  public void validateAndReturnTest() throws NotFoundException {
    PostcodeMapping mapping = new PostcodeMapping();
    mapping.setPostcode("EX1 1SR");
    mapping.setEasting(123456);
    mapping.setEasting(654321);
    List<PostcodeMapping> response =
        validationServiceImpl.validateAndReturn(Arrays.asList(mapping));
    assertNotNull(response);
    Optional<PostcodeMapping> mappingOptional = response.stream().findAny();
    PostcodeMapping mappingResponse =
        mappingOptional.isPresent() ? mappingOptional.get() : new PostcodeMapping();
    assertEquals("EX1 1SR", mappingResponse.getPostcode());
  }
}
