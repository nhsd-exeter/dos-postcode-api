package uk.nhs.digital.uec.api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.model.RegionRecord;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.repository.PostcodeMappingRepository;

/**
 * This is a service class to retrieve data from the repository layer and defines other business
 * logic
 */
@Service
@Slf4j
public class PostcodeMappingServiceImpl implements PostcodeMappingService {

  @Autowired private PostcodeMappingRepository postcodeMappingRepository;
  @Autowired private ValidationService validationService;
  @Autowired private RegionMapper regionMapper;

  @Override
  public List<PostcodeMapping> getByPostCodes(List<String> postCodes)
      throws InvalidPostcodeException, NotFoundException {
    log.info("Validating postcode input");
    List<String> validPostcodes = validationService.validatePostCodes(postCodes);
    log.info("Attempting to get postcode mapping from database - getByPostCodes");
    List<PostcodeMapping> location =
        validPostcodes.stream()
            .map(this::getByPostcode)
            .filter(Objects::nonNull)
            .map(this::mapPostCodeToRegion)
            .collect(Collectors.toList());
    log.info("Validating response, returning locations");
    return validationService.validateAndReturn(location);
  }

  @Override
  public List<PostcodeMapping> getByName(String name)
      throws InvalidParameterException, NotFoundException {
    log.info("Attempting to get postcode mapping from database - getByName");
    List<PostcodeMapping> location =
        postcodeMappingRepository.findByName(name)
          .stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::mapPostCodeToRegion)
            .collect(Collectors.toList());
    log.info("Validating response, returning locations");
    return validationService.validateAndReturn(location);
  }

  @Override
  public List<PostcodeMapping> getByPostCodesAndName(List<String> postCodes, String name)
      throws InvalidPostcodeException, NotFoundException {
    log.info("Validating postcode input");
    List<String> validPostcodes = validationService.validatePostCodes(postCodes);
    log.info("Attempting to get postcode mapping from database - getByPostcodeAndName");
    List<PostcodeMapping> location =
        validPostcodes.stream()
            .map(t -> getByPostcodeAndName(t, name))
            .filter(Objects::nonNull)
            .map(this::mapPostCodeToRegion)
            .collect(Collectors.toList());
    log.info("Validating response, returning locations");
    return validationService.validateAndReturn(location);
  }

  private PostcodeMapping getByPostcode(String postcode) {
    PostcodeMapping mapping;
    log.info("Finding mapping by postcode {}",postcode);
    Optional<PostcodeMapping> findByPostCodeOptional =
        postcodeMappingRepository.findByPostCode(postcode);
    mapping = findByPostCodeOptional.orElse(null);
    log.info("Mapping for {} is {}",postcode,mapping);
    return mapping;
  }

  private PostcodeMapping getByPostcodeAndName(String postcode, String name) {
    PostcodeMapping mapping;
    log.info("Finding mapping by postcode and name");
    Optional<PostcodeMapping> findByPostCodeAndNameOptional =
        postcodeMappingRepository.findByPostCodeAndName(postcode, name);
    mapping = findByPostCodeAndNameOptional.orElse(null);
    log.info("Mapping for {} is {}",postcode,mapping);
    return mapping;
  }

  private PostcodeMapping mapPostCodeToRegion(PostcodeMapping postcodeMapping){
      log.info("Finding region details for {}",postcodeMapping.getPostCode());
      RegionRecord regionRecord = regionMapper.getRegionRecord(postcodeMapping.getPostCode());
      if(Objects.isNull(regionRecord)){
        return postcodeMapping;
      }
      postcodeMapping.setRegion(regionRecord.getRegion());
      postcodeMapping.setSubRegion(regionRecord.getSubRegion());
      return postcodeMapping;
  }
}
