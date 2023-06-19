package uk.nhs.digital.uec.api.config;

import static com.amazonaws.regions.Regions.EU_WEST_2;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverterFactory;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * This class defines the basic authentication and connection details to AWS DynamoDB The values
 * will be picked from the configuration file
 */
@Configuration
@EnableDynamoDBRepositories(
    basePackages = {"uk.nhs.digital.uec.api.repository"},
    dynamoDBMapperConfigRef = "dynamoDBMapperConfig")
@Slf4j
public class DynamoConfig {

  private static final Regions DEFAULT_REGION = EU_WEST_2;
  @Autowired Environment environment;

  @Value("${dynamo.config.region}")
  private String awsRegion;

  @Value("${dynamo.endpoint}")
  private String amazonDynamoDBEndpoint;

  @Value("${dynamo.table.name}")
  private String dynamoDbTableName;

  @Bean
  public AmazonDynamoDB amazonDynamoDB() {
    log.info("Authenticating DynamoDB Instance");
    log.info("Amazon dynamodb endpoint:{}", amazonDynamoDBEndpoint);
    log.info("Amazon dynamodb awsRegion:{}", awsRegion);
    if (Arrays.stream(environment.getActiveProfiles())
        .anyMatch(env -> env.equalsIgnoreCase("local"))) {
      return AmazonDynamoDBClientBuilder.standard()
          .withEndpointConfiguration(
              new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, awsRegion))
          .build();
    }
    return AmazonDynamoDBClientBuilder.standard().withRegion(EU_WEST_2).build();
  }

  @Bean
  public DynamoDBMapperConfig dynamoDBMapperConfig(
      DynamoDBMapperConfig.TableNameOverride tableNameOverrider) {
    // Create empty DynamoDBMapperConfig builder
    DynamoDBMapperConfig.Builder builder = new DynamoDBMapperConfig.Builder();
    // Inject missing defaults from the deprecated method
    builder.withTypeConverterFactory(DynamoDBTypeConverterFactory.standard());
    builder.withTableNameResolver(DynamoDBMapperConfig.DefaultTableNameResolver.INSTANCE);
    // Inject the table name overrider bean
    builder.withTableNameOverride(tableNameOverrider());
    return builder.build();
  }

  @Bean
  public DynamoDBMapperConfig.TableNameOverride tableNameOverrider() {
    return DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(dynamoDbTableName);
  }
}
