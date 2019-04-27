package pl.pogodane.generators.model.google;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Point {
   private BigDecimal lat;
   private BigDecimal lng;
}
