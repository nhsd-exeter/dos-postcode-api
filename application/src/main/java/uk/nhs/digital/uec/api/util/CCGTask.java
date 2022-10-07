package uk.nhs.digital.uec.api.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.model.CCGRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
@Slf4j(topic = "Postcode API - CCG Task")
@NoArgsConstructor
@Setter
@Getter
public class CCGTask implements Callable<List<CCGRecord>> {

  private String fileName;

  @Autowired
  private ResourceLoader resourceLoader;

  public CCGTask(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public List<CCGRecord> call() {
    List<CCGRecord> ccgRecordList = new ArrayList<>();
    Long start = System.currentTimeMillis();
    log.info("loading CCG csv async");
    try (BufferedReader bufferedReader =
           new BufferedReader(
             new InputStreamReader(getStream(getFileName()), StandardCharsets.UTF_8))) {
      CsvToBean<CCGRecord> csvBean =
        new CsvToBeanBuilder<CCGRecord>(bufferedReader)
          .withType(CCGRecord.class).build();
      log.info("Parsed {} CCG csv file to pojos now sorting ... {}ms", fileName, System.currentTimeMillis() - start);
      ccgRecordList = csvBean.parse()
        .stream()
        .sorted()
        .collect(Collectors.toList());
      log.info("Successfully loaded {} CCG csv. {}ms", fileName, System.currentTimeMillis() - start);
      return ccgRecordList;
    } catch (Exception e) {
      log.error("Unable to collate CCGs: {}", e.getMessage());
      return ccgRecordList;
    }
  }

  private InputStream getStream(String fileName) throws IOException {
    return resourceLoader.getResource("classpath:" + fileName).getInputStream();
  }
}
