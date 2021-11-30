package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;

public interface PostcodeMappingService {

  List<PostcodeMapping> getByPostCodesAndName(List<String> postCodes, String name)
      throws InvalidPostcodeException;

  List<PostcodeMapping> getByPostCodes(List<String> postCodes) throws InvalidPostcodeException;

  List<PostcodeMapping> getByName(String name);

  List<PostcodeMapping> getAll();
}
