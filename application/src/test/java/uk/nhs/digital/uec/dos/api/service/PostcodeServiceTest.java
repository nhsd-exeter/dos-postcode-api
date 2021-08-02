package uk.nhs.digital.uec.dos.api.dospostcodeapi.service;

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

import uk.nhs.digital.uec.dos.api.dospostcodeapi.domain.Postcode;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.repository.PostcodeRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class PostcodeServiceTest {

    @InjectMocks
    private PostcodeServiceImpl PostcodeService;

    @MockBean
    private PostcodeRepository PostcodeRepository;

    private List<Postcode> PostcodeList;
    private List<Optional<Postcode>> optionalPostcodeList;

    @BeforeEach
    public void init(){
        Postcode Postcode = new Postcode();
        Postcode.setName("Nhs Halton Postcode");
        Postcode.setPostCode("WA1 1QY");
        PostcodeList = new ArrayList<>();
        optionalPostcodeList = new ArrayList<>();
        PostcodeList.add(Postcode);
        optionalPostcodeList.add(Optional.of(Postcode));
    }

    @Test
    public void testGetByPostCodeInAndName() throws InvalidPostcodeException
    {
        List<String> postCodes = new ArrayList<>();
        postCodes.add("WA11QY");
        when(PostcodeRepository.findByPostCodeInAndName(postCodes, "Nhs Halton Postcode")).thenReturn(optionalPostcodeList);
        List<Postcode> findByPostCodeIn = PostcodeService.getByPostCodesAndName(postCodes, "Nhs Halton Postcode");
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetByPostCodes() throws InvalidPostcodeException {
        List<String> postCodes = new ArrayList<>();
        postCodes.add("WA11QY");
        when(PostcodeRepository.findByPostCodeIn(postCodes)).thenReturn(optionalPostcodeList);
        List<Postcode> findByPostCodeIn = PostcodeService.getByPostCodes(postCodes);
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetByPostcodeName() throws InvalidPostcodeException
    {
        when(PostcodeRepository.findByName("Nhs Halton Postcode")).thenReturn(optionalPostcodeList);
        List<Postcode> findByPostCodeIn = PostcodeService.getByName("Nhs Halton Postcode");
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetAll() throws InvalidPostcodeException
    {
        when(PostcodeRepository.findAll()).thenReturn(PostcodeList);
        List<Postcode> findByPostCodeIn = PostcodeService.getAll();
        assertTrue(findByPostCodeIn.size() > 0);
    }

}
