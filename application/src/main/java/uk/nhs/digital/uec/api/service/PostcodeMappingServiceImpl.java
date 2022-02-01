package uk.nhs.digital.uec.api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.repository.PostcodeMappingRepository;

/**
 * This is a service class to retrieve data from the repository layer and defines other business
 * logic
 */
@Service
public class PostcodeMappingServiceImpl implements PostcodeMappingService {

  @Autowired private PostcodeMappingRepository postcodeMappingRepository;
  @Autowired private ValidationService validationService;

  @Override
  public List<PostcodeMapping> getByPostCodes(List<String> postCodes)
      throws InvalidPostcodeException, NotFoundException {
    List<String> validPostcodes = validationService.validatePostCodes(postCodes);
    List<PostcodeMapping> location =
        validPostcodes.stream()
            .map(this::getByPostcode)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return validationService.validateAndReturn(location);
  }

  @Override
  public List<PostcodeMapping> getByName(String name)
      throws InvalidParameterException, NotFoundException {
    List<PostcodeMapping> location =
        postcodeMappingRepository.findByName(name).stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return validationService.validateAndReturn(location);
  }

  @Override
  public List<PostcodeMapping> getByPostCodesAndName(List<String> postCodes, String name)
      throws InvalidPostcodeException, NotFoundException {
    List<String> validPostcodes = validationService.validatePostCodes(postCodes);
    List<PostcodeMapping> location =
        validPostcodes.stream()
            .map(t -> getByPostcodeAndName(t, name))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return validationService.validateAndReturn(location);
  }

  private PostcodeMapping getByPostcode(String postcode) {
    Optional<PostcodeMapping> findByPostCodeOptional =
        postcodeMappingRepository.findByPostCode(postcode);
    return findByPostCodeOptional.isPresent() ? findByPostCodeOptional.get() : null;
  }

  private PostcodeMapping getByPostcodeAndName(String postcode, String name) {
    Optional<PostcodeMapping> findByPostCodeAndNameOptional =
        postcodeMappingRepository.findByPostCodeAndName(postcode, name);
    return findByPostCodeAndNameOptional.isPresent() ? findByPostCodeAndNameOptional.get() : null;
  }
}
