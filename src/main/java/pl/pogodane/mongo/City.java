package pl.pogodane.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class City {
   @Id
   private String id;
   private String technicalId;
   private BigDecimal lat;
   private BigDecimal lng;
   private BigDecimal northEastLat;
   private BigDecimal northEastLng;
   private BigDecimal southWestLat;
   private BigDecimal southWestLng;
   private String name;
   private String voivodeship;
   private List<CityStation> cityRainfallStations;
   private List<CityStation> citySynopticStations;
   private List<CityStation> cityMeteorologicalStations;
}
