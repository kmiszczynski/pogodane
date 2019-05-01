package pl.pogodane.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class CityStation {
   @Id
   private String id;
   private String stationName;
   private double distanceFromCityInKilometers;
   private boolean inCityBounds;
   private String stationId;
   /*
     rank rules:
     base rank - 5000
     in city bounds - + 1000
     - distance from city
    */
   private double rank;
   private String type;
}
