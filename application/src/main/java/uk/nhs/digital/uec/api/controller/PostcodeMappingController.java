package uk.nhs.digital.uec.api.controller;

import static uk.nhs.digital.uec.api.constants.SwaggerConstants.NAME_DESC;
import static uk.nhs.digital.uec.api.constants.SwaggerConstants.POSTCODES_DESC;
import static uk.nhs.digital.uec.api.exception.ErrorMessageEnum.NO_PARAMS_PROVIDED;

import io.swagger.annotations.ApiParam;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.PostcodeMappingService;

/** RestController for Postcode Mapping service */
@RestController
@RequestMapping("/api/search")
@PreAuthorize("hasAnyRole('POSTCODE_API_ACCESS')")
@Slf4j
public class PostcodeMappingController {

  @Autowired private PostcodeMappingService postcodeMappingService;

  @Value("${invalid.postcode}")
  private String validPostCodeMessage;

  @GetMapping()
  public ResponseEntity<List<PostcodeMapping>> getPostcodeMapping(
      @ApiParam(POSTCODES_DESC) @RequestParam(name = "postcodes", required = false)
          List<String> postCodes,
      @ApiParam(NAME_DESC) @RequestParam(name = "name", required = false) String name)
      throws InvalidPostcodeException, InvalidParameterException, NotFoundException {
    long start  = System.currentTimeMillis();
    List<PostcodeMapping> postcodeMapping = null;
    if (CollectionUtils.isNotEmpty(postCodes) && StringUtils.isNotBlank(name)) {
      log.info("Processing get By PostCodes And Name: {} and name: {}",postCodes, name);
      postcodeMapping = postcodeMappingService.getByPostCodesAndName(postCodes, name);
    } else if (CollectionUtils.isNotEmpty(postCodes) && StringUtils.isBlank(name)) {
      log.info("Processing get By PostCodes {}",postCodes);
      postcodeMapping = postcodeMappingService.getByPostCodes(postCodes);
    } else if (StringUtils.isNotBlank(name) && CollectionUtils.isEmpty(postCodes)) {
      log.info("Processing get By name {}",name);
      postcodeMapping = postcodeMappingService.getByName(name);
    } else {
      log.error("Variables not submitted {}", NO_PARAMS_PROVIDED.getMessage());
      throw new InvalidParameterException(NO_PARAMS_PROVIDED.getMessage());
    }
    log.info("Preparing response {}ms",System.currentTimeMillis() - start);
    return ResponseEntity.ok(postcodeMapping);
  }
}
