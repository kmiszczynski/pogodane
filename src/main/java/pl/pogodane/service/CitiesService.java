package pl.pogodane.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pogodane.api.City;
import pl.pogodane.mongo.repositories.CityRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CitiesService {
   @Autowired
   private CityRepository cityRepository;

   public List<City> fetchAllCities() {
      return cityRepository.findAll()
         .stream()
         .map(this::map)
         .collect(Collectors.toList());
   }

   public Optional<City> findByTechnicalId(String technicalId) {
      pl.pogodane.mongo.City city = cityRepository.findByTechnicalId(technicalId);
      if (city == null) {
         return Optional.empty();
      }
      return Optional.of(map(city));
   }

   private City map(pl.pogodane.mongo.City input) {
      return City.builder()
         .name(input.getName())
         .voivodeship(input.getVoivodeship())
         .technicalId(input.getTechnicalId())
         .build();
   }
}
