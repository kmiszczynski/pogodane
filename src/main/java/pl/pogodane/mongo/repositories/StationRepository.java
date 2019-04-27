package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.Station;

public interface StationRepository extends MongoRepository<Station, String> {
}
