package uk.nhs.digital.uec.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.nhs.digital.uec.api.config.DynamoConfig;

@SpringBootApplication
@Import(DynamoConfig.class)
public class PostcodeMappingApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(PostcodeMappingApplication.class);
    app.setAdditionalProfiles("ssl");
    app.run(args);
  }
}
