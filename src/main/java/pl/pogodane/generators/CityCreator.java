package pl.pogodane.generators;

import org.springframework.stereotype.Component;
import pl.pogodane.generators.model.RawLocation;
import pl.pogodane.generators.model.google.Bounds;
import pl.pogodane.generators.model.google.Point;
import pl.pogodane.mongo.City;
import pl.pogodane.mongo.CityStation;
import pl.pogodane.mongo.Station;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CityCreator {
   private static final int NUMBER_OF_STATIONS_PER_CITY = 3;
   private static final double BASE_RANK = 5000;

   public City createCityStationsForCity(RawLocation city, Bounds cityBounds, Point cityLocation, List<Station> stations) {
      List<CityStation> cityStations = stations
         .stream()
         .filter(station -> station.getLat() != null && station.getLng() != null)
         .map(station -> map(station, cityBounds, cityLocation, stations))
         .sorted(Comparator.comparing(CityStation::getRank).reversed())
         .limit(NUMBER_OF_STATIONS_PER_CITY)
         .collect(Collectors.toList());

      return City.builder()
         .name(city.getName())
         .cityStations(cityStations)
         .lat(cityLocation.getLat())
         .lng(cityLocation.getLng())
         .northEastLat(cityBounds == null ? null : cityBounds.getNortheast().getLat())
         .northEastLng(cityBounds == null ? null : cityBounds.getNortheast().getLng())
         .southWestLat(cityBounds == null ? null : cityBounds.getSouthwest().getLat())
         .southWestLng(cityBounds == null ? null : cityBounds.getSouthwest().getLng())
         .voivodeship(city.getVoivodeship())
         .technicalId(city.getEscapedName() + "-" + city.getEscapedVoivodeship())
         .build();
   }

   private CityStation map(Station station, Bounds cityBounds, Point cityLocation, List<Station> allStations) {
      double distanceBetweenInKilometers = distance(cityLocation.getLat().doubleValue(), cityLocation.getLng().doubleValue(),
         station.getLat().doubleValue(), station.getLng().doubleValue(), 'K');
      boolean inBounds = isInBounds(station.getLat(), station.getLng(), cityBounds);
      double rank = calculateRank(distanceBetweenInKilometers, inBounds);

      List<String> stationIds = allStations
         .stream()
         .filter(s -> s.getStationName().equals(station.getStationName()))
         .map(Station::getStationId)
         .collect(Collectors.toList());

      CityStation cityStation = new CityStation();
      cityStation.setStationIds(stationIds);
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

      if (bounds == null) {
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
