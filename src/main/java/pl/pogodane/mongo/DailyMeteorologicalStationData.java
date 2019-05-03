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
public class DailyMeteorologicalStationData {
   @Id
   private String id;
   @Indexed
   private String stationId;
   private LocalDate date;
   private BigDecimal maximumTemperature;
   private boolean maximumTemperatureMeasured;
   private BigDecimal minimumTemperature;
   private boolean minimumTemperatureMeasured;
   private BigDecimal averageTemperature;
   private boolean averageTemperatureMeasured;
   private BigDecimal rainfallAmount;
   private boolean rainfallAmountMeasured;
   private String rainfallType;
   private BigDecimal snowHeight;
   private boolean snowHeightMeasured;
   private BigDecimal humidity;
   private boolean humidityMeasured;
   private BigDecimal windSpeed;
   private boolean windSpeedMeasured;
   private BigDecimal cloudiness;
   private boolean cloudinessMeasured;
}
