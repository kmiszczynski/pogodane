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
import java.math.RoundingMode;
import java.util.*;
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
        Collections.sort(yearlyCityData, Comparator.comparing(YearlyCityData::getYear));

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
        YearlyCityData maximumYearlyRainfallAmount = yearlyCityData.stream()
                .filter(data -> data.getRainfallAmount() != null)
                .max(Comparator.comparing(YearlyCityData::getRainfallAmount))
                .orElseGet(() -> new YearlyCityData());
        List<YearlyCityData> cityDataWithAverageTemperature = yearlyCityData.stream()
                .filter(data -> data.getAverageTemperature() != null)
                .collect(Collectors.toList());
        Optional<BigDecimal> sumOfAverageTemperatures = cityDataWithAverageTemperature.stream()
                .map(YearlyCityData::getAverageTemperature)
                .reduce(BigDecimal::add);

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
                .averageTemperature(sumOfAverageTemperatures.map(sum -> sum.divide(
                        BigDecimal.valueOf(cityDataWithAverageTemperature.size()), 2, RoundingMode.HALF_UP))
                        .orElse(null))
                .maximumYearlyRainfallAmount(maximumYearlyRainfallAmount.getMaximumRainfallAmount())
                .maximumYearlyRainfallAmountYear(maximumYearlyRainfallAmount.getYear())
                .build();
    }
}
