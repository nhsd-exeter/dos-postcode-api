package uk.nhs.digital.uec.dos.api.dospostcodeapi.controller;

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
import uk.nhs.digital.uec.dos.api.dospostcodeapi.domain.Postcode;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.exception.InvalidPostcodeException;
import uk.nhs.digital.uec.dos.api.dospostcodeapi.service.PostcodeService;

/**
    RestController for postcode search
 */
@RestController
@RequestMapping("/api/postcode")
@Slf4j
public class PostcodeController {

    @Autowired
    private PostcodeService PostcodeService;
    @Value("${invalid.postcode}")
    private String validPostCodeMessage;
    @Autowired
    private Environment environment;

    @GetMapping("/home")
    public ResponseEntity<String> getPostcode() {
            return ResponseEntity.ok("Welcome to the DoS Postcode service");
    }

    @GetMapping()
    public ResponseEntity<List<Postcode>> getPostcode(@RequestParam(name = "postcodes", required = false) List<String> postCodes,
            @RequestParam(name = "name", required = false) String name) throws InvalidPostcodeException {
        List<Postcode> postcodes=null;
        /** Temparary change to block prod and demo environment for execution of Postcode service */
        if(unBlockExecutionForProfile()){
            if(CollectionUtils.isNotEmpty(postCodes) && StringUtils.isNotBlank(name)){
                postcodes = PostcodeService.getByPostCodesAndName(postCodes, name);
            }else if(CollectionUtils.isNotEmpty(postCodes) && StringUtils.isBlank(name)){
                postcodes = PostcodeService.getByPostCodes(postCodes);
            }
            else if(StringUtils.isNotBlank(name) && CollectionUtils.isEmpty(postCodes)){
                postcodes = PostcodeService.getByName(name);
            }
            else{
              throw new InvalidPostcodeException();
            }
        }
        return ResponseEntity.ok(postcodes);
    }

    /** Temparary change to block prod and demo environment for execution of Postcode service */
    private boolean unBlockExecutionForProfile(){
        if(environment == null) return true;
        if(Arrays.stream(environment.getActiveProfiles()).anyMatch(
            env -> (env.equalsIgnoreCase("dev-compose")
            || env.equalsIgnoreCase("nonprod")) ))
            {
                log.info("Postcode info is unblocked");
                return true;
            }
            log.info("Postcode info is blocked");
            return false;
    }

}
