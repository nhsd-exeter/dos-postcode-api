package uk.nhs.digital.uec.api.service.impl;

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

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RegionMapperImpl implements RegionMapper {

  private static final String NORTH_EAST_AND_YORKSHIRE_PCODERY63 = "pcodey63.csv";
  private static final String NORTHWEST_PCODEY62 = "pcodey62.csv";
  private static final String EASTENGLAND_PCODEY61 = "pcodey61.csv";
  private static final String MIDLANDS_PCODEY60 = "pcodey60.csv";
  private static final String SOUTHEAST_PCODEY59 = "pcodey59.csv";
  private static final String SOUTHWEST_PCODEY58 = "pcodey58.csv";
  private static final String LONDON_PCODEY56 = "pcodey56.csv";

  private List<RegionRecord> recordsList;
  private List<ICBRecord> icbRecordList;
  private List<CCGRecord> ne_and_yorkshire;
  private List<CCGRecord> northWest;
  private List<CCGRecord> eastEngland;
  private List<CCGRecord> midlands;
  private List<CCGRecord> southEast;
  private List<CCGRecord> southWest;
  private List<CCGRecord> london;

  @Autowired
  private ExecutorService executor;

  @Autowired
  private RegionUtil regionUtil;

  @Autowired
  private ICBUtil icbUtil;

  @Autowired
  private CCGUtil ccgUtil;

  @PostConstruct
  public void init() {
    try {
      recordsList = this.loadCSVFileToPojo().get();
      icbRecordList = this.loadICBToPoJO().get();
      ne_and_yorkshire = this.getCCGRecord(NORTH_EAST_AND_YORKSHIRE_PCODERY63).get();
      northWest = this.getCCGRecord(NORTHWEST_PCODEY62).get();
      eastEngland = this.getCCGRecord(EASTENGLAND_PCODEY61).get();
      midlands = this.getCCGRecord(MIDLANDS_PCODEY60).get();
      southEast = this.getCCGRecord(SOUTHEAST_PCODEY59).get();
      southWest = this.getCCGRecord(SOUTHWEST_PCODEY58).get();
      london = this.getCCGRecord(LONDON_PCODEY56).get();
    } catch (InterruptedException I) {
      log.warn("Thread has been interrupted {}", I.getMessage());
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      log.error("Error constructing Region mapper: {} ", e.getMessage());
    }
    executor.shutdown();
  }

  @Async
  private Future<List<RegionRecord>> loadCSVFileToPojo() {
    log.info("Async call for regions csv load");
    return executor.submit(() -> regionUtil.call());
  }

  @Async
  private Future<List<ICBRecord>> loadICBToPoJO() {
    log.info("Async call for ICV csv load");
    return executor.submit(() -> icbUtil.call());
  }

  @Async
  private Future<List<CCGRecord>> getCCGRecord(String collection) {
    log.info("Async call for CCG csv load");
    ccgUtil.setFileName(collection);
    return executor.submit(() -> ccgUtil.call());
  }

  private List<CCGRecord> getAllCCGs() {
    List<CCGRecord> allRec = new ArrayList<>();
    allRec.addAll(ne_and_yorkshire);
    allRec.addAll(london);
    allRec.addAll(eastEngland);
    allRec.addAll(southEast);
    allRec.addAll(southWest);
    allRec.addAll(northWest);
    allRec.addAll(midlands);
    return allRec.stream().sorted().collect(Collectors.toList());
  }

  public RegionRecord getRegionRecord(String postcode) {
    List<RegionRecord> regionRecordList = recordsList
      .stream()
      .filter(regionRecord ->
        postcode.startsWith(regionRecord.getPartPostcode())
      )
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
      log.info(
        "partPostCode {} and this code will use in binarySearch",
        partPostCode
      );
      int index = binarySearchIndex(
        regionRecordList.stream().map(RegionRecord::getPartPostcode).toArray(),
        partPostCode
      );
      regionRecord = regionRecordList.get(index);
    }
    if (regionRecord.getRegion().equals("North East")){
      regionRecord.setRegion("Yorkshire and The Humber");
      log.info("Updated region from North East to Yorkshire and The Humber: {}, {}",postcode, regionRecord);
    }
    return regionRecord;
  }

  public Map<String, List<String>> getAllRegions() {
    Map<String, List<String>> regions = new HashMap<>();
    List<String> distinctRegionNames = recordsList
      .stream()
      .map(RegionRecord::getRegion)
      .distinct()
      .sorted()
      .collect(Collectors.toList());
    for (String strRegion : distinctRegionNames) {
      Predicate<RegionRecord> regionFilter = regionRecord ->
        regionRecord.getRegion().matches(strRegion);
      List<String> list = recordsList
        .stream()
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
    int index = binarySearchIndex(
      icbRecordList.stream().map(ICBRecord::getOrgCode).toArray(),
      orgCode
    );
    icbRecord = icbRecordList.get(index);
    return icbRecord;
  }

  @Override
  public List<CCGRecord> getCCGRecord(String postcode, String district) {
    log.info("Searching {} in district {}", postcode, district);

    String code = getCodeFromPostcode(postcode);

    try {
      List<CCGRecord> ccgRecords = getCCGRecordsByDistrict(district);

      if (!ccgRecords.isEmpty()) {
        List<Integer> indexes = binarySearchIndexes(
          ccgRecords.stream().map(CCGRecord::getPostcode).toArray(),
          code
        );

        return indexes.stream()
          .map(ccgRecords::get)
          .collect(Collectors.toList());
      }
    } catch (IndexOutOfBoundsException e) {
      log.error("An error with the binary search in getCCGRecord {}", e.getMessage());
    } catch (Exception e) {
      log.error("An error with the binary search {}", e.getMessage());
    }

    log.info("CCG Record not found");
    return Collections.emptyList();
  }


  private String getCodeFromPostcode(String postcode) {
    int endIndex;
    if (postcode.length() == 5) {
      endIndex = 2;
    } else if (postcode.length() == 6) {
      endIndex = 3;
    } else {
      endIndex = 4;
    }
    return postcode.substring(0, endIndex).trim();
  }

  private List<CCGRecord> getCCGRecordsByDistrict(String district) {
    switch (district.toLowerCase()) {
      case "yorkshire":
        return ne_and_yorkshire;
      case "london":
        return london;
      case "east of england":
        return eastEngland;
      case "west midlands":
      case "east midlands":
        return midlands;
      case "north west":
        return northWest;
      case "south west":
        return southWest;
      case "south east":
        return southEast;
      default:
        return getAllCCGs();
    }
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
   * This method is written extend the possibility  of "binarySearchIndex" method - use it in scenarios as explained in
   * jira ticket SFD-5564
   * All it does is, performing a binary search on an array of postcodes to find all occurrences of a target postcode.
   * @param records The array of postcodes to search within.
   * @param target  The postcode to search for.
   * @return A list of indexes where the target postcode is found. If no occurrences are found, the list is empty.
   */
  private static List<Integer> binarySearchIndexes(Object[] records, String target) {
    List<Integer> indexes = new ArrayList<>();
    int low = 0;
    int high = records.length - 1;

    while (low <= high) {
      int middle = low + ((high - low) / 2);
      String s = records[middle].toString();
      int result = target.compareTo(s);

      if (result == 0) {
        indexes.add(middle);

        int leftIndex = middle - 1;
        while (leftIndex >= 0 && records[leftIndex].toString().equals(target)) {
          indexes.add(leftIndex);
          leftIndex--;
        }

        int rightIndex = middle + 1;
        while (rightIndex < records.length && records[rightIndex].toString().equals(target)) {
          indexes.add(rightIndex);
          rightIndex++;
        }

        return indexes;
      } else if (result > 0) {
        low = middle + 1;
      } else {
        high = middle - 1;
      }
    }

    return indexes;
  }
}
