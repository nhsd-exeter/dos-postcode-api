package uk.nhs.digital.a2si.servicefinder.ccg.service;

import static uk.nhs.digital.a2si.servicefinder.ccg.utils.CcgUtils.validatePostCodes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.digital.a2si.servicefinder.ccg.domain.Ccg;
import uk.nhs.digital.a2si.servicefinder.ccg.exception.InvalidPostcodeException;
import uk.nhs.digital.a2si.servicefinder.ccg.repository.CcgRepository;

/**
 * This is a service class to retrieve data from the repository layer and defines other business logic
 */

@Service
public class CcgServiceImpl implements CcgService {

@Autowired
private CcgRepository ccgRepository;

    @Override
    public List<Ccg> getByPostCodes(List<String> postCodes) throws InvalidPostcodeException {
            return ccgRepository.findByPostCodeIn(validatePostCodes(postCodes)).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Ccg> getByName(String name) {
            return ccgRepository.findByName(name).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Ccg> getByPostCodesAndName(List<String> postCodes, String name) throws InvalidPostcodeException {
        return ccgRepository.findByPostCodeInAndName(validatePostCodes(postCodes), name).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Ccg> getAll() {
        return ccgRepository.findAll();
    }

}
