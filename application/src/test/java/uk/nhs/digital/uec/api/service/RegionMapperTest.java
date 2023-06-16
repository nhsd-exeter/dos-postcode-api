package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import uk.nhs.digital.uec.api.model.CCGRecord;
import uk.nhs.digital.uec.api.model.ICBRecord;
import uk.nhs.digital.uec.api.model.RegionRecord;
import uk.nhs.digital.uec.api.service.impl.RegionMapperImpl;
import uk.nhs.digital.uec.api.util.CCGUtil;

@ExtendWith(MockitoExtension.class)
public class RegionMapperTest {

  @InjectMocks
  RegionMapperImpl classUnderTest;

  @Mock
  private ResourceLoader resourceLoader;

  @Mock
  private ExecutorService executorService;

  @Mock
  private CCGUtil ccgUtil;

  static final String postcode = "EX88XE";
  List<RegionRecord> regionRecords;
  List<ICBRecord> icbRecordList;
  List<CCGRecord> ccgList;

  @BeforeEach
  public void setup() {
    regionRecords = new ArrayList<>();
    regionRecords.add(
      new RegionRecord(
        "EX8",
        "subregion8",
        new String[] { "EX8", "EX9" },
        "Yorkshire"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "FX88",
        "subregion88",
        new String[] { "FX88", "FX89" },
        "london"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "GX7",
        "subregion7",
        new String[] { "GX77", "GX78" },
        "east of england"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "HX6",
        "subregion9",
        new String[] { "HX66", "HX67" },
        "west midlands"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "IX56",
        "subregion10",
        new String[] { "IX56", "IX57" },
        "east midlands"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "JX78",
        "subregion11",
        new String[] { "JX78", "JX79" },
        "north west"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "KX86",
        "subregion12",
        new String[] { "KX86", "KX87" },
        "south west"
      )
    );
    regionRecords.add(
      new RegionRecord(
        "LX96",
        "subregion13",
        new String[] { "LX96", "LX97" },
        "south east"
      )
    );

    ccgList = new ArrayList<>();
    ccgList =
      List.of(
        getCCGRecordObject("EX8", "X2C4Y"),
        getCCGRecordObject("FX88", "01H"),
        getCCGRecordObject("GX7", "06L"),
        getCCGRecordObject("HX6", "M1J4Y"),
        getCCGRecordObject("IX56", "M1JY"),
        getCCGRecordObject("JX78", "MJ4Y"),
        getCCGRecordObject("K86", "M1JY"),
        getCCGRecordObject("LX96", "1J4Y"),
        getCCGRecordObject("XX99", "XXXXX")
        //IP14
      );
    icbRecordList = new ArrayList<>();
    icbRecordList =
      List.of(
        new ICBRecord(
          "X2C4Y",
          "X2C4Y Region",
          "X2C4Y ICB",
          "X2C4Y CCG",
          "X2C4Y@nhs.net"
        ),
        new ICBRecord("01H", "01H Region", "01H ICB", "01H CCG", "01H@nhs.net"),
        new ICBRecord("06L", "06L Region", "06L ICB", "06L CCG", "06L@nhs.net"),
        new ICBRecord(
          "M1J4Y",
          "M1J4Y Region",
          "M1J4Y ICB",
          "M1J4Y CCG",
          "M1J4Y@nhs.net"
        ),
        new ICBRecord(
          "M1JY",
          "M1JY Region",
          "M1JY ICB",
          "M1JY CCG",
          "M1JY@nhs.net"
        ),
        new ICBRecord(
          "MJ4Y",
          "MJ4Y Region",
          "MJ4Y ICB",
          "MJ4Y CCG",
          "MJ4Y@nhs.net"
        ),
        new ICBRecord(
          "M1JY",
          "M1JY Region",
          "M1JY ICB",
          "M1JY CCG",
          "M1JY@nhs.net"
        ),
        new ICBRecord(
          "1J4Y",
          "1J4Y Region",
          "1J4Y ICB",
          "1J4Y CCG",
          "1J4Y@nhs.net"
        ),
        new ICBRecord(
          "XXXXX",
          "XXXXX Region",
          "XXXXX ICB",
          "XXXXX CCG",
          "XXXXX@nhs.net"
        )
      );
  }

