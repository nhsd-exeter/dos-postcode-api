package uk.nhs.digital.a2si.servicefinder.ccg.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.a2si.servicefinder.ccg.domain.Ccg;
import uk.nhs.digital.a2si.servicefinder.ccg.exception.InvalidPostcodeException;
import uk.nhs.digital.a2si.servicefinder.ccg.service.CcgService;

/**
    RestController for ccg search
 */
@RestController
@RequestMapping("/api/ccg")
@Slf4j
public class CcgController {

    @Autowired
    private CcgService ccgService;
    @Value("${invalid.postcode}")
    private String validPostCodeMessage;
    @Autowired
    private Environment environment;

    @GetMapping("/home")
    public ResponseEntity<String> getCcg() {
            return ResponseEntity.ok("Welcome to NHS clinical commissioning groups(CCG) search service");
    }

    @GetMapping()
    public ResponseEntity<List<Ccg>> getCcg(@RequestParam(name = "postcodes", required = false) List<String> postCodes,
            @RequestParam(name = "name", required = false) String name) throws InvalidPostcodeException {
        List<Ccg> ccgByPostCodes=null;
        /** Temparary change to block prod and demo environment for execution of ccg service */
        if(unBlockExecutionForProfile()){
            if(CollectionUtils.isNotEmpty(postCodes) && StringUtils.isNotBlank(name)){
                ccgByPostCodes = ccgService.getByPostCodesAndName(postCodes, name);
            }else if(CollectionUtils.isNotEmpty(postCodes) && StringUtils.isBlank(name)){
                ccgByPostCodes = ccgService.getByPostCodes(postCodes);
            }
            else if(StringUtils.isNotBlank(name) && CollectionUtils.isEmpty(postCodes)){
                ccgByPostCodes = ccgService.getByName(name);
            }
            else{
              throw new InvalidPostcodeException();
            }
        }
        return ResponseEntity.ok(ccgByPostCodes);
    }

    /** Temparary change to block prod and demo environment for execution of ccg service */
    private boolean unBlockExecutionForProfile(){
        if(environment == null) return true;
        if(Arrays.stream(environment.getActiveProfiles()).anyMatch(
            env -> (env.equalsIgnoreCase("dev-compose")
            || env.equalsIgnoreCase("nonprod")) ))
            {
                log.info("ccg info is unblocked");
                return true;
            }
            log.info("ccg info is blocked");
            return false;
    }

}
