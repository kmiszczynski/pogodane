package pl.pogodane.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.pogodane.api.CitiesResponse;
import pl.pogodane.api.City;
import pl.pogodane.rest.DICTIONARY;
import pl.pogodane.service.CitiesService;

import java.util.List;
import java.util.Optional;

@RestController
public class CitiesController {
   @Autowired
   private CitiesService citiesService;

   @GetMapping(value = DICTIONARY.ALL_CITIES)
   @CrossOrigin(origins = "http://www.pogodane.pl'")
   public CitiesResponse getAllCities() {
      List<City> cities = citiesService.fetchAllCities();
      return new CitiesResponse(cities);
   }

   @GetMapping(value = DICTIONARY.CITY + "/{technicalId}")
   @CrossOrigin(origins = "http://www.pogodane.pl'")
   public City getCity(@PathVariable String technicalId) {
      Optional<City> city = citiesService.findByTechnicalId(technicalId);

      return city.orElseGet(() -> new City());
   }
}
