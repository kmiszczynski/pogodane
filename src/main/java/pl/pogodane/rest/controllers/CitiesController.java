package pl.pogodane.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pogodane.api.CitiesResponse;
import pl.pogodane.api.City;
import pl.pogodane.rest.DICTIONARY;
import pl.pogodane.service.CitiesService;

import java.util.List;

@RestController
public class CitiesController {
   @Autowired
   private CitiesService citiesService;

   @GetMapping(value = DICTIONARY.ALL_CITIES)
   @CrossOrigin(origins = "http://localhost:4200")
   public CitiesResponse getAllCities() {
      List<City> cities = citiesService.fetchAllCities();
      return new CitiesResponse(cities);
   }
}
