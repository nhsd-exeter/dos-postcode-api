package uk.nhs.digital.uec.api.controller;


import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.digital.uec.api.service.RegionService;

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
  public ResponseEntity<Map<String, List<String>>> getAllRegions() {
    long start = System.currentTimeMillis();
    Map<String, List<String>> regions = regionService.getAllRegions();
    log.info("Preparing response {}ms", System.currentTimeMillis() - start);
    return ResponseEntity.ok(regions);
  }
}
