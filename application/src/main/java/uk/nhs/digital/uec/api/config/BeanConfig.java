package uk.nhs.digital.uec.api.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.uec.api.service.RegionMapper;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan
public class BeanConfig {

  @Bean
  public RegionMapper getRegionMapper() {
    return new RegionMapper();
  }

  @Bean
  public ExecutorService executor(){
    return Executors.newSingleThreadExecutor();
  }

  @Bean
  public ModelMapper modelMapper(){return new ModelMapper();}

}
