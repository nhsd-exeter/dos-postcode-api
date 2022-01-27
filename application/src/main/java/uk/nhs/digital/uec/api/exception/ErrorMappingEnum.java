package uk.nhs.digital.uec.api.exception;

import java.util.EnumMap;

/** This class maps the validation error code with the relevant message */
public class ErrorMappingEnum {

  enum ValidationCodes {
    VAL001("VAL-001"),
    VAL002("VAL-002");

    private String validationCode;

    private ValidationCodes(String s) {
      validationCode = s;
    }

    public String getValidationCode() {
      return validationCode;
    }
  }

  public static EnumMap<ValidationCodes, String> getValidationEnum() {
    EnumMap<ValidationCodes, String> codesMapping = new EnumMap<>(ValidationCodes.class);
    codesMapping.put(ValidationCodes.VAL001, ErrorMessageEnum.NO_PARAMS_PROVIDED.getMessage());
    codesMapping.put(ValidationCodes.VAL002, ErrorMessageEnum.INVALID_POSTCODE.getMessage());
    return codesMapping;
  }
}
