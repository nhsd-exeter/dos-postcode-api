package uk.nhs.digital.uec.dos.api.dospostcodeapi.service;

import static uk.nhs.digital.uec.dos.api.dospostcodeapi.utils.PostcodeUtils.validatePostCodes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.digital.uec.dos.api.dospostcodeapi.domain.Postcode;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.repository.PostcodeRepository;

/**
 * This is a service class to retrieve data from the repository layer and defines other business logic
 */

@Service
public class PostcodeServiceImpl implements PostcodeService {

@Autowired
private PostcodeRepository PostcodeRepository;

    @Override
    public List<Postcode> getByPostCodes(List<String> postCodes) throws InvalidPostcodeException {
            return PostcodeRepository.findByPostCodeIn(validatePostCodes(postCodes)).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Postcode> getByName(String name) {
            return PostcodeRepository.findByName(name).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Postcode> getByPostCodesAndName(List<String> postCodes, String name) throws InvalidPostcodeException {
        return PostcodeRepository.findByPostCodeInAndName(validatePostCodes(postCodes), name).stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Postcode> getAll() {
        return PostcodeRepository.findAll();
    }

}
