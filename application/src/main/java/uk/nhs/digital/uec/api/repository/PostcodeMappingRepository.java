package uk.nhs.digital.uec.api.repository;

import java.util.List;
import java.util.Optional;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import uk.nhs.digital.uec.api.domain.PostcodeMapping;

/** This repository layer class retrieves the data from DynamoDB configured in this project */
@EnableScan
public interface PostcodeMappingRepository extends CrudRepository<PostcodeMapping, String> {

  @Override
  List<PostcodeMapping> findAll();

  Optional<PostcodeMapping> findById(String id);

  Optional<PostcodeMapping> findByPostCode(String postCode);

  List<Optional<PostcodeMapping>> findByName(String name);

  Optional<PostcodeMapping> findByPostCodeAndName(String postCode, String name);
}
