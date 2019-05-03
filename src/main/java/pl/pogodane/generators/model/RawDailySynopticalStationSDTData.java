package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawDailySynopticalStationSDTData {
   private String stationCode;
   private String stationName;
   private String year;
   private String month;
   private String day;
   private String averageCloudiness;
   private String averageCloudinessStatus;
   private String averageWindSpeed;
   private String averageWindSpeedStatus;
   private String averageTemperature;
   private String averageTemperatureStatus;
   private String averageSteamPressure;
   private String averageSteamPressureStatus;
   private String averageHumidity;
   private String averageHumidityStatus;
   private String averagePressure;
   private String averagePressureStatus;
}
