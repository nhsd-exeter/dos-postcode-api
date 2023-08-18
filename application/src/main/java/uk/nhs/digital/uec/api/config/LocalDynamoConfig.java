package uk.nhs.digital.uec.api.config;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.amazonaws.regions.Regions.EU_WEST_2;

@Configuration
@Slf4j
public class LocalDynamoConfig {

  @Autowired
  Environment environment;
  @Value("${dynamo.config.region}")
  private String awsRegion;
  @Value("${dynamo.endpoint}")
  private String amazonDynamoDBEndpoint;
  @Value("${dynamo.table.name}")
  private String dynamoDbTableName;

  private final String key = "postcode";
  private final ObjectMapper mapper = new ObjectMapper();

  String entry1 = "{\"postcode\":\"PO305XW\",\"easting\":\"74\", \"northing\":\"150\", \"name\":\"NHS Isle of Wight CCG\"}";
  String entry2 = "{\"postcode\":\"WA11QY\",\"easting\":\"558439\", \"northing\":\"140222\",\"name\":\"NHS Halton CCG\"}";
  String entry3 = "{\"postcode\":\"AL86JL\",\"easting\":\"265\",\"northing\":\"166\",\"name\":\"NHS East and North Hertfordshire CCG\"}";
  String entry4 = "{\"postcode\":\"EX11SR\",\"easting\":\"292777\", \"northing\":\"92633\",\"name\":\"NHS Devon Clinical Commissioning Group (CCG)\"}";
  String entry5 = "{\"postcode\":\"LS166EB\",\"easting\":\"51\", \"northing\":\"130\",\"name\":\"NHS Leeds CCG\"}";
  String entry6 = "{\"postcode\":\"EX78PR\",\"easting\":\"297717\", \"northing\":\"81762\",\"name\":\"NHS Devon Clinical Commissioning Group (CCG)\"}";
  String entry7 = "{\"postcode\":\"SE106ZS\",\"easting\":\"297717\", \"northing\":\"81762\",\"name\":\"NHS London\"}";
  String entry8 = "{\"postcode\":\"TF74NJ\",\"easting\":\"297717\", \"northing\":\"81762\",\"name\":\"NHS London\"}";
  String entry9 = "{\"postcode\":\"MK81AS\",\"easting\":\"297717\", \"northing\":\"81762\",\"name\":\"NHS East and North Hertfordshire CCG\"}";
  String entry10 = "{\"postcode\":\"S12HE\",\"easting\":\"435364\", \"northing\":\"387300\",\"name\":\"NHS SHEFFIELD CCG\"}";
  String entry11 = "{\"postcode\":\"DL170HF\",\"easting\":\"428580\", \"northing\":\"529821\",\"name\":\"NHS DURHAM DALES\"}";
  String entry12 = "{\"postcode\":\"DH88TF\",\"easting\":\"428580\", \"northing\":\"529821\",\"name\":\"NHS DURHAM DALES\"}";
  String entry13 = "{\"postcode\":\"NE639UZ\",\"easting\":\"427406\", \"northing\":\"587768\",\"name\":\"NHS Northumberland CCG\"}";


  @Bean
  public void setupDynamoTables() {
    Boolean execute = Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> env.equalsIgnoreCase("local"));
    if (!execute) {
      return;
    }
    DynamoDbClient client = DynamoDbClient.builder()
      .endpointOverride(URI.create(amazonDynamoDBEndpoint))
      .region(Region.of(EU_WEST_2.name()))
      .build();

    CreateTableResponse response = client.createTable(createTable());
    // Wait for the table to become active
    client.waiter().waitUntilTableExists(DescribeTableRequest.builder().tableName(dynamoDbTableName).build());
    DescribeTableResponse tableDescription = client.describeTable(
      DescribeTableRequest.builder().tableName(dynamoDbTableName).build());
    if(!tableDescription.table().tableName().equalsIgnoreCase(dynamoDbTableName)){
    }
    log.info("Table created: " + tableDescription.table());

    String[] itemsStrings = new String[]{entry1, entry2, entry3, entry4, entry5, entry6, entry7, entry8, entry9,entry10,entry11,entry12,entry13};
    List<PutItemRequest> items = Arrays.stream(itemsStrings)
      .map(this::jsonToMap)
      .filter(Objects::nonNull)
      .map(this::createItem)
      .collect(Collectors.toList());

    for (PutItemRequest putItemRequest : items) {
      PutItemResponse putItemResponse = client.putItem(putItemRequest);
      putItemResponse.toString();
    }
  }

  private Map<String, AttributeValue> jsonToMap(String json) {
    try {
      Map<String, AttributeValue> item = new HashMap<>();
      Map<String, String> rawItem = mapper.readValue(json, new TypeReference<Map<String, String>>() {
      });
      item.put(key, AttributeValue.builder().s(rawItem.get("postcode")).build());
      item.put("northing", AttributeValue.builder().n(rawItem.get("northing")).build());
      item.put("easting", AttributeValue.builder().n(rawItem.get("easting")).build());
      item.put("name", AttributeValue.builder().s(rawItem.get("name")).build());

      return item;
    } catch (IOException e) {
      log.error("Unable to convert to map");
    }
    return null;
  }

  private CreateTableRequest createTable() {
    AttributeDefinition attributeDefinition = AttributeDefinition.builder()
      .attributeName(key)
      .attributeType(ScalarAttributeType.S)
      .build();

    ProvisionedThroughput provisionedThroughput = ProvisionedThroughput.builder()
      .readCapacityUnits(1L)
      .writeCapacityUnits(1L)
      .build();

    KeySchemaElement keySchemaElement = KeySchemaElement.builder()
      .attributeName(key)
      .keyType(KeyType.HASH)
      .build();

    CreateTableRequest request = CreateTableRequest.builder()
      .attributeDefinitions(attributeDefinition)
      .keySchema(keySchemaElement)
      .provisionedThroughput(provisionedThroughput)
      .tableName(dynamoDbTableName)
      .build();
    return request;
  }

  private PutItemRequest createItem(Map<String, AttributeValue> item) {
    return PutItemRequest.builder()
      .tableName(dynamoDbTableName)
      .item(item)
      .build();
  }

}
