package uk.nhs.digital.uec.api.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ICBRecord implements Comparable<ICBRecord>{

  @CsvBindByName(column = "organisation_code")
  public String orgCode;

  @CsvBindByName(column = "nhs_region")
  public String nhsRegion;

  @CsvBindByName(column = "nhs_icb")
  public String nhsIcb;

  @CsvBindByName(column = "email")
  public String email;

  @Override
  public int compareTo(ICBRecord o) {
    return this.orgCode.compareTo(o.getOrgCode());
  }
}
