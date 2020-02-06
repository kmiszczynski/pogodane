package pl.pogodane.api;

import lombok.*;
import pl.pogodane.mongo.YearlyCityData;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllYearsCityResponse {
   private List<YearlyCityData> yearlyCityData;
   private BigDecimal maximumTemperature;
   private String maximumTemperatureYear;
   private String maximumTemperatureMonth;
   private BigDecimal minimumTemperature;
   private String minimumTemperatureYear;
   private String minimumTemperatureMonth;
   private BigDecimal maximumSnowHeight;
   private String maximumSnowHeightYear;
   private String maximumSnowHeightMonth;
   private BigDecimal maximumRainfallAmount;
   private String maximumRainfallAmountYear;
   private String maximumRainfallAmountMonth;
}
