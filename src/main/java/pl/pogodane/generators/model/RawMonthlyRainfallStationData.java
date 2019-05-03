package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawMonthlyRainfallStationData {
   private String stationCode;
   private String stationName;
   private String year;
   private String month;
   private String rainfallAmount;
   private String rainfallAmountStatus;
   private String daysWithSnow;
   private String daysWithSnowStatus;
   private String maximumRainfallAmount;
   private String maximumRainfallAmountStatus;
}
