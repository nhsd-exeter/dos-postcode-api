package uk.nhs.digital.uec.api.service;

import java.util.List;

import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;

public interface PostcodeMappingService {

    public List<PostcodeMapping> getByPostCodesAndName(List<String> postCodes, String name) throws InvalidPostcodeException;
    public List<PostcodeMapping> getByPostCodes(List<String> postCodes) throws InvalidPostcodeException;
    public List<PostcodeMapping> getByName(String name);
    public List<PostcodeMapping> getAll();

}
