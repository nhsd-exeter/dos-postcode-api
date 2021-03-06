package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.domain.RegionRecord;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class RegionMapperTest {

  @InjectMocks RegionMapper classUnderTest;
  @Mock private ResourceLoader resourceLoader;
  @Mock private ExecutorService executorService;
  static final String postcode = "EX8 8XE";
  List<RegionRecord> regionRecords;

  @BeforeEach
  public void setup() {
    regionRecords = new ArrayList<>();
    regionRecords.add(new RegionRecord("EX8", "subregion", new String[] {"EX88","EX89"}, "region8"));
    regionRecords.add(new RegionRecord("EX7", "subregion7", new String[] {"EX77","EX78"}, "region7"));
    regionRecords.add(new RegionRecord("EX6", "subregion7", new String[] {"EX66","EX67"}, "region6"));
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
    assertThrows(NullPointerException.class, () -> classUnderTest.getRegionRecord(postcode));
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
    assertThrows(NullPointerException.class, () -> classUnderTest.getRegionRecord(postcode));
  }


  @Test
  public void getAllRegionsTest() throws IOException, ExecutionException, InterruptedException {
    //Given
    Future<List<RegionRecord>> fut = mock(Future.class);
    when(executorService.submit(any(Callable.class))).thenReturn(fut);
    when(fut.get()).thenReturn(regionRecords);
    classUnderTest.init();

    //when
    Map<String,List<String>> regions = classUnderTest.getAllRegions();

    //Then
    assertNotNull(regions);
    assertEquals(3,regions.size());

  }


}
