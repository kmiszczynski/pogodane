package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.DailySynopticalStationData;

public interface DailySynopticalStationDataRepository extends MongoRepository<DailySynopticalStationData, String> {
}
