package uk.nhs.digital.a2si.servicefinder.ccg.repository;

import java.util.List;
import java.util.Optional;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import uk.nhs.digital.a2si.servicefinder.ccg.domain.Ccg;

/**
 * This repository layer class retrieves the data from DynamoDB configured in this project
 */
@EnableScan
public interface CcgRepository extends CrudRepository<Ccg, String> {

    @Override
    List<Ccg> findAll();
    Optional<Ccg> findById(String id);
    List<Optional<Ccg>> findByPostCode(String postCode);
    List<Optional<Ccg>> findByName(String name);
    List<Optional<Ccg>> findByPostCodeInAndName(List<String> postCodes, String name);
    List<Optional<Ccg>> findByPostCodeAndName(String postCode, String name);
    List<Optional<Ccg>> findByPostCodeIn(List<String> postCodes);

}
