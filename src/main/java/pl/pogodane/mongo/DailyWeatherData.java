package pl.pogodane.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Getter
@Setter
public class DailyWeatherData {
   @Id
   private String id;
   private LocalDate date;
   private WeatherData weatherData;
}
