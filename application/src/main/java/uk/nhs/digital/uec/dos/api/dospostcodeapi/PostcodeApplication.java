package uk.nhs.digital.uec.dos.api.dospostcodeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.config.DynamoConfig;

@SpringBootApplication
@ComponentScan({"uk.nhs.digital.uec.dos.api.dospostcodeapi","uk.nhs.digital.a2si.servicefinder.common"})
@Import(DynamoConfig.class)
public class PostcodeApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(PostcodeApplication.class);
        app.setAdditionalProfiles("ssl");
        app.run(args);

  }
}
