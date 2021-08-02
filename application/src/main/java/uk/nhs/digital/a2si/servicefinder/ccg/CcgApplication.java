package uk.nhs.digital.a2si.servicefinder.ccg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import uk.nhs.digital.a2si.servicefinder.ccg.config.DynamoConfig;

@SpringBootApplication
@ComponentScan({"uk.nhs.digital.a2si.servicefinder.ccg","uk.nhs.digital.a2si.servicefinder.common"})
@Import(DynamoConfig.class)
public class CcgApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(CcgApplication.class);
        app.setAdditionalProfiles("ssl");
        app.run(args);

  }
}
