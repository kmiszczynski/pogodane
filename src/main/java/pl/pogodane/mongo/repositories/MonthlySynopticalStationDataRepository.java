package pl.pogodane.mongo.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.pogodane.mongo.MonthlySynopticalStationData;

public interface MonthlySynopticalStationDataRepository extends MongoRepository<MonthlySynopticalStationData, String> {
   MonthlySynopticalStationData findByStationIdAndYearAndMonth(String stationId, String year, String month);
}
