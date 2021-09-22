package uk.nhs.digital.uec.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** RestController for Postcode Mapping service */
@RestController
public class HomeController {

  @GetMapping("/home")
  public ResponseEntity<String> getPostcodeMappingHome() {
    return ResponseEntity.ok("Welcome to postcode details search service");
  }
}
