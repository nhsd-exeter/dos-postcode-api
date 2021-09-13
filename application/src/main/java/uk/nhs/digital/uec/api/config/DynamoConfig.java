package uk.nhs.digital.uec.api.config;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import org.apache.commons.lang3.StringUtils;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class defines the basic authentication and connection details to AWS DynamoDB
 * The values will be picked from the configuration file
 */

@Configuration
@EnableDynamoDBRepositories
    (basePackages = {"uk.nhs.digital.uec.api.repository"})
public class DynamoConfig {

    @Value("${dynamo.config.region}")
    private String awsRegion;

    @Value("${dynamo.endpoint}")
    private String amazonDynamoDBEndpoint;

    @Value("${dynamo.table.name}")
    private String dynamoDbTableName;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        AmazonDynamoDB amazonDynamoDB = null;
        if (StringUtils.isNotEmpty(amazonDynamoDBEndpoint)) {
            amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                new EndpointConfiguration(amazonDynamoDBEndpoint, awsRegion)).build();
        }
        return amazonDynamoDB;
    }

    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return new DynamoDBMapperConfig
                .Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(dynamoDbTableName))
                .build();
    }
}
