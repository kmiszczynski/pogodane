package pl.pogodane.generators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import pl.pogodane.generators.model.RawLocation;
import pl.pogodane.generators.model.Station;
import pl.pogodane.generators.model.google.GeocodingResults;
import pl.pogodane.generators.model.google.Geometry;
import pl.pogodane.mongo.City;
import pl.pogodane.mongo.repositories.CityRepository;
import pl.pogodane.mongo.repositories.StationRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BasicDataGenerator implements DataGenerator {
   private static final String API_KEY = "AIzaSyDF1CGlbncDOtXzfsRdS5tGvfDTgiZtdLw";

   private static final String CITY_TYPE = "miasto";
   private static final int THREAD_POOL = 10;

   @Autowired
   private StationRepository stationRepository;
   @Autowired
   private CityRepository cityRepository;
   @Autowired
   private CityCreator cityCreator;

   public void generateData() throws IOException, SAXException, ExecutionException, InterruptedException {
      ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL);

      List<Station> stations = fetchStationsFromFile();
      List<RawLocation> rawLocations = fetchRawLocationsFromFile();
      List<RawLocation> citiesOnly = filterCities(rawLocations);
      List<pl.pogodane.mongo.Station> stationsMapped = stations
         .stream()
         .map(this::map)
         .collect(Collectors.toList());
      stationRepository.saveAll(stationsMapped);

      List<CompletableFuture<City>> completableFutures = new ArrayList<>();
      for (RawLocation city : citiesOnly) {
         CompletableFuture<City> cityStationFuture = new CompletableFuture<>();

         completableFutures.add(cityStationFuture);
         executorService.submit(() -> handleSingleCity(city, cityStationFuture));
      }
      CompletableFuture<List<City>> combinedFutures = gatherAll(completableFutures);
      List<City> cities = combinedFutures.get();
      cityRepository.saveAll(cities);
   }

   private void handleSingleCity(RawLocation city, CompletableFuture<City> cityStationFuture) {
      try {
         RestTemplate restTemplate = new RestTemplate();
         String query = city.getEscapedName() + "," + city.getEscapedVoivodeship();
         ResponseEntity<GeocodingResults> cityResult = restTemplate.getForEntity("https://maps.googleapis.com/maps/api/geocode/json?address=" + query + "&key=" + API_KEY, GeocodingResults.class);
         Geometry geometry = cityResult.getBody().getResults().get(0).getGeometry();
         City cityForPersistance = cityCreator.createCity(city, geometry.getBounds(), geometry.getLocation());
         log.info("Completed calculating of city stations for city {}", city.getEscapedName());
         cityStationFuture.complete(cityForPersistance);
      } catch (Exception e) {
         log.error("Exception occured while handling city " + city.getEscapedName() + " - " + city.getEscapedVoivodeship(), e);
      }
   }

   private pl.pogodane.mongo.Station map(Station station) {
      return pl.pogodane.mongo.Station.builder()
         .stationId(station.getStationId())
         .stationName(station.getStationName())
         .count(Integer.valueOf(station.getCount()))
         .height(new BigDecimal(station.getHeight()))
         .firstYear(Integer.valueOf(station.getFirstYear()))
         .lastYear(Integer.valueOf(station.getLastYear()))
         .lat(station.getLat().equalsIgnoreCase("null") ? null : new BigDecimal(station.getLat().replace(",", ".")))
         .lng(station.getLng().equalsIgnoreCase("null") ? null : new BigDecimal(station.getLng().replace(",", ".")))
         .years(Integer.valueOf(station.getYears()))
         .type(station.getType())
         .build();
   }

   private List<RawLocation> filterCities(List<RawLocation> rawLocations) {
      return rawLocations
         .stream()
         .filter(rawLocation -> CITY_TYPE.equalsIgnoreCase(rawLocation.getType()))
         .collect(Collectors.toList());
   }

   private List<Station> fetchStationsFromFile() throws IOException, SAXException {
      Smooks stationsSmooks = new Smooks("smooks-stations.xml");
      ExecutionContext stationsExecutionContext = stationsSmooks.createExecutionContext();
      JavaResult stationsResult = new JavaResult();
      stationsSmooks.filterSource(stationsExecutionContext, new StringSource(readStationsFromFile()), stationsResult);
      return (List) stationsResult.getBean("stationsList");
   }

   private List<RawLocation> fetchRawLocationsFromFile() throws IOException, SAXException {
      Smooks locationsSmooks = new Smooks("smooks-locations.xml");
      ExecutionContext locationsExecutionContext = locationsSmooks.createExecutionContext();
      JavaResult locationsResult = new JavaResult();
      locationsSmooks.filterSource(locationsExecutionContext, new StringSource(readRawLocationsFromFile()), locationsResult);
      return (List) locationsResult.getBean("rawLocationsList");
   }

   private String readStationsFromFile() throws IOException {
      return IOUtils.toString(BasicDataGenerator.class.getClassLoader().getResourceAsStream("input/stations.csv"),
         "UTF-8");
   }

   private String readRawLocationsFromFile() throws IOException {
      return IOUtils.toString(BasicDataGenerator.class.getClassLoader().getResourceAsStream("input/rawLocations.csv"),
         "UTF-8");
   }

   public static <T> CompletableFuture<List<T>> gatherAll(List<CompletableFuture<T>> futures) {
      CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);

      return CompletableFuture.allOf(cfs)
         .thenApply(ignored -> futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList())
         );
   }
}
