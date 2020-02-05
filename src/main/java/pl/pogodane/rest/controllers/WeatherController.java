package pl.pogodane.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.pogodane.api.YearForCityResponse;
import pl.pogodane.rest.DICTIONARY;
import pl.pogodane.service.WeatherService;

@RestController
public class WeatherController {
   @Autowired
   private WeatherService weatherService;

   @GetMapping(value = DICTIONARY.WEATHER_DATA + "/{city}/{year}")
   @CrossOrigin(origins = "http://localhost:4200")
   public YearForCityResponse getYearForCityResponse(@PathVariable String city, @PathVariable String year) {
      return weatherService.createYearForCityResponse(city, year);
   }
}
