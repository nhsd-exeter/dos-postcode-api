package uk.nhs.digital.uec.api.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CCGRecord implements Comparable<CCGRecord> {

  @CsvBindByPosition(position = 0)
  private String postcode;

  @CsvBindByPosition(position = 2)
  private String orgCode;

  @Override
  public int compareTo(CCGRecord o) {
    return this.getPostcode()
        .replaceAll("\\s+", "")
        .compareTo(o.getPostcode().replaceAll("\\s+", ""));
  }
}
