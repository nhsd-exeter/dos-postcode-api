package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    regionRecords.add(new RegionRecord("EX8", "subregion", new String[] {"EX8"}, "region"));
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
}
