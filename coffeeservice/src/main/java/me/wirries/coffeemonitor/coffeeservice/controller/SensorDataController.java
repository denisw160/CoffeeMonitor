package me.wirries.coffeemonitor.coffeeservice.controller;

import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the REST controller for the data of the coffee sensor.
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
@RestController
@RequestMapping("/api")
public class SensorDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDataController.class);

    private SensorDataRepository repository;

    /**
     * Constructor with AutoWiring the dependencies.
     *
     * @param repository Repository for the coffee sensor data
     */
    @Autowired
    public SensorDataController(SensorDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Return all sensor data from the coffee sensor.
     *
     * @return List with all data.
     */
    @GetMapping("/data")
    public List<SensorData> getData() {
        LOGGER.info("Get all coffee sensor data ...");
        return new ArrayList<>(repository.findAll());
    }

    /**
     * Get latest data.
     *
     * @return SensorData
     */
    @GetMapping("/data/latest")
    public SensorData getDataLatest() {
        LOGGER.info("Get latest coffee sensor data");
        return repository.findTopByOrderByTimestampDesc();
    }

    /**
     * Get data by id.
     *
     * @param id Id of the data entry
     * @return SensorData
     */
    @GetMapping("/data/{id}")
    public SensorData getDataById(@PathVariable("id") String id) {
        LOGGER.info("Get coffee sensor data for {}", id);
        return repository.findById(id).orElse(null);
    }

}
