package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.pogodane.generators.model.google.Bounds;
import pl.pogodane.generators.model.google.Point;
import pl.pogodane.mongo.City;
import pl.pogodane.mongo.CityStation;
import pl.pogodane.mongo.Station;
import pl.pogodane.mongo.repositories.CityRepository;
import pl.pogodane.mongo.repositories.StationRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StationsForCityGenerator {

   @Value("${station.maximumDistance}")
   private double MAXIMUM_DISTANCE_FROM_CITY;

   @Autowired
   StationRepository stationRepository;
   @Autowired
   CityRepository cityRepository;

   private static final String RAINFALL_TYPE = "o";
   private static final String SYNOPTIC_TYPE = "s";
   private static final String METEOROLOGICAL_TYPE = "k";

   private static final double BASE_RANK = 5000;

   public void supplyCitiesWithStations() {
      List<City> cities = cityRepository.findAll();
      List<Station> stations = stationRepository.findAll()
         .stream()
         .filter(station -> station.getLng() != null && station.getLat() != null)
         .collect(Collectors.toList());

      for (City city : cities) {
         Point northeast = new Point(city.getNorthEastLat(), city.getNorthEastLng());
         Point southeast = new Point(city.getSouthWestLat(), city.getSouthWestLng());
         Bounds cityBounds = new Bounds(northeast, southeast);
         Point cityLocation = new Point(city.getLat(), city.getLng());

         List<Station> stationsInRange = stations
            .stream()
            .filter(station -> notExceedingMaximumDistance(station, city))
            .collect(Collectors.toList());

         // rainfall stations
         List<CityStation> rainfallStations = stationsInRange
            .stream()
            .filter(station -> station.getType().contains(RAINFALL_TYPE))
            .map(station -> map(station, cityBounds, cityLocation))
            .sorted(Comparator.comparing(CityStation::getRank).reversed())
            .collect(Collectors.toList());

         // synoptical stations
         List<CityStation> synopticalStations = stationsInRange
            .stream()
            .filter(station -> station.getType().contains(SYNOPTIC_TYPE))
            .map(station -> map(station, cityBounds, cityLocation))
            .sorted(Comparator.comparing(CityStation::getRank).reversed())
            .collect(Collectors.toList());

         // meteorological stations
         List<CityStation> meteorologicalStations = stationsInRange
            .stream()
            .filter(station -> station.getType().contains(METEOROLOGICAL_TYPE))
            .map(station -> map(station, cityBounds, cityLocation))
            .sorted(Comparator.comparing(CityStation::getRank).reversed())
            .collect(Collectors.toList());

         city.setCityRainfallStations(rainfallStations);
         city.setCitySynopticStations(synopticalStations);
         city.setCityMeteorologicalStations(meteorologicalStations);

         log.info("City: {}, Rainfall stations: {}, Synoptical stations: {}, Meteorological stations: {}",
            city.getName(), rainfallStations.size(), synopticalStations.size(), meteorologicalStations.size());
      }

      cityRepository.saveAll(cities);
   }

   private boolean notExceedingMaximumDistance(Station station, City city) {
      double distanceBetweenInKilometers = distance(city.getLat().doubleValue(), city.getLng().doubleValue(),
         station.getLat().doubleValue(), station.getLng().doubleValue(), 'K');
      return distanceBetweenInKilometers < MAXIMUM_DISTANCE_FROM_CITY;
   }

   private CityStation map(Station station, Bounds cityBounds, Point cityLocation) {
      double distanceBetweenInKilometers = distance(cityLocation.getLat().doubleValue(), cityLocation.getLng().doubleValue(),
         station.getLat().doubleValue(), station.getLng().doubleValue(), 'K');
      boolean inBounds = isInBounds(station.getLat(), station.getLng(), cityBounds);
      double rank = calculateRank(distanceBetweenInKilometers, inBounds);

      CityStation cityStation = new CityStation();
      cityStation.setStationId(station.getStationId());
      cityStation.setDistanceFromCityInKilometers(distanceBetweenInKilometers);
      cityStation.setRank(rank);
      cityStation.setInCityBounds(inBounds);
      cityStation.setStationName(station.getStationName());

      return cityStation;
   }

   private double calculateRank(double distanceBetweenInKilometers, boolean inBounds) {
      double inBoundsBonus = inBounds ? 1000 : 0;
      return BASE_RANK + inBoundsBonus - distanceBetweenInKilometers;
   }

   private boolean isInBounds(BigDecimal lat, BigDecimal lng, Bounds bounds) {

      if (bounds.getSouthwest().getLat() == null) {
         return false;
      }

      return lat.compareTo(bounds.getSouthwest().getLat()) >= 0
         && lat.compareTo(bounds.getNortheast().getLat()) <= 0
         && lng.compareTo(bounds.getSouthwest().getLng()) >= 0
         && lng.compareTo(bounds.getNortheast().getLng()) <= 0;
   }

   private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
      double theta = lon1 - lon2;
      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
      dist = Math.acos(dist);
      dist = rad2deg(dist);
      dist = dist * 60 * 1.1515;
      if (unit == 'K') {
         dist = dist * 1.609344;
      } else if (unit == 'N') {
         dist = dist * 0.8684;
      }
      return (dist);
   }

   private double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
   }

   private double rad2deg(double rad) {
      return (rad * 180.0 / Math.PI);
   }
}
