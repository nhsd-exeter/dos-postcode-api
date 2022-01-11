package uk.nhs.digital.uec.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class PostcodeMappingHomeController {

  @GetMapping
  public ResponseEntity<String> getPostcodeMappingHome() {
    return ResponseEntity.ok("Welcome to postcode details search service");
  }
}
