package uk.nhs.digital.uec.dos.api.dospostcodeapi.repository;

import java.util.List;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import uk.nhs.digital.uec.dos.api.dospostcodeapi.domain.Postcode;

/**
 * This repository layer class retrieves the data from DynamoDB configured in this project
 */
@EnableScan
public interface PostcodeRepository extends CrudRepository<Postcode, String> {

    @Override
    List<Postcode> findAll();
    Optional<Postcode> findById(String id);
    List<Optional<Postcode>> findByPostCode(String postCode);
    List<Optional<Postcode>> findByName(String name);
    List<Optional<Postcode>> findByPostCodeInAndName(List<String> postCodes, String name);
    List<Optional<Postcode>> findByPostCodeAndName(String postCode, String name);
    List<Optional<Postcode>> findByPostCodeIn(List<String> postCodes);

}
