package pl.pogodane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pogodane.generators.BasicDataGenerator;
import pl.pogodane.generators.DailyRainfallStationDataGenerator;
import pl.pogodane.generators.StationsForCityGenerator;

@SpringBootApplication
public class PogodaneAppApplication implements CommandLineRunner {

   @Autowired
   private BasicDataGenerator basicDataGenerator;
   @Autowired
   private StationsForCityGenerator stationsForCityGenerator;
   @Autowired
   private DailyRainfallStationDataGenerator dailyRainfallStationDataGenerator;

   private static final String LOAD_BASIC_DATA_PARAM = "--loadBasicData";
   private static final String GENERATE_CITY_STATIONS_PARAM = "--generateCityStations";
   private static final String GENERATE_DAILY_RAINFALL_STATION_DATA = "--generateDailyRainfallStationData";

   public static void main(String[] args) {
      SpringApplication.run(PogodaneAppApplication.class, args);
   }

   @Override public void run(String... args) throws Exception {
      if (args.length > 0 && args[0].equals(LOAD_BASIC_DATA_PARAM)) {
         basicDataGenerator.generateBasicData();
      }
      if (args.length > 0 && args[0].equals(GENERATE_CITY_STATIONS_PARAM)) {
         stationsForCityGenerator.supplyCitiesWithStations();
      }
      if (args.length > 0 && args[0].equals(GENERATE_DAILY_RAINFALL_STATION_DATA)) {
         dailyRainfallStationDataGenerator.generateData();
      }
   }
}
