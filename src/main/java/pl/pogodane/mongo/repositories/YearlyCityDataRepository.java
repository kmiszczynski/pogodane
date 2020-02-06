package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.YearlyCityData;

import java.util.List;

public interface YearlyCityDataRepository extends MongoRepository<YearlyCityData, String> {
   YearlyCityData findByCityTechnicalIdAndYear(String cityTechnicalId, String year);

   List<YearlyCityData> findAllByCityTechnicalId(String cityTechnicalId);
}
