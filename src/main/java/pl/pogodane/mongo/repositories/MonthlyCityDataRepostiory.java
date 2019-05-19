package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.MonthlyCityData;

public interface MonthlyCityDataRepostiory extends MongoRepository<MonthlyCityData, String> {
}
