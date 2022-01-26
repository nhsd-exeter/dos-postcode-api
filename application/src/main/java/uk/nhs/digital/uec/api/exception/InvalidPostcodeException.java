package uk.nhs.digital.uec.api.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidPostcodeException extends PostcodeMappingException {

  public InvalidPostcodeException(String message) {
    super(message);
  }
}
