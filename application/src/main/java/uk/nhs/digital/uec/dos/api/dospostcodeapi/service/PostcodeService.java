package uk.nhs.digital.uec.dos.api.dospostcodeapi.service;

import java.util.List;

import uk.nhs.digital.uec.dos.api.dospostcodeapi.domain.Postcode;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.exception.InvalidPostcodeException;

public interface PostcodeService {

    public List<Postcode> getByPostCodesAndName(List<String> postCodes, String name) throws InvalidPostcodeException;
    public List<Postcode> getByPostCodes(List<String> postCodes) throws InvalidPostcodeException;
    public List<Postcode> getByName(String name);
    public List<Postcode> getAll();

}
