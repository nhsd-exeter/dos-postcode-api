package uk.nhs.digital.a2si.servicefinder.ccg.service;

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

import uk.nhs.digital.a2si.servicefinder.ccg.domain.Ccg;
import uk.nhs.digital.a2si.servicefinder.ccg.exception.InvalidPostcodeException;
import uk.nhs.digital.a2si.servicefinder.ccg.repository.CcgRepository;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CcgServiceTest {

    @InjectMocks
    private CcgServiceImpl ccgService;

    @MockBean
    private CcgRepository ccgRepository;

    private List<Ccg> ccgList;
    private List<Optional<Ccg>> optionalCcgList;

    @BeforeEach
    public void init(){
        Ccg ccg = new Ccg();
        ccg.setName("Nhs Halton CCG");
        ccg.setPostCode("WA1 1QY");
        ccgList = new ArrayList<>();
        optionalCcgList = new ArrayList<>();
        ccgList.add(ccg);
        optionalCcgList.add(Optional.of(ccg));
    }

    @Test
    public void testGetByPostCodeInAndName() throws InvalidPostcodeException
    {
        List<String> postCodes = new ArrayList<>();
        postCodes.add("WA11QY");
        when(ccgRepository.findByPostCodeInAndName(postCodes, "Nhs Halton CCG")).thenReturn(optionalCcgList);
        List<Ccg> findByPostCodeIn = ccgService.getByPostCodesAndName(postCodes, "Nhs Halton CCG");
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetByPostCodes() throws InvalidPostcodeException {
        List<String> postCodes = new ArrayList<>();
        postCodes.add("WA11QY");
        when(ccgRepository.findByPostCodeIn(postCodes)).thenReturn(optionalCcgList);
        List<Ccg> findByPostCodeIn = ccgService.getByPostCodes(postCodes);
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetByCcgName() throws InvalidPostcodeException
    {
        when(ccgRepository.findByName("Nhs Halton CCG")).thenReturn(optionalCcgList);
        List<Ccg> findByPostCodeIn = ccgService.getByName("Nhs Halton CCG");
        assertTrue(findByPostCodeIn.size() > 0);
    }

    @Test
    public void testGetAll() throws InvalidPostcodeException
    {
        when(ccgRepository.findAll()).thenReturn(ccgList);
        List<Ccg> findByPostCodeIn = ccgService.getAll();
        assertTrue(findByPostCodeIn.size() > 0);
    }

}
