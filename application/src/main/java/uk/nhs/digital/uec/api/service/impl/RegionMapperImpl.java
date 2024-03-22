package uk.nhs.digital.uec.api.service.impl;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uk.nhs.digital.uec.api.model.ICBRecord;
import uk.nhs.digital.uec.api.model.RegionRecord;
import uk.nhs.digital.uec.api.service.RegionMapper;
import uk.nhs.digital.uec.api.util.ICBUtil;
import uk.nhs.digital.uec.api.util.RegionUtil;

@Slf4j
@Component
public class RegionMapperImpl implements RegionMapper {

  private List<RegionRecord> recordsList;
  private List<ICBRecord> icbRecordList;

  @Autowired private ExecutorService executor;

  @Autowired private RegionUtil regionUtil;

  @Autowired private ICBUtil icbUtil;

  @PostConstruct
  public void init() {
    try {
      recordsList = this.loadRegionsToPojo().get();
      icbRecordList = this.loadICBToPoJO().get();
    } catch (InterruptedException I) {
      log.warn("Thread has been interrupted {}", I.getMessage());
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      log.error("Error constructing Region mapper: {} ", e.getMessage());
    }
    executor.shutdown();
  }

  @Async
  private Future<List<RegionRecord>> loadRegionsToPojo() {
    log.info("Async call for regions csv load");
    return executor.submit(() -> regionUtil.call());
  }

  @Async
  private Future<List<ICBRecord>> loadICBToPoJO() {
    log.info("Async call for ICV csv load");
    return executor.submit(() -> icbUtil.call());
  }

  public RegionRecord getRegionRecord(String postcode) {
    final List<RegionRecord> regionRecordList =
        recordsList.stream()
            .filter(regionRecord -> postcode.startsWith(regionRecord.getPartPostcode()))
            .collect(Collectors.toList());
    if (regionRecordList.isEmpty()) {
      log.info("No Region record found for a given postcode {}", postcode);
    }
    RegionRecord regionRecord = new RegionRecord();
    String partPostCode = "";
    if (regionRecordList.size() == 1) {
      regionRecord = regionRecordList.stream().findFirst().orElse(null);
    } else {
      switch (postcode.length()) {
        case 5:
          partPostCode = postcode.substring(0, 2);
          break;
        case 7:
          partPostCode = postcode.substring(0, 4);
          break;
        default:
          partPostCode = postcode.substring(0, 3);
          break;
      }
      log.info("partPostCode {} and this code will use in binarySearch", partPostCode);
      int index =
          binarySearchIndex(
              regionRecordList.stream().map(RegionRecord::getPartPostcode).toArray(), partPostCode);
      if (index != -1) {
        regionRecord = regionRecordList.get(index);
      }
    }
    if (regionRecord.getRegion().equals("North East")) {
      regionRecord.setRegion("Yorkshire and The Humber");
      log.info(
          "Updated region from North East to Yorkshire and The Humber: {}, {}",
          postcode,
          regionRecord);
    }

    log.info("RegionRecord details: {}", regionRecord);
    return regionRecord;
  }

  public Map<String, List<String>> getAllRegions() {
    Map<String, List<String>> regions = new HashMap<>();
    List<String> distinctRegionNames =
        recordsList.stream()
            .map(RegionRecord::getRegion)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    for (String strRegion : distinctRegionNames) {
      Predicate<RegionRecord> regionFilter =
          regionRecord -> regionRecord.getRegion().matches(strRegion);
      List<String> list =
          recordsList.stream()
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

  public ICBRecord getICBRecord(String orgCode) {
    ICBRecord icbRecord = new ICBRecord();
    int index =
        binarySearchIndex(icbRecordList.stream().map(ICBRecord::getOrgCode).toArray(), orgCode);
    if (index != -1) {
      icbRecord = icbRecordList.get(index);
    }
    log.info("ICBRecord details: {}", icbRecord);

    return icbRecord;
  }

  private int binarySearchIndex(Object[] records, String target) {
    int low = 0;
    int high = records.length - 1;
    while (low <= high) {
      int middle = low + ((high - low) / 2);
      String s = records[middle].toString();
      int result = target.compareTo(s);
      if (result == 0) {
        return middle;
      } else if (result > 0) {
        low = middle + 1;
      } else {
        high = middle - 1;
      }
    }
    return -1;
  }
}
