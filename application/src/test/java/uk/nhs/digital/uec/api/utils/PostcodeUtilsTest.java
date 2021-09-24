package uk.nhs.digital.uec.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.formatPostCodeWithSpace;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.formatPostCodeWithoutSpace;
import static uk.nhs.digital.uec.api.util.PostcodeUtils.validatePostCode;

import org.junit.jupiter.api.Test;

public class PostcodeUtilsTest {

  @Test
  public void testFormatPostCodeWithSpace() {

    String postCode = "AL86JL";
    String formatPostCode1 = formatPostCodeWithSpace(postCode);
    assertEquals("AL8 6JL", formatPostCode1);
  }

  @Test
  public void testFormatPostCodeWithoutSpace() {

    String postCode = "AL8 6JL";
    String formatPostCode1 = formatPostCodeWithoutSpace(postCode);
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
  public void testValidPostCode() {
    String postCode = "AL8 6JL";
    boolean validatePostCode = validatePostCode(postCode);
    assertTrue(validatePostCode);
  }

  @Test
  public void testValidPostCode2() {
    String postCode = "N42QZ";
    boolean validatePostCode = validatePostCode(postCode);
    assertTrue(validatePostCode);
  }
}
