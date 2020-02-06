package pl.pogodane.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pogodane.api.AllYearsCityResponse;
import pl.pogodane.api.YearForCityResponse;
import pl.pogodane.mongo.MonthlyCityData;
import pl.pogodane.mongo.YearlyCityData;
import pl.pogodane.mongo.repositories.MonthlyCityDataRepostiory;
import pl.pogodane.mongo.repositories.YearlyCityDataRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

      YearlyCityData maximumTemperature = yearlyCityData.stream()
         .filter(data -> data.getMaximumTemperature() != null)
         .max(Comparator.comparing(YearlyCityData::getMaximumTemperature))
         .orElseGet(() -> new YearlyCityData());
      YearlyCityData minimumTemperature = yearlyCityData.stream()
         .filter(data -> data.getMinimumTemperature() != null)
         .min(Comparator.comparing(YearlyCityData::getMinimumTemperature))
         .orElseGet(() -> new YearlyCityData());
      YearlyCityData maximumSnowHeight = yearlyCityData.stream()
         .filter(data -> data.getMaximumSnowHeight() != null)
         .max(Comparator.comparing(YearlyCityData::getMaximumSnowHeight))
         .orElseGet(() -> new YearlyCityData());
      YearlyCityData maximumRainfallAmount = yearlyCityData.stream()
         .filter(data -> data.getMaximumRainfallAmount() != null)
         .max(Comparator.comparing(YearlyCityData::getMaximumRainfallAmount))
         .orElseGet(() -> new YearlyCityData());

      return AllYearsCityResponse.builder()
         .yearlyCityData(yearlyCityData)
         .maximumTemperature(maximumTemperature.getMaximumTemperature())
         .maximumTemperatureMonth(maximumTemperature.getMaximumTemperatureMonth())
         .maximumTemperatureYear(maximumTemperature.getYear())
         .minimumTemperature(minimumTemperature.getMinimumTemperature())
         .minimumTemperatureMonth(minimumTemperature.getMinimumTemperatureMonth())
         .minimumTemperatureYear(minimumTemperature.getYear())
         .maximumSnowHeight(maximumSnowHeight.getMaximumSnowHeight())
         .maximumSnowHeightMonth(maximumSnowHeight.getMaximumSnowHeightMonth())
         .maximumSnowHeightYear(maximumSnowHeight.getYear())
         .maximumRainfallAmount(maximumRainfallAmount.getMaximumRainfallAmount())
         .maximumRainfallAmountMonth(maximumRainfallAmount.getMaximumRainfallMonth())
         .maximumRainfallAmountYear(maximumRainfallAmount.getYear())
         .build();
   }
}
