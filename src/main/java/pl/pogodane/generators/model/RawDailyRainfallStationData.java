package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawDailyRainfallStationData {
   private String stationCode;
   private String stationName;
   private String year;
   private String month;
   private String day;
   private String rainfallAmount;
   private String rainfallStatus;
   private String rainfallType;
   private String snowHeight;
   private String snowStatus;
}
