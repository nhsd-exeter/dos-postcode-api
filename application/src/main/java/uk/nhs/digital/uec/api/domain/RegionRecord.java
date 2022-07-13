package uk.nhs.digital.uec.api.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegionRecord {

  @CsvBindByName(column = "Postcode")
  String partPostcode;
  @CsvBindByName(column = "Region")
  String subRegion;
  @CsvBindByName(column = "Nearby districts")
  String[] districts;
  @CsvBindByName(column = "UK region")
  String region;
}
