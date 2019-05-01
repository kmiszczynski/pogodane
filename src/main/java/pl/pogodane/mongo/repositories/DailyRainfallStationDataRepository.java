package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.DailyRainfallStationData;

public interface DailyRainfallStationDataRepository extends MongoRepository<DailyRainfallStationData, String> {
}
