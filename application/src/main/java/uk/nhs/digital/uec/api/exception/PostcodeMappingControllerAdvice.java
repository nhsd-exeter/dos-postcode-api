package uk.nhs.digital.uec.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
    Controller advice class for ccg
 */

@ControllerAdvice
@Slf4j
public class PostcodeMappingControllerAdvice {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleStatusException(ResponseStatusException ex) {
        log.error(ex.getReason(), ex);
        log.error(ExceptionUtils.getStackTrace(ex));
        return ResponseEntity.status(ex.getStatus())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorMessage(ex.getStatus(), ex.getMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception exception) {
        log.error(ExceptionUtils.getStackTrace(exception));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
    }

    @ExceptionHandler(InvalidPostcodeException.class)
    public ResponseEntity handleInvalidPostCodeException(Exception exception) {
        log.error(ExceptionUtils.getStackTrace(exception));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(new ErrorMessage(HttpStatus.BAD_REQUEST, "Please enter a valid postcode"));
    }

}
