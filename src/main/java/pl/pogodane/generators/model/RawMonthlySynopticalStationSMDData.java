package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawMonthlySynopticalStationSMDData {
   private String stationCode;
   private String stationName;
   private String year;
   private String month;
   private String maximumTemperature;
   private String maximumTemperatureStatus;
   private String averageMaximumTemperature;
   private String averageMaximumTemperatureStatus;
   private String minimalTemperature;
   private String minimalTemperatureStatus;
   private String averageMinimalTemperature;
   private String averageMinimalTemperatureStatus;
   private String averageTemperature;
   private String averageTemperatureStatus;
   private String minimalGroundTemperature;
   private String minimalGroundTemperatureStatus;
   private String rainfallAmount;
   private String rainfallAmountStatus;
   private String maximumRainfallAmount;
   private String maximumRainfallAmountStatus;
   private String firstDayOfMaximumRainfall;
   private String lastDayOfMaximumRainfall;
   private String timeOfSun;
   private String timeOfSunStatus;
   private String maximumSnowHeight;
   private String maximumSnowHeightStatus;
   private String daysWithSnowSurface;
   private String daysWithSnowSurfaceStatus;
   private String daysWithRain;
   private String daysWithRainStatus;
   private String daysWithSnow;
   private String daysWithSnowStatus;

}
