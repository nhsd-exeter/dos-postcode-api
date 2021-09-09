package uk.nhs.digital.uec.api.controller;

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
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.service.PostcodeMappingService;

/** RestController for Postcode Mapping service */
@RestController
@RequestMapping("/api/search")
@PreAuthorize("hasAnyRole('ROLE_POSTCODE')")
@Slf4j
public class PostcodeMappingController {

  @Autowired private PostcodeMappingService postcodeMappingService;

  @Value("${invalid.postcode}")
  private String validPostCodeMessage;

  @GetMapping("/home")
  public ResponseEntity<String> getPostcodeMappingHome() {
    return ResponseEntity.ok("Welcome to postcode details search service");
  }

  @GetMapping()
  public ResponseEntity<List<PostcodeMapping>> getPostcodeMapping(
      @RequestParam(name = "postcodes", required = false) List<String> postCodes,
      @RequestParam(name = "name", required = false) String name)
      throws InvalidPostcodeException {
    List<PostcodeMapping> postcodeMapping = null;
    if (CollectionUtils.isNotEmpty(postCodes) && StringUtils.isNotBlank(name)) {
      postcodeMapping = postcodeMappingService.getByPostCodesAndName(postCodes, name);
    } else if (CollectionUtils.isNotEmpty(postCodes) && StringUtils.isBlank(name)) {
      postcodeMapping = postcodeMappingService.getByPostCodes(postCodes);
    } else if (StringUtils.isNotBlank(name) && CollectionUtils.isEmpty(postCodes)) {
      postcodeMapping = postcodeMappingService.getByName(name);
    } else {
      throw new InvalidPostcodeException();
    }
    return ResponseEntity.ok(postcodeMapping);
  }
}
