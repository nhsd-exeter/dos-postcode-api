package uk.nhs.digital.uec.api.exception;

/** This class defines the messages that will be used while throwing relevant exceptions */
public enum ErrorMessageEnum {
  INVALID_POSTCODE("Postcode is provided but it is invalid"),
  NO_LOCATION_FOUND("No location details found for the given name or postcode"),
  NO_PARAMS_PROVIDED("No postcode or name provided");

  private String message;

  private ErrorMessageEnum(String s) {
    message = s;
  }

  public String getMessage() {
    return message;
  }
}
