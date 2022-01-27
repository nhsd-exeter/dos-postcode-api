package uk.nhs.digital.uec.api.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostcodeMappingException extends Exception {

  private static final long serialVersionUID = 6802170828962110458L;

  public PostcodeMappingException(String message) {
    super(message);
  }
}
