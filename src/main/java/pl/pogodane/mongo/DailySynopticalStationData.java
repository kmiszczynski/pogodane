package pl.pogodane.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document
@CompoundIndexes({
   @CompoundIndex(name = "stationId_date", def = "{'stationId': 1, 'date': 1}")
}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailySynopticalStationData {
   @Id
   private String id;
   @Indexed
   private String stationId;
   private LocalDate date;
   private BigDecimal averageCloudiness;
   private boolean averageCloudinessMeasured;
   private BigDecimal averageWindSpeed;
   private boolean averageWindSpeedMeasured;
   private BigDecimal averageTemperature;
   private boolean averageTemperatureMeasured;
   private BigDecimal averageHumidity;
   private boolean averageHumidityMeasured;
   private BigDecimal averageAirPressure;
   private boolean averageAirPressureMeasured;
   private BigDecimal maximumTemperature;
   private boolean maximumTemperatureMeasured;
   private BigDecimal minimalTemperature;
   private boolean minimalTemperatureMeasured;
   private BigDecimal rainfallAmount;
   private boolean rainfallAmountMeasured;
   private String rainfallType;
   private BigDecimal snowHeight;
   private boolean snowHeightMeasured;
}
