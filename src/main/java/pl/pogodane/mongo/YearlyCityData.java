package pl.pogodane.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@CompoundIndexes({
   @CompoundIndex(name = "cityTechnicalId_year", def = "{'cityTechnicalId': 1, 'year': 1}")
}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YearlyCityData {
   private String id;
   private String cityTechnicalId;
   private String cityName;
   private String year;
   private BigDecimal rainfallAmount;
   private boolean rainfallAmountMeasured;
   private int daysWithSnow;
   private BigDecimal maximumRainfallAmount;
   private String maximumRainfallMonth;
   private boolean maximumRainfallAmountMeasured;
   private BigDecimal maximumTemperature;
   private String maximumTemperatureMonth;
   private boolean maximumTemperatureMeasured;
   private BigDecimal minimumTemperature;
   private String minimumTemperatureMonth;
   private boolean minimumTemperatureMeasured;
   private BigDecimal averageTemperature;
   private boolean averageTemperatureMeasured;
   private BigDecimal averageMaximumTemperature;
   private boolean averageMaximumTemperatureMeasured;
   private BigDecimal averageMinimumTemperature;
   private boolean averageMinimumTemperatureMeasured;
   private BigDecimal averageHumidity;
   private boolean averageHumidityMeasured;
   private BigDecimal averageWindSpeed;
   private boolean averageWindSpeedMeasured;
   private BigDecimal averageCloudiness;
   private boolean averageCloudinessMeasured;
   private BigDecimal maximumSnowHeight;
   private String maximumSnowHeightMonth;
   private boolean maximumSnowHeightMeasured;
   private int daysWithSnowSurface;
   private int daysWithRain;
   private BigDecimal averageAirPressure;
   private boolean averageAirPressureMeasured;
}
