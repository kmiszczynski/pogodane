package pl.pogodane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.pogodane.generators.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
   @Autowired
   private MonthlyCityDataGenerator monthlyCityDataGenerator;
   @Autowired
   private YearlyCityDataGenerator yearlyCityDataGenerator;

   private static final String LOAD_BASIC_DATA_PARAM = "--loadBasicData";
   private static final String GENERATE_CITY_STATIONS_PARAM = "--generateCityStations";
   private static final String GENERATE_DAILY_RAINFALL_STATION_DATA = "--generateDailyRainfallStationData";
   private static final String GENERATE_DAILY_METEOROLOGICAL_STATION_DATA = "--generateDailyMeteorologicalStationData";
   private static final String GENERATE_DAILY_SYNOPTICAL_STATION_DATA = "--generateDailySynopticalStationData";
   private static final String GENERATE_MONTHLY_RAINFALL_STATION_DATA = "--generateMonthlyRainfallStationData";
   private static final String GENERATE_MONTHLY_METEOROLOGICAL_STATION_DATA = "--generateMonthlyMeteorologicalStationData";
   private static final String GENERATE_MONTHLY_SYNOPTICAL_STATION_DATA = "--generateMonthlySynopticalStationData";
   private static final String GENERATE_MONTHLY_CITY_DATA = "--generateMonthlyCityData";
   private static final String GENERATE_YEARLY_CITY_DATE = "--generateYearlyCityData";

   private static final Map<String, DataGenerator> dataGenerators = new LinkedHashMap<>();

   public static void main(String[] args) {
      SpringApplication.run(PogodaneAppApplication.class, args);
   }

   @Override public void run(String... args) throws Exception {

      // preserve order
      dataGenerators.put(LOAD_BASIC_DATA_PARAM, basicDataGenerator);
      dataGenerators.put(GENERATE_CITY_STATIONS_PARAM, stationsForCityGenerator);
      dataGenerators.put(GENERATE_DAILY_RAINFALL_STATION_DATA, dailyRainfallStationDataGenerator);
      dataGenerators.put(GENERATE_DAILY_METEOROLOGICAL_STATION_DATA, dailyMeteorologicalStationDataGenerator);
      dataGenerators.put(GENERATE_DAILY_SYNOPTICAL_STATION_DATA, dailySynopticalStationDataGenerator);
      dataGenerators.put(GENERATE_MONTHLY_RAINFALL_STATION_DATA, monthlyRainfallStationDataGenerator);
      dataGenerators.put(GENERATE_MONTHLY_METEOROLOGICAL_STATION_DATA, monthlyMeteorologicalStationDataGenerator);
      dataGenerators.put(GENERATE_MONTHLY_SYNOPTICAL_STATION_DATA, monthlySynopticalStationDataGenerator);
      dataGenerators.put(GENERATE_MONTHLY_CITY_DATA, monthlyCityDataGenerator);
      dataGenerators.put(GENERATE_YEARLY_CITY_DATE, yearlyCityDataGenerator);

      if (args.length > 0) {
         List<String> parameters = Arrays.asList(args);
         for (String parameter : parameters) {
            dataGenerators.get(parameter).generateData();
         }
      }
   }
}
