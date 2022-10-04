package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;

public interface ValidationService {

  public List<String> validatePostCodes(List<String> postCodes)
      throws InvalidPostcodeException, NotFoundException;

  public boolean validatePostCode(String postCode);

  public List<PostcodeMapping> validateAndReturn(List<PostcodeMapping> location)
      throws NotFoundException;
}
