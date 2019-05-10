package pl.pogodane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pogodane.generators.*;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class PogodaneAppApplication implements CommandLineRunner {

   @Autowired
   private BasicDataGenerator basicDataGenerator;
   @Autowired
   private StationsForCityGenerator stationsForCityGenerator;
   @Autowired
   private DailyRainfallStationDataGenerator dailyRainfallStationDataGenerator;
   @Autowired
   private DailyMeteorologicalStationDataGenerator dailyMeteorologicalStationDataGenerator;
   @Autowired
   private DailySynopticalStationDataGenerator dailySynopticalStationDataGenerator;
   @Autowired
   private MonthlyRainfallStationDataGenerator monthlyRainfallStationDataGenerator;
   @Autowired
   private MonthlyMeteorologicalStationDataGenerator monthlyMeteorologicalStationDataGenerator;
   @Autowired
   private MonthlySynopticalStationDataGenerator monthlySynopticalStationDataGenerator;

   private static final String LOAD_BASIC_DATA_PARAM = "--loadBasicData";
   private static final String GENERATE_CITY_STATIONS_PARAM = "--generateCityStations";
   private static final String GENERATE_DAILY_RAINFALL_STATION_DATA = "--generateDailyRainfallStationData";
   private static final String GENERATE_DAILY_METEOROLOGICAL_STATION_DATA = "--generateDailyMeteorologicalStationData";
   private static final String GENERATE_DAILY_SYNOPTICAL_STATION_DATA = "--generateDailySynopticalStationData";
   private static final String GENERATE_MONTHLY_RAINFALL_STATION_DATA = "--generateMonthlyRainfallStationData";
   private static final String GENERATE_MONTHLY_METEOROLOGICAL_STATION_DATA = "--generateMonthlyMeteorologicalStationData";
   private static final String GENERATE_MONTHLY_SYNOPTICAL_STATION_DATA = "--generateMonthlySynopticalStationData";

   public static void main(String[] args) {
      SpringApplication.run(PogodaneAppApplication.class, args);
   }

   @Override public void run(String... args) throws Exception {

      if (args.length > 0) {
         List<String> parameters = Arrays.asList(args);
         if (parameters.contains(LOAD_BASIC_DATA_PARAM)) {
            basicDataGenerator.generateBasicData();
         }
         if (parameters.contains(GENERATE_CITY_STATIONS_PARAM)) {
            stationsForCityGenerator.supplyCitiesWithStations();
         }
         if (parameters.contains(GENERATE_DAILY_RAINFALL_STATION_DATA)) {
            dailyRainfallStationDataGenerator.generateData();
         }
         if (parameters.contains(GENERATE_DAILY_METEOROLOGICAL_STATION_DATA)) {
            dailyMeteorologicalStationDataGenerator.generateData();
         }
         if (parameters.contains(GENERATE_DAILY_SYNOPTICAL_STATION_DATA)) {
            dailySynopticalStationDataGenerator.generateData();
         }
         if (parameters.contains(GENERATE_MONTHLY_RAINFALL_STATION_DATA)) {
            monthlyRainfallStationDataGenerator.generateData();
         }
         if (parameters.contains(GENERATE_MONTHLY_METEOROLOGICAL_STATION_DATA)) {
            monthlyMeteorologicalStationDataGenerator.generateData();
         }
         if (parameters.contains(GENERATE_MONTHLY_SYNOPTICAL_STATION_DATA)) {
            monthlySynopticalStationDataGenerator.generateData();
         }
      }
   }
}
