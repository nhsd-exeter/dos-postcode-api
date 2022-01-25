package uk.nhs.digital.uec.api.exception;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.nhs.digital.uec.api.domain.ErrorResponse;
import uk.nhs.digital.uec.api.exception.ErrorMappingEnum.ValidationCodes;

/** Controller advice class for postcode mapping details */
@ControllerAdvice
@Slf4j
public class PostcodeMappingControllerAdvice {

  @ExceptionHandler({InvalidPostcodeException.class, InvalidParameterException.class})
  public ResponseEntity<ErrorResponse> handleInvalidPostCodeException(Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));

    ErrorResponse errorResponse = new ErrorResponse();

    Optional<ValidationCodes> validationCodesOptional =
        ErrorMappingEnum.getValidationEnum().entrySet().stream()
            .filter(entry -> exception.getMessage().equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();

    errorResponse.setValidationCode(
        validationCodesOptional.isPresent()
            ? validationCodesOptional.get().getValidationCode()
            : null);
    errorResponse.setMessage(exception.getMessage());
    errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));

    ErrorResponse errorResponse = new ErrorResponse();

    Optional<ValidationCodes> validationCodesOptional =
        ErrorMappingEnum.getValidationEnum().entrySet().stream()
            .filter(entry -> exception.getMessage().equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .findFirst();

    errorResponse.setValidationCode(
        validationCodesOptional.isPresent()
            ? validationCodesOptional.get().getValidationCode()
            : null);
    errorResponse.setMessage(exception.getMessage());
    errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(HttpStatus.NOT_FOUND.value()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleException(Exception exception) {
    log.error(ExceptionUtils.getStackTrace(exception));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
  }
}
