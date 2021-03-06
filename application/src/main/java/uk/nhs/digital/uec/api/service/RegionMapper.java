package uk.nhs.digital.uec.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.domain.RegionRecord;
import uk.nhs.digital.uec.api.util.RegionUtil;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@Component
public class RegionMapper {

  private List<RegionRecord> recordsList;
  @Autowired
  private ExecutorService executor;
  @Autowired
  private RegionUtil regionUtil;

  @PostConstruct
  public void init() {
    try {
      recordsList = this.loadCSVFileToPojo().get();
    } catch (InterruptedException I) {
      log.warn("Thread has been interrupted {}", I.getMessage());
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      log.error("Error constructing Region mapper: {} ", e.getMessage());
    }
  }

  private Future<List<RegionRecord>> loadCSVFileToPojo() {
    return executor.submit(() -> regionUtil.call());
  }


  public RegionRecord getRegionRecord(String postcode) {
    return recordsList.stream()
      .filter(regionRecord -> postcode.startsWith(regionRecord.getPartPostcode()))
      .findFirst()
      .orElse(null);
  }

  public Map<String, List<String>> getAllRegions() {
    Map<String, List<String>> regions = new HashMap<>();
    List<String> distinctRegionNames = recordsList.stream().map(RegionRecord::getRegion).distinct().sorted().collect(Collectors.toList());
    for (String strRegion : distinctRegionNames) {
      Predicate<RegionRecord> regionFilter = regionRecord -> regionRecord.getRegion().matches(strRegion);
      List<String> list = recordsList.stream()
        .filter(regionFilter)
        .map(RegionRecord::getSubRegion)
        .map(String::trim)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
      regions.put(strRegion, list);
    }
    return regions;
  }
}
