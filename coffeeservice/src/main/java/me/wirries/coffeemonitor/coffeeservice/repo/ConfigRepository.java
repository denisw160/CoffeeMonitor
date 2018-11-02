package me.wirries.coffeemonitor.coffeeservice.repo;

import me.wirries.coffeemonitor.coffeeservice.model.Config;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This repository handles the configuration between the application and the database.
 */
public interface ConfigRepository extends MongoRepository<Config, String> {

    /**
     * Find the last document from the database.
     *
     * @return last Config for the system
     */
    Config findTopByOrderByTimestampDesc();

    // add more custom query with the repository syntax

}
