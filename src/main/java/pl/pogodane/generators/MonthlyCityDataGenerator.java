package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.pogodane.generators.utils.DateEntryUtils;
import pl.pogodane.generators.utils.StationUtils;
import pl.pogodane.mongo.*;
import pl.pogodane.mongo.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MonthlyCityDataGenerator implements DataGenerator {

   private static final int LAST_YEAR = 2019;

   @Autowired
   private CityRepository cityRepository;
   @Autowired
   private StationRepository stationRepository;
   @Autowired
   private MonthlyRainfallStationDataRepository rainfallStationDataRepository;
   @Autowired
   private MonthlySynopticalStationDataRepository synopticalStationDataRepository;
   @Autowired
   private MonthlyMeteorologicalStationDataRepository meteorologicalStationDataRepository;
   @Autowired
   private MonthlyCityDataRepostiory monthlyCityDataRepostiory;

   public void generateData() {
      List<City> allCities = cityRepository.findAll();
      allCities
         .parallelStream()
         .forEach(this::handleCity);
   }

   private void handleCity(City city) {
      List<Station> allStationsForCity = getAllStationsForCity(city);
      int yearOfOldestEntry = StationUtils.getYearOfOldestEntry(allStationsForCity);

      for (int year = yearOfOldestEntry; year <= LAST_YEAR; year++) {
         for (int month = 1; month <= 12; month++) {
            MonthlyCityData monthlyCityData = createMonthlyCityData(city, year, month);
            if (monthlyCityData.anyDataExists()) {
               monthlyCityDataRepostiory.save(monthlyCityData);
            }
         }
      }
      log.info("Persisted data for city: {}", city.getName());
   }

   private MonthlyCityData createMonthlyCityData(City city, int year, int month) {

      List<String> stationsUsed = new ArrayList<>();

      Optional<MonthlyRainfallStationData> monthlyRainfallStationData = findMonthlyRainfallStationData(city, year, month);
      Optional<MonthlySynopticalStationData> monthlySynopticalStationData = findMonthlySynopticalStationData(city, year, month);
      Optional<MonthlyMeteorologicalStationData> monthlyMeteorologicalStationData = findMonthlyMeteorologicalStationData(city, year, month);

      MonthlyCityData.MonthlyCityDataBuilder builder = MonthlyCityData.builder()
         .cityTechnicalId(city.getTechnicalId())
         .year(String.valueOf(year))
         .month(DateEntryUtils.getMonthAsString(month))
         .cityName(city.getName());
      if (monthlyRainfallStationData.isPresent()) {
         MonthlyRainfallStationData data = monthlyRainfallStationData.get();
         stationsUsed.add(data.getStationId());
         builder = getBuilderForRainfallData(builder, data);
      }
      if (monthlyMeteorologicalStationData.isPresent()) {
         MonthlyMeteorologicalStationData data = monthlyMeteorologicalStationData.get();
         stationsUsed.add(data.getStationId());
         builder = getBuilderForMeteorologicalData(builder, data);
      }
      if (monthlySynopticalStationData.isPresent()) {
         MonthlySynopticalStationData data = monthlySynopticalStationData.get();
         stationsUsed.add(data.getStationId());
         builder = getBuilderForSynopticalData(builder, data);
      }
      return builder
         .stationsUsed(stationsUsed)
         .build();
   }

   private MonthlyCityData.MonthlyCityDataBuilder getBuilderForSynopticalData(MonthlyCityData.MonthlyCityDataBuilder builder, MonthlySynopticalStationData data) {
      return builder
         .maximumTemperature(data.getMaximumTemperature())
         .maximumTemperatureMeasured(data.isMaximumTemperatureMeasured())
         .averageMaximumTemperature(data.getAverageMaximumTemperature())
         .averageMaximumTemperatureMeasured(data.isAverageMaximumTemperatureMeasured())
         .minimumTemperature(data.getMinimalTemperature())
         .minimumTemperatureMeasured(data.isMinimalTemperatureMeasured())
         .averageMinimumTemperature(data.getAverageMinimalTemperature())
         .averageMinimumTemperatureMeasured(data.isAverageMinimalTemperatureMeasured())
         .averageTemperature(data.getAverageTemperature())
         .averageTemperatureMeasured(data.isAverageTemperatureMeasured())
         .maximumSnowHeight(data.getMaximumSnowHeight())
         .maximumSnowHeightMeasured(data.isMaximumSnowHeightMeasured())
         .daysWithSnowSurface(data.getDaysWithSnowSurface())
         .daysWithRain(data.getDaysWithRain())
         .averageCloudiness(data.getAverageCloudiness())
         .averageCloudinessMeasured(data.isAverageCloudinessMeasured())
         .averageWindSpeed(data.getAverageWindSpeed())
         .averageWindSpeedMeasured(data.isAverageWindSpeedMeasured())
         .averageHumidity(data.getAverageHumidity())
         .averageHumidityMeasured(data.isAverageHumidityMeasured())
         .averageAirPressure(data.getAverageAirPressure())
         .averageAirPressureMeasured(data.isAverageAirPressureMeasured());
   }

   private MonthlyCityData.MonthlyCityDataBuilder getBuilderForRainfallData(MonthlyCityData.MonthlyCityDataBuilder builder, MonthlyRainfallStationData data) {
      return builder
         .maximumRainfallAmount(data.getMaximumRainfallAmount())
         .maximumRainfallAmountMeasured(data.isMaximumRainfallMeasured())
         .rainfallAmount(data.getRainfallAmount())
         .rainfallAmountMeasured(data.isRainfallAmountMeasured())
         .daysWithSnow(data.getDaysWithSnow());
   }

   private MonthlyCityData.MonthlyCityDataBuilder getBuilderForMeteorologicalData(MonthlyCityData.MonthlyCityDataBuilder builder, MonthlyMeteorologicalStationData data) {
      return builder
         .maximumTemperature(data.getMaximumTemperature())
         .maximumTemperatureMeasured(data.isMaximumTemperatureMeasured())
         .minimumTemperature(data.getMinimumTemperature())
         .minimumTemperatureMeasured(data.isMinimumTemperatureMeasured())
         .averageTemperature(data.getAverageTemperature())
         .averageTemperatureMeasured(data.isAverageTemperatureMeasured())
         .averageMaximumTemperature(data.getAverageMaximumTemperature())
         .averageMaximumTemperatureMeasured(data.isAverageMaximumTemperatureMeasured())
         .averageMinimumTemperature(data.getAverageMinimumTemperature())
         .averageMinimumTemperatureMeasured(data.isAverageMinimumTemperatureMeasured())
         .averageHumidity(data.getAverageHumidity())
         .averageHumidityMeasured(data.isAverageHumidityMeasured())
         .averageWindSpeed(data.getAverageWindSpeed())
         .averageWindSpeedMeasured(data.isAverageWindSpeedMeasured())
         .averageCloudiness(data.getAverageCloudiness())
         .averageCloudinessMeasured(data.isAverageCloudinessMeasured())
         .maximumSnowHeight(data.getMaximumSnowHeight())
         .maximumSnowHeightMeasured(data.isMaximumSnowHeightMeasured())
         .daysWithSnowSurface(data.getDaysWithSnowSurface())
         .daysWithRain(data.getDaysWithRain());
   }

   private Optional<MonthlyRainfallStationData> findMonthlyRainfallStationData(City city, int year, int month) {
      for (CityStation cityRainfallStation : city.getCityRainfallStations()) {
         MonthlyRainfallStationData stationData = rainfallStationDataRepository.findByStationIdAndYearAndMonth(cityRainfallStation.getStationId(), String.valueOf(year), DateEntryUtils.getMonthAsString(month));
         if (stationData != null) {
            return Optional.of(stationData);
         }
      }
      return Optional.empty();
   }

   private Optional<MonthlySynopticalStationData> findMonthlySynopticalStationData(City city, int year, int month) {
      for (CityStation citySynopticalStation : city.getCitySynopticStations()) {
         MonthlySynopticalStationData stationData = synopticalStationDataRepository.findByStationIdAndYearAndMonth(citySynopticalStation.getStationId(), String.valueOf(year), DateEntryUtils.getMonthAsString(month));
         if (stationData != null) {
            return Optional.of(stationData);
         }
      }
      return Optional.empty();
   }

   private Optional<MonthlyMeteorologicalStationData> findMonthlyMeteorologicalStationData(City city, int year, int month) {
      for (CityStation cityMeteorologicalStation : city.getCityMeteorologicalStations()) {
         MonthlyMeteorologicalStationData stationData = meteorologicalStationDataRepository.findByStationIdAndYearAndMonth(cityMeteorologicalStation.getStationId(), String.valueOf(year), DateEntryUtils.getMonthAsString(month));
         if (stationData != null) {
            return Optional.of(stationData);
         }
      }
      return Optional.empty();
   }

   private List<Station> getAllStationsForCity(City city) {
      List<Station> stations = new ArrayList<>();
      List<Station> rainfallStations = city.getCityRainfallStations()
         .stream()
         .map(cityStation -> stationRepository.findByStationId(cityStation.getStationId()))
         .collect(Collectors.toList());
      List<Station> meteorologicalStations = city.getCityMeteorologicalStations()
         .stream()
         .map(cityStation -> stationRepository.findByStationId(cityStation.getStationId()))
         .collect(Collectors.toList());
      List<Station> synopticalStations = city.getCitySynopticStations()
         .stream()
         .map(cityStation -> stationRepository.findByStationId(cityStation.getStationId()))
         .collect(Collectors.toList());

      stations.addAll(rainfallStations);
      stations.addAll(meteorologicalStations);
      stations.addAll(synopticalStations);

      return stations;
   }
}
