package me.wirries.coffeemonitor.coffeeservice.controller;

import me.wirries.coffeemonitor.coffeeservice.model.Alive;
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
import java.util.Date;
import java.util.List;

/**
 * This is the REST controller for the api of the coffee service.
 * It's provides the access to the sensor data and the configuration.
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    private SensorDataRepository repository;

    /**
     * Constructor with AutoWiring the dependencies.
     *
     * @param repository Repository for the coffee sensor data
     */
    @Autowired
    public ApiController(SensorDataRepository repository) {
        this.repository = repository;
    }

    /**
     * Return the status of the coffee sensors.
     *
     * @return Alive-Status
     */
    @GetMapping("/alive")
    public Alive getAlive() {
        LOGGER.info("Get the alive status ...");
        Alive alive = new Alive();
        alive.setTimestamp(new Date());

        SensorData latest = getDataLatest();
        if (latest == null || latest.getTimestamp() == null) {
            alive.setAlive(false);
        } else {
            Date aliveDate = new Date(System.currentTimeMillis() - (30000));
            alive.setAlive(latest.getTimestamp().getTime() >= aliveDate.getTime());
        }

        return alive;
    }


    /**
     * Return all sensor data from the coffee sensor.
     *
     * @return List with all data
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
        SensorData data = repository.findTopByOrderByTimestampDesc();
        return (data != null) ? data : new SensorData();
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

    // TODO Adding REST Services for the configuration

}
