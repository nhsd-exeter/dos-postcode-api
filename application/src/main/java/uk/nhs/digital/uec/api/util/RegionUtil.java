package uk.nhs.digital.uec.api.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.model.RegionRecord;

@Slf4j
@Component
public class RegionUtil implements Callable<List<RegionRecord>> {

  @Autowired private ResourceLoader resourceLoader;

  @Override
  public List<RegionRecord> call() {
    Long start = System.currentTimeMillis();
    log.info("loading Region csv async");
    Resource resource = resourceLoader.getResource("classpath:postcode_regions.csv");
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      CsvToBean<RegionRecord> csvBean =
          new CsvToBeanBuilder<RegionRecord>(bufferedReader).withType(RegionRecord.class).build();
      log.info("Successfully loaded region csv. {}ms", System.currentTimeMillis() - start);
      return csvBean.parse();
    } catch (IOException e) {
      log.error("Unable to collate regions: {}", e.getMessage());
      return Collections.emptyList();
    }
  }
}
