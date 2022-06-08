package uk.nhs.digital.uec.api.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

// service-finder-local-postcode-location-mapping
/* Domain object for postcode mapping */
@DynamoDBTable(tableName = "uec-dos-api-pc-sgg-postcode-location-mapping")
@Getter
@Setter
public class PostcodeMapping {
  @DynamoDBHashKey
  @DynamoDBAttribute(attributeName = "postcode")
  private String postCode;

  @DynamoDBAttribute(attributeName = "name")
  private String name;

  @DynamoDBAttribute(attributeName = "easting")
  private Integer easting;

  @DynamoDBAttribute(attributeName = "northing")
  private Integer northing;
}
