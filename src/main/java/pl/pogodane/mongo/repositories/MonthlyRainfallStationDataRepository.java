package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.MonthlyRainfallStationData;

public interface MonthlyRainfallStationDataRepository extends MongoRepository<MonthlyRainfallStationData, String> {
   MonthlyRainfallStationData findByStationIdAndYearAndMonth(String stationId, String year, String month);
}
