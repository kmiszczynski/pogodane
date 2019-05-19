package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.MonthlyMeteorologicalStationData;

public interface MonthlyMeteorologicalStationDataRepository extends MongoRepository<MonthlyMeteorologicalStationData, String> {
   MonthlyMeteorologicalStationData findByStationIdAndYearAndMonth(String stationId, String year, String month);
}
