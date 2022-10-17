package uk.nhs.digital.uec.api.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.model.ICBRecord;

@Slf4j
@Component
public class ICBUtil implements Callable<List<ICBRecord>> {

  @Autowired private ResourceLoader resourceLoader;

  @Override
  public List<ICBRecord> call() {
    List<ICBRecord> icbRecordList = new ArrayList<>();
    Long start = System.currentTimeMillis();
    log.info("loading ICB csv async");
    Resource resource = resourceLoader.getResource("classpath:DOS_Email_and_ICB.csv");
    try (BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      CsvToBean<ICBRecord> csvBean =
          new CsvToBeanBuilder<ICBRecord>(bufferedReader).withType(ICBRecord.class).build();
      icbRecordList = csvBean.parse().stream().sorted().collect(Collectors.toList());
      log.info("Successfully loaded ICB csv. {}ms", System.currentTimeMillis() - start);
      return icbRecordList;
    } catch (IOException e) {
      log.error("Unable to collate regions: {}", e.getMessage());
      return icbRecordList;
    }
  }
}
