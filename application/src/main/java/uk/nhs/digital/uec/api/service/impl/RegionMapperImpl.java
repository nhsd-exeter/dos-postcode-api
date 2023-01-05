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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j(topic = "Postcode API - Region_Mapper_Service")
@Component
public class RegionMapperImpl implements RegionMapper {

  private static final String YORKSHIRE_PCODERY63 = "pcodey63.csv";
  private static final String NORTHWEST_PCODEY62 = "pcodey62.csv";
  private static final String EASTENGLAND_PCODEY61 = "pcodey61.csv";
  private static final String MIDLANDS_PCODEY60 = "pcodey60.csv";
  private static final String SOUTHEAST_PCODEY59 = "pcodey59.csv";
  private static final String SOUTHWEST_PCODEY58 = "pcodey58.csv";
  private static final String LONDON_PCODEY56 = "pcodey56.csv";

  private List<RegionRecord> recordsList;
  private List<ICBRecord> icbRecordList;
  private List<CCGRecord> yorkshire;
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
      yorkshire = this.getCCGRecord(YORKSHIRE_PCODERY63).get();
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
    allRec.addAll(yorkshire);
    allRec.addAll(london);
    allRec.addAll(eastEngland);
    allRec.addAll(southEast);
    allRec.addAll(southWest);
    allRec.addAll(northWest);
    allRec.addAll(midlands);
    return allRec.stream().sorted().collect(Collectors.toList());
  }

  public RegionRecord getRegionRecord(String postcode) {
    List<RegionRecord> regionRecordList =
      recordsList.stream()
        .filter(regionRecord -> postcode.startsWith(regionRecord.getPartPostcode()))
        .collect(Collectors.toList());
    RegionRecord regionRecord = null;
    if (regionRecordList.size() == 1) {
      regionRecord = regionRecordList.stream().findFirst().orElse(null);
    } else {
      int postCodeLength = postcode.replaceAll(" ", "").length() == 6 ? 3 : 4;
      final String partPostCode = postcode.substring(0, postCodeLength);
      int index =
        binarySearchIndex(
          regionRecordList.stream().map(RegionRecord::getPartPostcode).toArray(), partPostCode);
      regionRecord = regionRecordList.get(index);
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

  public CCGRecord getCCGRecord(String postcode, String district) {
    CCGRecord ccgRecord = null;
    log.info("Searching {} in district {}", postcode, district);
    String code;
    if (postcode.length() == 5) {
      code = postcode.substring(0, 2).trim();
    } else if (postcode.length() == 6) {
      code = postcode.substring(0, 3).trim();
    } else {
      code = postcode.substring(0, 4).trim();
    }
    try {
      if (district.contains("Yorkshire")) {
        ccgRecord =
          yorkshire.get(
            binarySearchIndex(yorkshire.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else if (district.equalsIgnoreCase("london")) {
        ccgRecord =
          london.get(
            binarySearchIndex(london.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else if (district.equalsIgnoreCase("east of england")) {
        ccgRecord =
          eastEngland.get(
            binarySearchIndex(
              eastEngland.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else if (district.equalsIgnoreCase("west midlands")
        || district.equalsIgnoreCase("east midlands")) {
        ccgRecord =
          midlands.get(
            binarySearchIndex(midlands.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else if (district.equalsIgnoreCase("north west")) {
        ccgRecord =
          northWest.get(
            binarySearchIndex(northWest.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else if (district.equalsIgnoreCase("south west")) {
        ccgRecord =
          southWest.get(
            binarySearchIndex(southWest.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else if (district.equalsIgnoreCase("south east")) {
        ccgRecord =
          southEast.get(
            binarySearchIndex(southEast.stream().map(CCGRecord::getPostcode).toArray(), code));
      } else {
        ccgRecord =
          getAllCCGs()
            .get(
              binarySearchIndex(
                getAllCCGs().stream().map(CCGRecord::getPostcode).toArray(), code));
      }
    }
    catch (IndexOutOfBoundsException e) {
      log.error("An error with the binary search in getCCGRecord {}", e.getMessage());
      if (ccgRecord == null){
        int index = binarySearchIndex(getAllCCGs().stream().map(CCGRecord::getPostcode).toArray(), code);
        ccgRecord = index < 0 ? null : getAllCCGs().get(index);
      }
    }
    catch (Exception e) {
      log.error("An error with the binary search {}", e.getMessage());
    }

    return ccgRecord;
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
