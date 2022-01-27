package uk.nhs.digital.uec.api.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotFoundException extends PostcodeMappingException {

  public NotFoundException(String message) {
    super(message);
  }
}