  @Test
  public void getRegionRecordByPostCodeTest()
    throws IOException, ExecutionException, InterruptedException {
    // Given
    Future<List<RegionRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(regionRecords);
    classUnderTest.init();

    // when
    RegionRecord regionRecord = classUnderTest.getRegionRecord(postcode);

    // Then
    assertNotNull(regionRecord);
  }

  @Test
  public void throwThreadExecutionExceptionWhilstReadingCSV()
    throws ExecutionException, InterruptedException {
    // Given
    Future<List<RegionRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenThrow(ExecutionException.class);

    // when
    classUnderTest.init();

    // Then
    assertThrows(
      NullPointerException.class,
      () -> classUnderTest.getRegionRecord(postcode)
    );
  }

  @Test
  public void throwThreadInterupredExceptionWhilstReadingCSV()
    throws ExecutionException, InterruptedException {
    // Given
    Future<List<RegionRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenThrow(InterruptedException.class);

    // when
    classUnderTest.init();

    // Then
    assertThrows(
      NullPointerException.class,
      () -> classUnderTest.getRegionRecord(postcode)
    );
  }

  @Test
  public void getAllRegionsTest()
    throws IOException, ExecutionException, InterruptedException {
    //Given
    Future<List<RegionRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(regionRecords);
    classUnderTest.init();

    //when
    Map<String, List<String>> regions = classUnderTest.getAllRegions();

    //Then
    assertNotNull(regions);
    assertEquals(8, regions.size());
  }

  @Test
  public void getICBRecordTest()
    throws IOException, ExecutionException, InterruptedException {
    // Given
    Future<List<ICBRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(icbRecordList);
    classUnderTest.init();

    // when
    ICBRecord icbRecord = classUnderTest.getICBRecord("06L");

    ICBRecord expected = ICBRecord
      .builder()
      .email("06L@nhs.net")
      .nhsIcb("06L ICB")
      .nhsCcg("06L CCG")
      .nhsRegion("06L Region")
      .orgCode("06L")
      .build();

    // Then
    assertNotNull(icbRecord);
    assertEquals(expected, icbRecord);
  }

  @Test
  public void getCCGRecordTest()
    throws IOException, ExecutionException, InterruptedException {
    // Given
    Future<List<CCGRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(ccgList);
    classUnderTest.init();

    // when
    CCGRecord ccgRecord = classUnderTest.getCCGRecord("EX8 1NY", "yorkshire");

    CCGRecord expected = getCCGRecordObject("EX8", "X2C4Y");

    // Then
    assertNotNull(ccgRecord);
    assertEquals(expected, ccgRecord);
  }

  @Test
  public void getCCGRecordNullTest()
    throws IOException, ExecutionException, InterruptedException {
    // Given
    Future<List<CCGRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(ccgList);
    classUnderTest.init();

    // when
    CCGRecord ccgRecord = classUnderTest.getCCGRecord("XX1 1XX", "Yorkshire");
    // Then
    assertNull(ccgRecord);
  }

  @Test
  public void getCCGRecordKnownPostCodeAndUnknownRegion()
    throws IOException, ExecutionException, InterruptedException {
    // Given
    Future<List<CCGRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(ccgList);
    classUnderTest.init();

    // when
    CCGRecord ccgRecord = classUnderTest.getCCGRecord("FX88 1XX", "XXXXXXXXXX");

    CCGRecord expected = getCCGRecordObject("FX88", "01H");

    // Then
    assertNotNull(ccgRecord);
    assertEquals(expected, ccgRecord);
  }

  private static CCGRecord getCCGRecordObject(String postcode, String orgCode) {
    return CCGRecord.builder().postcode(postcode).orgCode(orgCode).build();
  }
}
