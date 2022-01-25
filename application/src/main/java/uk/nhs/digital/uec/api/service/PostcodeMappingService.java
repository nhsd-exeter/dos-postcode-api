package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;

public interface PostcodeMappingService {

  List<PostcodeMapping> getByPostCodesAndName(List<String> postCodes, String name)
      throws InvalidPostcodeException, InvalidParameterException, NotFoundException;

  List<PostcodeMapping> getByPostCodes(List<String> postCodes)
      throws InvalidPostcodeException, InvalidParameterException, NotFoundException;

  List<PostcodeMapping> getByName(String name) throws InvalidParameterException, NotFoundException;

  List<PostcodeMapping> getAll();
}
