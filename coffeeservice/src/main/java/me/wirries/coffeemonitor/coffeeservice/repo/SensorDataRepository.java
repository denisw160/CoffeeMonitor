package me.wirries.coffeemonitor.coffeeservice.repo;

import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This repository handles the objects between the application and the database.
 */
public interface SensorDataRepository extends MongoRepository<SensorData, String> {

    /**
     * Find the last document from the database.
     *
     * @return last SensorData from the coffee machine
     */
    SensorData findTopByOrderByTimestampDesc();

    // add more custom query with the repository syntax

}
