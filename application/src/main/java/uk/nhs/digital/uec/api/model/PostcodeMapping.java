package uk.nhs.digital.uec.api.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// service-finder-local-postcode-location-mapping
/* Domain object for postcode mapping */
@DynamoDBTable(tableName = "service-finder-local-postcode-location-mapping")
@Getter
@Setter
@ToString
public class PostcodeMapping {
  @DynamoDBHashKey
  @DynamoDBAttribute(attributeName = "postcode")
  private String postcode;

  @DynamoDBAttribute(attributeName = "name")
  private String name;

  @DynamoDBAttribute(attributeName = "easting")
  private Integer easting;

  @DynamoDBAttribute(attributeName = "northing")
  private Integer northing;

  @DynamoDBAttribute(attributeName = "ccgName")
  private String ccg;

  @DynamoDBAttribute(attributeName = "organisationCode")
  private String organisationCode;

  @DynamoDBAttribute(attributeName = "nhs_region")
  private String nhs_region;

  @DynamoDBAttribute(attributeName = "icb")
  private String icb;

  @DynamoDBAttribute(attributeName = "email")
  private String email;

  private String region;

  private String subRegion;
}
