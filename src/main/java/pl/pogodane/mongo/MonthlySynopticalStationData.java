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
   @CompoundIndex(name = "stationId_year_month", def = "{'stationId': 1, 'year': 1, 'month': 1}")
}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlySynopticalStationData {
   @Id
   private String id;
   @Indexed
   private String stationId;
   private String year;
   private String month;
   private BigDecimal maximumTemperature;
   private boolean maximumTemperatureMeasured;
   private BigDecimal averageMaximumTemperature;
   private boolean averageMaximumTemperatureMeasured;
   private BigDecimal minimalTemperature;
   private boolean minimalTemperatureMeasured;
   private BigDecimal averageMinimalTemperature;
   private boolean averageMinimalTemperatureMeasured;
   private BigDecimal averageTemperature;
   private boolean averageTemperatureMeasured;
   private BigDecimal rainfallAmount;
   private boolean rainfallAmountMeasured;
   private BigDecimal maximumRainfallAmount;
   private boolean maximumRainfallAmountMeasured;
   private BigDecimal maximumSnowHeight;
   private boolean maximumSnowHeightMeasured;
   private int daysWithSnowSurface;
   private boolean daysWithSnowSurfaceMeasured;
   private int daysWithRain;
   private boolean daysWithRainMeasured;
   private int daysWithSnow;
   private boolean daysWithSnowMeasured;
   private BigDecimal averageCloudiness;
   private boolean averageCloudinessMeasured;
   private BigDecimal averageWindSpeed;
   private boolean averageWindSpeedMeasured;
   private BigDecimal averageHumidity;
   private boolean averageHumidityMeasured;
   private BigDecimal averageAirPressure;
   private boolean averageAirPressureMeasured;
}
