package uk.nhs.digital.uec.api.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotFoundException extends Exception {

  public NotFoundException(String message) {
    super(message);
  }
}
