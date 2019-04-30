package pl.pogodane.mongo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WeatherData {
   private BigDecimal avgTemperature;
   private BigDecimal precipitation;
   private BigDecimal cloudinessPercentage;
   private BigDecimal avgWindSpeed;
   private BigDecimal avgAirPressure;
   private BigDecimal avgHumidity;
}
