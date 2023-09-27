package uk.nhs.digital.uec.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

import static com.amazonaws.regions.Regions.EU_WEST_2;

@Configuration
@Slf4j
public class LocalDynamoConfig {

  @Value("${dynamo.config.region}")
  private String awsRegion;

  @Value("${dynamo.endpoint}")
  private String amazonDynamoDBEndpoint;

  @Value("${dynamo.table.name}")
  private String dynamoDbTableName;

  @Bean
  public DynamoDbClient setupDynamoTables(Environment environment) {

    log.info("Authenticating DynamoDB Instance");
    log.info("Amazon dynamodb endpoint:{}", amazonDynamoDBEndpoint);
    log.info("Amazon dynamodb awsRegion:{}", awsRegion);

    return DynamoDbClient.builder()
        .endpointOverride(URI.create(amazonDynamoDBEndpoint))
        .region(Region.of(EU_WEST_2.name()))
        .build();
  }
}
