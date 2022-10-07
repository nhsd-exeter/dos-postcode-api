package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan
public class BeanConfig {

  @Bean
  public ExecutorService executor() {
    return Executors.newFixedThreadPool(15);
  }


}
