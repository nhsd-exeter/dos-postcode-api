package uk.nhs.digital.uec.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.model.RegionRecord;
import uk.nhs.digital.uec.api.exception.ErrorMessageEnum;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.repository.PostcodeMappingRepository;
import uk.nhs.digital.uec.api.service.impl.PostcodeMappingServiceImpl;
import uk.nhs.digital.uec.api.service.impl.RegionMapperImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class PostcodeMappingServiceTest {

  private static String serviceName = "Nhs Halton CCG";
  private static String POST_CODE = "WA11QY";
  @InjectMocks
  private PostcodeMappingServiceImpl postcodeMappingService;
  @MockBean
  private PostcodeMappingRepository postcodeMappingRepository;
  @MockBean
  private ValidationService validationService;
  @MockBean
  private RegionMapperImpl regionMapper;
  private List<PostcodeMapping> postcodeMappingList;
  private List<Optional<PostcodeMapping>> postcodeMappingOptList;
  private List<String> postCodes = null;
  private RegionRecord regionRecord = new RegionRecord("WA1", "subRegion", new String[]{"WA1"}, "region");

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
  public void testGetByPostCodeInAndName() throws InvalidPostcodeException, NotFoundException {
    when(postcodeMappingRepository.findByPostCodeAndName(postCodes.get(0), serviceName))
      .thenReturn(postcodeMappingOptList.get(0));
    when(validationService.validatePostCodes(postCodes)).thenReturn(postCodes);
    when(validationService.validateAndReturn(anyList())).thenReturn(postcodeMappingList);
    when(regionMapper.getRegionRecord("WA1 1QY")).thenReturn(regionRecord);
    List<PostcodeMapping> findByPostCodeIn =
      postcodeMappingService.getByPostCodesAndName(postCodes, serviceName);
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testGetByPostCodes() throws InvalidPostcodeException, NotFoundException {
    when(postcodeMappingRepository.findByPostCode(postCodes.get(0)))
      .thenReturn(postcodeMappingOptList.get(0));
    when(validationService.validatePostCodes(postCodes)).thenReturn(postCodes);
    when(validationService.validateAndReturn(anyList())).thenReturn(postcodeMappingList);
    List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByPostCodes(postCodes);
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testNotFoundExceptionTest()
    throws InvalidPostcodeException, InvalidParameterException, NotFoundException {
    when(validationService.validateAndReturn(anyList()))
      .thenThrow(new NotFoundException(ErrorMessageEnum.NO_LOCATION_FOUND.getMessage()));
    NotFoundException notFoundException =
      assertThrows(
        NotFoundException.class,
        () -> postcodeMappingService.getByPostCodesAndName(Collections.emptyList(), null));
    assertEquals(ErrorMessageEnum.NO_LOCATION_FOUND.getMessage(), notFoundException.getMessage());
  }

  @Test
  public void testInvalidPostcodeExceptionTest()
    throws InvalidPostcodeException, InvalidParameterException, NotFoundException {
    when(validationService.validatePostCodes(anyList()))
      .thenThrow(new InvalidPostcodeException(ErrorMessageEnum.INVALID_POSTCODE.getMessage()));
    InvalidPostcodeException invalidPostcodeException =
      assertThrows(
        InvalidPostcodeException.class,
        () -> postcodeMappingService.getByPostCodesAndName(Collections.emptyList(), null));
    assertEquals(
      ErrorMessageEnum.INVALID_POSTCODE.getMessage(), invalidPostcodeException.getMessage());
  }

  @Test
  public void testGetByCcgName()
    throws InvalidPostcodeException, InvalidParameterException, NotFoundException {
    when(postcodeMappingRepository.findByName(serviceName)).thenReturn(postcodeMappingOptList);
    when(validationService.validatePostCodes(postCodes)).thenReturn(postCodes);
    when(validationService.validateAndReturn(anyList())).thenReturn(postcodeMappingList);
    List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByName(serviceName);
    assertFalse(findByPostCodeIn.isEmpty());
  }

  @Test
  public void testEastingAndNorthing()
    throws InvalidPostcodeException, InvalidParameterException, NotFoundException {
    when(postcodeMappingRepository.findByName(serviceName)).thenReturn(postcodeMappingOptList);
    when(validationService.validatePostCodes(postCodes)).thenReturn(postCodes);
    when(validationService.validateAndReturn(anyList())).thenReturn(postcodeMappingList);
    List<PostcodeMapping> postcodeMappings = postcodeMappingService.getByName(serviceName);
    PostcodeMapping postCodeMapping = postcodeMappings.get(0);
    int easting = postCodeMapping.getEasting();
    int northing = postCodeMapping.getNorthing();
    assertEquals(12345, easting);
    assertEquals(360, northing);
  }
}
