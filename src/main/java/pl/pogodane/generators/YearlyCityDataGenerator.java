package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pogodane.mongo.MonthlyCityData;
import pl.pogodane.mongo.YearlyCityData;
import pl.pogodane.mongo.repositories.MonthlyCityDataRepostiory;
import pl.pogodane.mongo.repositories.YearlyCityDataRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class YearlyCityDataGenerator implements DataGenerator {

   @Autowired
   private MonthlyCityDataRepostiory monthlyCityDataRepostiory;
   @Autowired
   private YearlyCityDataRepository yearlyCityDataRepository;

   private static final BigDecimal NUMBER_OF_MONTHS = new BigDecimal(12);

   @Override public void generateData() {
      log.info("Fetching monthly city data...");
      List<MonthlyCityData> monthlyCityData = monthlyCityDataRepostiory.findAll();
      Map<String, List<MonthlyCityData>> monthlyCityDataByYear = monthlyCityData
         .stream()
         .collect(Collectors.groupingBy(MonthlyCityData::getYear));
      monthlyCityDataByYear.values().forEach(this::handleYear);
      log.info("All data persisted");
   }

   private void handleYear(List<MonthlyCityData> dataForYear) {
      Map<String, List<MonthlyCityData>> yearlyDataGroupedByCity = dataForYear.stream()
         .collect(Collectors.groupingBy(MonthlyCityData::getCityTechnicalId));

      yearlyDataGroupedByCity.values().forEach(this::handleYearCityPair);
   }

   private void handleYearCityPair(List<MonthlyCityData> monthlyCityData) {
      MonthlyCityData firstDataForYear = monthlyCityData.get(0);
      YearlyCityData result = new YearlyCityData();
      result.setYear(firstDataForYear.getYear());
      result.setCityName(firstDataForYear.getCityName());
      result.setCityTechnicalId(firstDataForYear.getCityTechnicalId());

      if (monthlyCityData.size() == 12) {
         calculateRainfallAmount(monthlyCityData, result);
         calculateAverageTemperature(monthlyCityData, result);
         calculateAverageHumidity(monthlyCityData, result);
         calculateAverageWindSpeed(monthlyCityData, result);
         calculateAverageCloudiness(monthlyCityData, result);
         calculateAverageAirPressure(monthlyCityData, result);
      }

      calculateDaysWithSnow(monthlyCityData, result);
      calculateMaximumRainfallAmount(monthlyCityData, result);
      calculateMaximumTemperature(monthlyCityData, result);
      calculateMinimumTemperature(monthlyCityData, result);
      calculateMaximumSnowHeight(monthlyCityData, result);
      calculateDaysWithRain(monthlyCityData, result);
      calculateDaysWithSnowSurface(monthlyCityData, result);

      log.info("Persisting {} year for {}", firstDataForYear.getYear(), firstDataForYear.getCityName());
      yearlyCityDataRepository.save(result);
   }

   private void calculateAverageAirPressure(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean allDataExists = monthlyCityData.stream()
         .allMatch(MonthlyCityData::isAverageAirPressureMeasured);
      if (allDataExists) {
         Optional<BigDecimal> sumOfAirPressure = monthlyCityData.stream()
            .map(MonthlyCityData::getAverageAirPressure)
            .reduce(BigDecimal::add);
         BigDecimal average = sumOfAirPressure.get().divide(NUMBER_OF_MONTHS, 2, RoundingMode.HALF_UP);

         result.setAverageAirPressure(average);
         result.setAverageAirPressureMeasured(true);
      }
   }

   private void calculateDaysWithSnowSurface(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      Optional<Integer> daysWithSnowSurface = monthlyCityData.stream()
         .map(MonthlyCityData::getDaysWithSnowSurface)
         .reduce(Integer::sum);
      result.setDaysWithSnowSurface(daysWithSnowSurface.orElse(0));
   }

   private void calculateDaysWithRain(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      Optional<Integer> daysWithRain = monthlyCityData.stream()
         .map(MonthlyCityData::getDaysWithRain)
         .reduce(Integer::sum);
      result.setDaysWithRain(daysWithRain.orElse(0));
   }

   private void calculateMaximumSnowHeight(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean anyDataExists = monthlyCityData.stream()
         .anyMatch(MonthlyCityData::isMaximumSnowHeightMeasured);
      if (anyDataExists) {
         Optional<MonthlyCityData> maximumSnowHeightMonth = monthlyCityData.stream()
            .filter(m -> m.getMaximumSnowHeight() != null)
            .max(Comparator.comparing(MonthlyCityData::getMaximumSnowHeight));
         result.setMaximumSnowHeight(maximumSnowHeightMonth.get().getMaximumSnowHeight());
         result.setMaximumSnowHeightMonth(maximumSnowHeightMonth.get().getMonth());
         result.setMaximumSnowHeightMeasured(true);
      }
   }

   private void calculateAverageCloudiness(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean allDataExists = monthlyCityData.stream()
         .allMatch(MonthlyCityData::isAverageCloudinessMeasured);
      if (allDataExists) {
         Optional<BigDecimal> sumOfCloudiness = monthlyCityData.stream()
            .map(MonthlyCityData::getAverageCloudiness)
            .reduce(BigDecimal::add);
         BigDecimal average = sumOfCloudiness.get().divide(NUMBER_OF_MONTHS, 2, RoundingMode.HALF_UP);

         result.setAverageCloudiness(average);
         result.setAverageCloudinessMeasured(true);
      }
   }

   private void calculateAverageWindSpeed(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean allDataExists = monthlyCityData.stream()
         .allMatch(MonthlyCityData::isAverageWindSpeedMeasured);
      if (allDataExists) {
         Optional<BigDecimal> sumOfWindSpeed = monthlyCityData.stream()
            .map(MonthlyCityData::getAverageWindSpeed)
            .reduce(BigDecimal::add);
         BigDecimal average = sumOfWindSpeed.get().divide(NUMBER_OF_MONTHS, 2, RoundingMode.HALF_UP);

         result.setAverageWindSpeed(average);
         result.setAverageWindSpeedMeasured(true);
      }
   }

   private void calculateAverageHumidity(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean allDataExists = monthlyCityData.stream()
         .allMatch(MonthlyCityData::isAverageHumidityMeasured);
      if (allDataExists) {
         Optional<BigDecimal> sumOfHumidity = monthlyCityData.stream()
            .map(MonthlyCityData::getAverageHumidity)
            .reduce(BigDecimal::add);
         BigDecimal average = sumOfHumidity.get().divide(NUMBER_OF_MONTHS, 2, RoundingMode.HALF_UP);

         result.setAverageHumidity(average);
         result.setAverageHumidityMeasured(true);
      }
   }

   private void calculateAverageTemperature(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean allDataExists = monthlyCityData.stream()
         .allMatch(MonthlyCityData::isAverageTemperatureMeasured);
      if (allDataExists) {
         Optional<BigDecimal> sumOfTemperatures = monthlyCityData.stream()
            .map(MonthlyCityData::getAverageTemperature)
            .reduce(BigDecimal::add);
         BigDecimal average = sumOfTemperatures.get().divide(NUMBER_OF_MONTHS, 2, RoundingMode.HALF_UP);

         result.setAverageTemperature(average);
         result.setAverageTemperatureMeasured(true);
      }
   }

   private void calculateMinimumTemperature(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean anyDataExists = monthlyCityData.stream()
         .anyMatch(MonthlyCityData::isMinimumTemperatureMeasured);
      if (anyDataExists) {
         Optional<MonthlyCityData> minimumTemperatureMonth = monthlyCityData.stream()
            .filter(m -> m.getMinimumTemperature() != null)
            .min(Comparator.comparing(MonthlyCityData::getMinimumTemperature));
         result.setMinimumTemperature(minimumTemperatureMonth.get().getMinimumTemperature());
         result.setMinimumTemperatureMonth(minimumTemperatureMonth.get().getMonth());
         result.setMinimumTemperatureMeasured(true);
      }
   }

   private void calculateMaximumTemperature(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean anyDataExists = monthlyCityData.stream()
         .anyMatch(MonthlyCityData::isMaximumTemperatureMeasured);
      if (anyDataExists) {
         Optional<MonthlyCityData> maximumTemperatureMonth = monthlyCityData.stream()
            .filter(m -> m.getMaximumTemperature() != null)
            .max(Comparator.comparing(MonthlyCityData::getMaximumTemperature));
         result.setMaximumTemperature(maximumTemperatureMonth.get().getMaximumTemperature());
         result.setMaximumTemperatureMonth(maximumTemperatureMonth.get().getMonth());
         result.setMaximumTemperatureMeasured(true);
      }
   }

   private void calculateMaximumRainfallAmount(List<MonthlyCityData> monthlyCityData, YearlyCityData result) {
      boolean anyDataExists = monthlyCityData.stream()
         .anyMatch(MonthlyCityData::isMaximumRainfallAmountMeasured);
      if (anyDataExists) {
         Optional<MonthlyCityData> maximumRainfallAmountMonth = monthlyCityData.stream()
            .filter(m -> m.getMaximumRainfallAmount() != null)
            .max(Comparator.comparing(MonthlyCityData::getMaximumRainfallAmount));
         result.setMaximumRainfallAmount(maximumRainfallAmountMonth.get().getMaximumRainfallAmount());
         result.setMaximumRainfallMonth(maximumRainfallAmountMonth.get().getMonth());
         result.setMaximumRainfallAmountMeasured(true);
      }
   }

   // TODO KM: check why calculated incorrectly
   private void calculateDaysWithSnow(List<MonthlyCityData> dataForYear, YearlyCityData result) {
      Optional<Integer> daysWithSnow = dataForYear.stream()
         .map(MonthlyCityData::getDaysWithSnow)
         .reduce(Integer::sum);
      result.setDaysWithSnow(daysWithSnow.orElse(0));
   }

   private void calculateRainfallAmount(List<MonthlyCityData> dataForYear, YearlyCityData result) {
      boolean allDataExists = dataForYear.stream()
         .allMatch(MonthlyCityData::isRainfallAmountMeasured);
      if (allDataExists) {
         Optional<BigDecimal> yearlyRainfallAmount = dataForYear.stream()
            .map(MonthlyCityData::getRainfallAmount)
            .reduce(BigDecimal::add);
         result.setRainfallAmount(yearlyRainfallAmount.get());
         result.setRainfallAmountMeasured(true);
      }
   }
}
