package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.City;

public interface CityRepository extends MongoRepository<City, String> {
}
