package uk.nhs.digital.uec.api.controller;

import static uk.nhs.digital.uec.api.constants.SwaggerConstants.*;

import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.model.PostcodeMappingDTO;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.service.RegionService;

/** RestController for Region Mapping service */
@RestController
@RequestMapping("/api/regions")
@Slf4j(topic = "Postcode API - Region controller")
public class RegionController {

  @Autowired private RegionService regionService;

  @GetMapping
  @PreAuthorize("hasAnyRole('POSTCODE_API_ACCESS')")
  public ResponseEntity getAllRegions() {
    long start = System.currentTimeMillis();
    Map<String, List<String>> regions = regionService.getAllRegions();
    log.info("Processing Get All Regions");
    log.info("Preparing response {}ms", System.currentTimeMillis() - start);
    return new ResponseEntity(regions, HttpStatus.OK);
  }

  @GetMapping(params = {"postcodes"})
  @PreAuthorize("hasAnyRole('POSTCODE_API_ACCESS')")
  public ResponseEntity getRegionDetailsByPostCodes(
      @ApiParam(POSTCODES_DESC) @RequestParam(name = "postcodes", required = false)
          List<String> postcodes) {
    try {
      long start = System.currentTimeMillis();
      List<PostcodeMappingDTO> postcodeMappingList = regionService.getRegionByPostCodes(postcodes);
      log.info("Processing Get Region Details By Given PostCodes:{}", postcodes);
      log.info("Preparing response {}ms", System.currentTimeMillis() - start);
      return new ResponseEntity(postcodeMappingList, HttpStatus.OK);
    } catch (InvalidParameterException ex) {
      log.error("InvalidParamException happened while fetching postcode: {}", ex.getMessage());
    } catch (NotFoundException ex) {
      log.error("NotFoundException happened while fetching postcode: {}", ex.getMessage());
    } catch (InvalidPostcodeException ex) {
      log.error("InvalidPostCodeException happened while fetching postcode: {}", ex.getMessage());
    } catch (Exception ex) {
      log.error("Exception happened while fetching postcode: {}", ex.getMessage());
    }
    return new ResponseEntity(new PostcodeMappingDTO(), HttpStatus.BAD_REQUEST);
  }

  @GetMapping(params = {"postcode"})
  @PreAuthorize("hasAnyRole('POSTCODE_API_ACCESS')")
  public ResponseEntity getRegionDetailsByPostCode(
      @ApiParam(POSTCODE_DESC) @RequestParam(name = "postcode", required = false) String postcode) {
    PostcodeMappingDTO postcodeMapping = new PostcodeMappingDTO();
    postcodeMapping.setPostcode(postcode);
    try {
      long start = System.currentTimeMillis();
      postcodeMapping = regionService.getRegionByPostCode(postcode);
      log.info("Preparing response {}ms", System.currentTimeMillis() - start);
      log.info("Processing Get Region Details By Given PostCode:{}", postcode);
      return new ResponseEntity(postcodeMapping, HttpStatus.OK);
    } catch (InvalidParameterException ex) {

      log.error("InvalidParamException happened while fetching postcode: {}", ex.getMessage());
    } catch (NotFoundException ex) {
      log.error("NotFoundException happened while fetching postcode: {}", ex.getMessage());
    } catch (InvalidPostcodeException ex) {
      log.error("InvalidPostCodeException happened while fetching postcode: {}", ex.getMessage());
    } catch (Exception ex) {
      log.error("Exception happened while fetching postcode: {}", ex.getMessage());
    }
    return new ResponseEntity(postcodeMapping, HttpStatus.BAD_REQUEST);
  }
}
