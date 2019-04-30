package pl.pogodane.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {
   private String name;
   private String voivodeship;
   private String technicalId;
}
