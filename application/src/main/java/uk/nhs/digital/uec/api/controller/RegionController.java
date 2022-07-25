package uk.nhs.digital.uec.api.controller;


import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.RegionService;

import static uk.nhs.digital.uec.api.constants.SwaggerConstants.POSTCODES_DESC;

/**
 * RestController for Region Mapping service
 */
@RestController
@RequestMapping("/api/regions")
@PreAuthorize("hasAnyRole('POSTCODE_API_ACCESS')")
@Slf4j
public class RegionController {

  @Autowired
  private RegionService regionService;

  @GetMapping()
  public ResponseEntity<?> getAllRegions(@ApiParam(POSTCODES_DESC) @RequestParam(name = "postcode", required = false) String postcode) throws InvalidParameterException, NotFoundException, InvalidPostcodeException {
    long start = System.currentTimeMillis();
    if (StringUtils.isNotBlank(postcode)){
      log.info("Processing Get All Regions By PostCode: {}",postcode);
      List<PostcodeMapping>  postcodeMappingList =   regionService.getRegionByPostCode(postcode);
      log.info("Preparing response {}ms", System.currentTimeMillis() - start);
      return ResponseEntity.ok(postcodeMappingList);
    }
    Map<String, List<String>> regions = regionService.getAllRegions();
    log.info("Processing Get All Regions");
    log.info("Preparing response {}ms", System.currentTimeMillis() - start);
    return ResponseEntity.ok(regions);
  }
}
