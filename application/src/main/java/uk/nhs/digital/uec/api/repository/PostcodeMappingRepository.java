package uk.nhs.digital.uec.api.repository;

import java.util.List;
import java.util.Optional;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.model.PostcodeMapping;

/** This repository layer class retrieves the data from DynamoDB configured in this project */
@EnableScan
@Repository
public interface PostcodeMappingRepository extends CrudRepository<PostcodeMapping, String> {

  @Override
  List<PostcodeMapping> findAll();

  Optional<PostcodeMapping> findById(String id);

  Optional<PostcodeMapping> findByPostCode(String postCode);

  List<Optional<PostcodeMapping>> findByName(String name);

  Optional<PostcodeMapping> findByPostCodeAndName(String postCode, String name);
}
