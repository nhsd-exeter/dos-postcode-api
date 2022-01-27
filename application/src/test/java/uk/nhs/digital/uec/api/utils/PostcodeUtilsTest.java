package uk.nhs.digital.uec.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.formatPostCodeWithSpace;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.formatPostCodeWithoutSpace;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.validateAndReturn;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.validatePostCode;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.validatePostCodes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;

public class PostcodeUtilsTest {

  private static final String postCodeWithSpace = "AL8 6JL";

  @Test
  public void testFormatPostCodeWithSpace() {

    String postCode = "AL86JL";
    String formatPostCode1 = formatPostCodeWithSpace(postCode);
    assertEquals(postCodeWithSpace, formatPostCode1);
  }

  @Test
  public void testFormatPostCodeWithoutSpace() {
    String formatPostCode1 = formatPostCodeWithoutSpace(postCodeWithSpace);
    assertEquals("AL86JL", formatPostCode1);
  }

  @Test
  public void testInvalidPostCode() {
    String postCode = "SL222 9PPP";
    boolean validatePostCode = validatePostCode(postCode);
    assertFalse(validatePostCode);
  }

  @Test
  public void testInvalidPostCode2() {
    String postCode = "N4%2QZ";
    boolean validatePostCode = validatePostCode(postCode);
    assertFalse(validatePostCode);
  }

  @Test
  public void invalidPostcodeTest() {
    InvalidPostcodeException invalidPostcodeException =
        assertThrows(
            InvalidPostcodeException.class, () -> validatePostCodes(Arrays.asList("N4%2QZ")));
    assertNotNull(
        ErrorMessageEnum.INVALID_POSTCODE.getMessage(), invalidPostcodeException.getMessage());
  }

  @Test
  public void invalidPostcodeOnEmptyPostcodeListTest()
      throws InvalidPostcodeException, NotFoundException {
    List<String> postCodes = validatePostCodes(Collections.<String>emptyList());
    assertEquals(Collections.emptyList(), postCodes);
  }

  @Test
  public void testValidPostCode() {
    boolean validatePostCode = validatePostCode(postCodeWithSpace);
    assertTrue(validatePostCode);
  }

  @Test
  public void testValidPostCode2() {
    String postCode = "N42QZ";
    boolean validatePostCode = validatePostCode(postCode);
    assertTrue(validatePostCode);
  }

  @Test
  public void validateAndReturnEmptyListTest() {
    NotFoundException notFoundException =
        assertThrows(NotFoundException.class, () -> validateAndReturn(Collections.emptyList()));
    assertEquals(ErrorMessageEnum.NO_LOCATION_FOUND.getMessage(), notFoundException.getMessage());
  }

  @Test
  public void validateAndReturnTest() throws NotFoundException {
    PostcodeMapping mapping = new PostcodeMapping();
    mapping.setPostCode("EX1 1SR");
    mapping.setEasting(123456);
    mapping.setEasting(654321);
    List<PostcodeMapping> response = validateAndReturn(Arrays.asList(mapping));
    assertNotNull(response);
    Optional<PostcodeMapping> mappingOptional = response.stream().findAny();
    PostcodeMapping mappingResponse =
        mappingOptional.isPresent() ? mappingOptional.get() : new PostcodeMapping();
    assertEquals("EX1 1SR", mappingResponse.getPostCode());
  }
}
