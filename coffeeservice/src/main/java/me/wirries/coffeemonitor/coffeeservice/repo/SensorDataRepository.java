package me.wirries.coffeemonitor.coffeeservice.repo;

import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * This repository handles the sensor data between the application and the database.
 */
public interface SensorDataRepository extends MongoRepository<SensorData, String> {

    /**
     * Find the last document from the database.
     *
     * @return last SensorData from the coffee machine
     */
    SensorData findTopByOrderByTimestampDesc();

    /**
     * Find all data after the given timestamp.
     *
     * @param timestamp Timestamp for query
     * @return List of data after the timestamp
     */
    @Query("{ 'timestamp' : { $gt: ?0} }")
    List<SensorData> findAfterTimestamp(@Param("timestamp") Date timestamp);

    // add more custom query with the repository syntax

}
