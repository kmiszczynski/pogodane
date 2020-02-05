package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.YearlyCityData;

public interface YearlyCityDataRepository extends MongoRepository<YearlyCityData, String> {
   YearlyCityData findByCityTechnicalIdAndYear(String cityTechnicalId, String year);
}
