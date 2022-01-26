package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.repository.PostcodeMappingRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class PostcodeMappingServiceTest {

  @InjectMocks private PostcodeMappingServiceImpl postcodeMappingService;

  @MockBean private PostcodeMappingRepository postcodeMappingRepository;

  private List<PostcodeMapping> postcodeMappingList;
  private List<Optional<PostcodeMapping>> postcodeMappingOptList;
  private static String serviceName = "Nhs Halton CCG";
  private List<String> postCodes = null;

  @BeforeEach
  public void init() {
    PostcodeMapping postcodeMapping = new PostcodeMapping();
    postcodeMapping.setName(serviceName);
    postcodeMapping.setPostCode("WA1 1QY");
    postcodeMapping.setEasting(12345);
    postcodeMapping.setNorthing(360);
    postcodeMappingList = new ArrayList<>();
    postcodeMappingOptList = new ArrayList<>();
    postcodeMappingList.add(postcodeMapping);
    postcodeMappingOptList.add(Optional.of(postcodeMapping));
    postCodes = new ArrayList<>();
    postCodes.add("WA11QY");
  }

  @Test
  public void testGetByPostCodeInAndName() throws InvalidPostcodeException {
    when(postcodeMappingRepository.findByPostCodeAndName("WA11QY", serviceName))
        .thenReturn(postcodeMappingOptList);
    List<PostcodeMapping> findByPostCodeIn =
        postcodeMappingService.getByPostCodesAndName(postCodes, serviceName);
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testGetByPostCodes() throws InvalidPostcodeException {
    when(postcodeMappingRepository.findByPostCode("WA11QY")).thenReturn(postcodeMappingOptList);
    List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByPostCodes(postCodes);
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testGetByCcgName() throws InvalidPostcodeException {
    when(postcodeMappingRepository.findByName(serviceName)).thenReturn(postcodeMappingOptList);
    List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByName(serviceName);
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testGetAll() throws InvalidPostcodeException {
    when(postcodeMappingRepository.findAll()).thenReturn(postcodeMappingList);
    List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getAll();
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testEastingAndNorthing() throws InvalidPostcodeException {
    when(postcodeMappingRepository.findByName(serviceName)).thenReturn(postcodeMappingOptList);
    List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByName(serviceName);
    PostcodeMapping postCodeMapping = findByPostCodeIn.get(0);
    int easting = postCodeMapping.getEasting();
    int northing = postCodeMapping.getNorthing();
    assertEquals(12345, easting);
    assertEquals(360, northing);
  }
}
