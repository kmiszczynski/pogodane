package pl.pogodane.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pogodane.api.AllYearsCityResponse;
import pl.pogodane.api.YearForCityResponse;
import pl.pogodane.mongo.MonthlyCityData;
import pl.pogodane.mongo.YearlyCityData;
import pl.pogodane.mongo.repositories.MonthlyCityDataRepostiory;
import pl.pogodane.mongo.repositories.YearlyCityDataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {
   @Autowired
   private YearlyCityDataRepository yearlyCityDataRepository;
   @Autowired
   private MonthlyCityDataRepostiory monthlyCityDataRepostiory;

   public YearForCityResponse createYearForCityResponse(String cityTechnicalId, String year) {
      YearlyCityData yearlyCityData = yearlyCityDataRepository.findByCityTechnicalIdAndYear(cityTechnicalId, year);
      List<MonthlyCityData> monthlyCityData = monthlyCityDataRepostiory.findAllByCityTechnicalIdAndYear(cityTechnicalId, year);

      return new YearForCityResponse(yearlyCityData, monthlyCityData);
   }

   public AllYearsCityResponse createAllYearCityResponse(String cityTechnicalId) {
      List<YearlyCityData> yearlyCityData = new ArrayList<>(yearlyCityDataRepository.findAllByCityTechnicalId(cityTechnicalId));

      return new AllYearsCityResponse(yearlyCityData);
   }
}
