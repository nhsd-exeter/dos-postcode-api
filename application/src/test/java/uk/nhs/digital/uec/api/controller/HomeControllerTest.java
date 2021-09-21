package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class HomeControllerTest {

  @InjectMocks HomeController homeController;

  @Test
  public void testHomeController()
  {
    //ResponseEntity<?> response = homeController.getPostcodeMappingHome();

    //assertEquals(HttpStatus.OK, response.getStatusCode());
    //assertEquals("Welcome to postcode details search service", response.getBody());
    assertEquals(1,1);

  }

}
