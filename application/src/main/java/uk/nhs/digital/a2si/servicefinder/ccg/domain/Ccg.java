package uk.nhs.digital.a2si.servicefinder.ccg.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "service-finder-postcode-ccg")
public class Ccg {
    private String postCode;
    private String name;

    /**
     * @return String return the postcode
     */
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "postcode")
    public String getPostCode() {
        return postCode;
    }

    /**
     * @param postcode the postcode to set
     */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    /**
     * @return String return the name
     */
    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
