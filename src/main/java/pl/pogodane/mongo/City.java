package pl.pogodane.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
   private List<CityStation> cityStations;
}
