package uk.nhs.digital.a2si.servicefinder.ccg.service;

import java.util.List;

import uk.nhs.digital.a2si.servicefinder.ccg.domain.Ccg;
import uk.nhs.digital.a2si.servicefinder.ccg.exception.InvalidPostcodeException;

public interface CcgService {

    public List<Ccg> getByPostCodesAndName(List<String> postCodes, String name) throws InvalidPostcodeException;
    public List<Ccg> getByPostCodes(List<String> postCodes) throws InvalidPostcodeException;
    public List<Ccg> getByName(String name);
    public List<Ccg> getAll();

}
