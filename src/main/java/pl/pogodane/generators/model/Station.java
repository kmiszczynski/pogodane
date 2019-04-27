package pl.pogodane.generators.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Station {
   private String stationId;
   private String stationName;
   private String lat;
   private String lng;
   private String height;
   private String count;
   private String years;
   private String firstYear;
   private String lastYear;
   private String type;
}
