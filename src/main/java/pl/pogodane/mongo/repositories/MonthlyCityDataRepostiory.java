package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.MonthlyCityData;

import java.util.List;

public interface MonthlyCityDataRepostiory extends MongoRepository<MonthlyCityData, String> {
   List<MonthlyCityData> findAllByCityTechnicalIdAndYear(String cityTechnicalId, String year);
}
