package uk.nhs.digital.uec.api.service;

import static uk.nhs.digital.uec.api.util.PostcodeUtils.validatePostCodes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.repository.PostcodeMappingRepository;

/**
 * This is a service class to retrieve data from the repository layer and defines other business
 * logic
 */
@Service
public class PostcodeMappingServiceImpl implements PostcodeMappingService {

  @Autowired private PostcodeMappingRepository postcodeMappingRepository;

  @Override
  public List<PostcodeMapping> getByPostCodes(List<String> postCodes)
      throws InvalidPostcodeException {
    return postcodeMappingRepository.findByPostCodeIn(validatePostCodes(postCodes)).stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    String hello = "hello";
  }

  @Override
  public List<PostcodeMapping> getByName(String name) {
    return postcodeMappingRepository.findByName(name).stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<PostcodeMapping> getByPostCodesAndName(List<String> postCodes, String name)
      throws InvalidPostcodeException {
    return postcodeMappingRepository
        .findByPostCodeInAndName(validatePostCodes(postCodes), name)
        .stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<PostcodeMapping> getAll() {
    return postcodeMappingRepository.findAll();
  }
}
