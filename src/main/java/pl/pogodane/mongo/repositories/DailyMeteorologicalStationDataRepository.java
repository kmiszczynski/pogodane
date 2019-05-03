package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.DailyMeteorologicalStationData;

public interface DailyMeteorologicalStationDataRepository extends MongoRepository<DailyMeteorologicalStationData, String> {
}
