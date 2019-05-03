package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawDailyMeteorologicalStationKDData {
   private String stationCode;
   private String stationName;
   private String year;
   private String month;
   private String day;
   private String maximalTemperature;
   private String maximalTemperatureStatus;
   private String minimalTemperature;
   private String minimalTemperatureStatus;
   private String averageTemperature;
   private String averageTemperatureStatus;
   private String minimalGroundTemperature;
   private String minimalGroundTemperatureStatus;
   private String rainfallAmount;
   private String rainfallAmountStatus;
   private String rainfallType;
   private String snowHeight;
   private String snowHeightStatus;
}
