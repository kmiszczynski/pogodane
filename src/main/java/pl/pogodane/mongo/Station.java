package pl.pogodane.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Station {
   @Id
   private String id;
   private String stationId;
   private String stationName;
   private BigDecimal lat;
   private BigDecimal lng;
   private BigDecimal height;
   private int count;
   private int years;
   private int firstYear;
   private int lastYear;
   private String type;
}
