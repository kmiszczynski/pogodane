package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawDailyMeteorologicalStationKDTData {
   private String stationCode;
   private String stationName;
   private String year;
   private String month;
   private String day;
   private String averageTemperature;
   private String averageTemperatureStatus;
   private String averageHumidity;
   private String averageHumidityStatus;
   private String averageWindSpeed;
   private String averageWindSpeedStatus;
   private String averageCloudiness;
   private String averageCloudinessStatus;
}
