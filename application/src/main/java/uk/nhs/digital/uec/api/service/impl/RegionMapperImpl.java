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
import uk.nhs.digital.uec.api.model.CCGRecord;
import uk.nhs.digital.uec.api.model.ICBRecord;
import uk.nhs.digital.uec.api.model.RegionRecord;
import uk.nhs.digital.uec.api.service.RegionMapper;
import uk.nhs.digital.uec.api.util.CCGUtil;
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

  @Autowired private CCGUtil ccgUtil;

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
    List<RegionRecord> regionRecordList =
        recordsList.stream()
            .filter(regionRecord -> postcode.startsWith(regionRecord.getPartPostcode()))
            .collect(Collectors.toList());
    RegionRecord regionRecord = null;
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
      regionRecord = regionRecordList.get(index);
    }
    if (regionRecord.getRegion().equals("North East")) {
      regionRecord.setRegion("Yorkshire and The Humber");
      log.info(
          "Updated region from North East to Yorkshire and The Humber: {}, {}",
          postcode,
          regionRecord);
    }
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
    ICBRecord icbRecord = null;
    int index =
        binarySearchIndex(icbRecordList.stream().map(ICBRecord::getOrgCode).toArray(), orgCode);
    icbRecord = icbRecordList.get(index);
    return icbRecord;
  }

  @Override
  public Optional<CCGRecord> getCCGRecord(String postcode, String region) {
    log.info("Searching {} ", postcode);

    ccgUtil.setFileName(postcode.substring(0, 1) + ".csv");
    List<CCGRecord> ccgRecords = ccgUtil.call();
    log.info("ccgRecords size {}", ccgRecords.size());
    Integer index =
        binarySearchIgnoringSpaces(
            ccgRecords.stream().map(e -> e.getPostcode()).toArray(), postcode);
    if (index == -1) {
      return Optional.empty();
    }
    return Optional.ofNullable(ccgRecords.get(index));
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

  /**
   * This method is written extend the possibility of "binarySearchIndex" method - use it in
   * scenarios as explained in jira ticket SFD-5564 All it does is, performing a binary search on an
   * array of postcodes to find all occurrences of a target postcode.
   *
   * @param records The array of postcodes to search within.
   * @param target The postcode to search for.
   * @return The index of the first occurrence of the target postcode in the array, or -1 if the
   *     target postcode is not found.
   */
  private static int binarySearchIgnoringSpaces(Object[] records, String target) {
    if (records == null || target == null) {
      return -1; // Invalid input
    }

    int left = 0;
    int right = records.length - 1;

    while (left <= right) {
      int mid = left + (right - left) / 2;

      // Compare ignoring white spaces
      String midValue = records[mid].toString().replaceAll("\\s", "");
      target = target.replaceAll("\\s", "");

      int result = midValue.compareTo(target);

      if (result == 0) {
        return mid; // Found the target
      } else if (result < 0) {
        left = mid + 1; // Target is in the right half
      } else {
        right = mid - 1; // Target is in the left half
      }
    }
    return -1; // Target not found
  }
}
