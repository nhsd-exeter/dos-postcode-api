package uk.nhs.digital.uec.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @InjectMocks
    private PostcodeMappingServiceImpl postcodeMappingService;

    @MockBean
    private PostcodeMappingRepository postcodeMappingRepository;

    private List<PostcodeMapping> postcodeMappingList;
    private List<Optional<PostcodeMapping>> postcodeMappingOptList;

    @BeforeEach
    public void init(){
        PostcodeMapping postcodeMapping = new PostcodeMapping();
        postcodeMapping.setName("Nhs Halton CCG");
        postcodeMapping.setPostCode("WA1 1QY");
        postcodeMapping.setEasting(12345);
        postcodeMapping.setNorthing(360);
        postcodeMappingList = new ArrayList<>();
        postcodeMappingOptList = new ArrayList<>();
        postcodeMappingList.add(postcodeMapping);
        postcodeMappingOptList.add(Optional.of(postcodeMapping));
    }

    @Test
    public void testGetByPostCodeInAndName() throws InvalidPostcodeException
    {
        List<String> postCodes = new ArrayList<>();
        postCodes.add("WA11QY");
        when(postcodeMappingRepository.findByPostCodeInAndName(postCodes, "Nhs Halton CCG")).thenReturn(postcodeMappingOptList);
        List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByPostCodesAndName(postCodes, "Nhs Halton CCG");
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetByPostCodes() throws InvalidPostcodeException {
        List<String> postCodes = new ArrayList<>();
        postCodes.add("WA11QY");
        when(postcodeMappingRepository.findByPostCodeIn(postCodes)).thenReturn(postcodeMappingOptList);
        List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByPostCodes(postCodes);
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetByCcgName() throws InvalidPostcodeException
    {
        when(postcodeMappingRepository.findByName("Nhs Halton CCG")).thenReturn(postcodeMappingOptList);
        List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByName("Nhs Halton CCG");
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetAll() throws InvalidPostcodeException
    {
        when(postcodeMappingRepository.findAll()).thenReturn(postcodeMappingList);
        List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getAll();
        assertTrue(findByPostCodeIn.size() > 0);
    }


    @Test
    public void testEastingAndNorthing() throws InvalidPostcodeException
    {
        when(postcodeMappingRepository.findByName("Nhs Halton CCG")).thenReturn(postcodeMappingOptList);
        List<PostcodeMapping> findByPostCodeIn = postcodeMappingService.getByName("Nhs Halton CCG");
        PostcodeMapping postCodeMapping = findByPostCodeIn.stream().findAny().get();
        int easting = postCodeMapping.getEasting();
        int northing = postCodeMapping.getNorthing();
        assertEquals(12345, easting);
        assertEquals(360, northing);
    }
}
