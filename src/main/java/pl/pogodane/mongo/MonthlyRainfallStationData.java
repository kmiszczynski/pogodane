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
public class MonthlyRainfallStationData {
   @Id
   private String id;
   @Indexed
   private String stationId;
   private String year;
   private String month;
   private BigDecimal rainfallAmount;
   private boolean rainfallAmountMeasured;
   private int daysWithSnow;
   private boolean daysWithSnowMeasured;
   private BigDecimal maximumRainfallAmount;
   private boolean maximumRainfallMeasured;
}
