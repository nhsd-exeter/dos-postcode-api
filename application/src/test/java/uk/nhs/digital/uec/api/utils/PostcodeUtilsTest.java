package uk.nhs.digital.uec.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.formatPostCodeWithSpace;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.formatPostCodeWithoutSpace;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.validatePostCode;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.validatePostCodes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;

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
    assertNotNull(invalidPostcodeException);
  }

  @Test
  public void invalidPostcodeOnEmptyPostcodeListTest() throws InvalidPostcodeException {
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
}
