package pl.pogodane.generators.utils;

import pl.pogodane.mongo.Station;

import java.util.List;

public class StationUtils {
   public static int getYearOfOldestEntry(List<Station> stations) {
      return stations
         .stream()
         .map(Station::getFirstYear)
         .min(Integer::compareTo)
         .orElseThrow(() -> new IllegalStateException("No stations for city provided"));
   }
}
