package pl.pogodane.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@CompoundIndexes({
   @CompoundIndex(name = "stationId_year", def = "{'stationId': 1, 'year': 1}")
}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyMeteorologicalStationData {
   @Id
   private String id;
   @Indexed
   private String stationId;
   private String year;
   private String month;
   private BigDecimal maximumTemperature;
   private boolean maximumTemperatureMeasured;
   private BigDecimal minimumTemperature;
   private boolean minimumTemperatureMeasured;
   private BigDecimal averageTemperature;
   private boolean averageTemperatureMeasured;
   private BigDecimal averageMaximumTemperature;
   private boolean averageMaximumTemperatureMeasured;
   private BigDecimal averageMinimumTemperature;
   private boolean averageMinimumTemperatureMeasured;
   private BigDecimal rainfallAmount;
   private boolean rainfallAmountMeasured;
   private BigDecimal averageHumidity;
   private boolean averageHumidityMeasured;
   private BigDecimal averageWindSpeed;
   private boolean averageWindSpeedMeasured;
   private BigDecimal averageCloudiness;
   private boolean averageCloudinessMeasured;
   private BigDecimal maximumRainfallAmount;
   private boolean maximumRainfallAmountMeasured;
}
