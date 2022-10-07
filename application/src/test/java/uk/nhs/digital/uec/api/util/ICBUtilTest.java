package uk.nhs.digital.uec.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import uk.nhs.digital.uec.api.model.ICBRecord;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ICBUtilTest {

  static final String path = "classpath:DOS_Email_and_ICB.csv";
  static final String mockCSV =
    "Postcode,Region,Nearby districts,UK region\n"
      + "EX8,Exeter,\"EX9, EX7, EX3, EX5, TQ14, EX2, EX1, EX10, EX4, EX11\",South West";
  @InjectMocks
  ICBUtil classUnderTest;
  @Mock
  ResourceLoader resourceLoader;
  InputStream inputStream;
  Resource resource;

  @BeforeEach
  public void setup() {
    inputStream = new ByteArrayInputStream(mockCSV.getBytes(StandardCharsets.UTF_8));
    resource = mock(Resource.class);
  }

  @Test
  public void getCSVToModelListSuccess() throws IOException {
    // given
    when(resourceLoader.getResource(path)).thenReturn(resource);
    when(resource.getInputStream()).thenReturn(inputStream);

    // when
    List<ICBRecord> results = classUnderTest.call();

    // Then
    assertFalse(results.isEmpty());
  }

  @Test
  public void throwIOExceptionDuringCSVRead() throws IOException {
    // Given
    when(resourceLoader.getResource(path)).thenReturn(resource);
    when(resource.getInputStream()).thenThrow(IOException.class);

    // when
    List<ICBRecord> results = classUnderTest.call();

    // Then
    verify(resourceLoader).getResource(path);
    verify(resource).getInputStream();
    assertTrue(results.isEmpty());
  }
}
