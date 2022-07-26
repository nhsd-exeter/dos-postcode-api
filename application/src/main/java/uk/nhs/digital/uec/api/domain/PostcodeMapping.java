package uk.nhs.digital.uec.api.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

// service-finder-local-postcode-location-mapping
/* Domain object for postcode mapping */
@DynamoDBTable(tableName = "uec-dos-api-pc-stg-postcode-location-mapping")
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

  @DynamoDBAttribute(attributeName = "ccgName")
  private String ccg;

  @DynamoDBAttribute(attributeName = "geographyCode")
  private String geographyCode;

  @DynamoDBAttribute(attributeName = "nationalGroupingCode")
  private String nationalGroupingCode;

  @DynamoDBAttribute(attributeName = "organisationCode")
  private String organisationCode;

  @DynamoDBAttribute(attributeName = "nhs_region")
  private String nhs_region;

  @DynamoDBAttribute(attributeName = "icb")
  private String icb;

  @DynamoDBAttribute(attributeName = "email")
  private String email;

  private String region;

  private String subregion;
}
