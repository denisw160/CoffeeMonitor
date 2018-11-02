package me.wirries.coffeemonitor.coffeeservice.controller;

import me.wirries.coffeemonitor.coffeeservice.model.*;
import me.wirries.coffeemonitor.coffeeservice.repo.ConfigRepository;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.Calendar.*;
import static org.apache.commons.lang3.time.DateUtils.*;

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

    private SensorDataRepository dataRepository;
    private ConfigRepository configRepository;

    /**
     * Constructor with AutoWiring the dependencies.
     *
     * @param dataRepository Repository for the coffee sensor data
     */
    @Autowired
    public ApiController(SensorDataRepository dataRepository,
                         ConfigRepository configRepository) {
        this.dataRepository = dataRepository;
        this.configRepository = configRepository;
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
        return new ArrayList<>(dataRepository.findAll());
    }

    /**
     * Get latest data.
     *
     * @return SensorData
     */
    @GetMapping("/data/latest")
    public SensorData getDataLatest() {
        LOGGER.info("Get latest coffee sensor data");
        SensorData data = dataRepository.findTopByOrderByTimestampDesc();
        return (data != null) ? data : new SensorData();
    }

    /**
     * Get latest data data for the last 7 days.
     * The data will be aggregated and shown on a hourly basis. Only the last hour will shown on an minute basis.
     *
     * @return SensorData
     */
    @GetMapping("/data/7days")
    public List<SensorData> getData7Days() {
        LOGGER.info("Get sensor data for the last 7 days");
        Date timestamp = addDays(truncate(new Date(), DATE), -6);
        return aggregateSensorData(dataRepository.findAfterTimestamp(timestamp));
    }

    // optimize and cleanup code - reduce code duplication
    private List<SensorData> aggregateSensorData(List<SensorData> data) {
        List<SensorData> list = new ArrayList<>();

        Date now = new Date();

        SensorData n = null;
        final List<Double> weightList = new ArrayList<>();
        final List<Boolean> allocatedList = new ArrayList<>();
        for (SensorData s : data) {
            // Pre-condition: first element
            if (n == null) {
                n = new SensorData(truncate(s.getTimestamp(), HOUR));
                list.add(n);

                weightList.clear();
                allocatedList.clear();
            }

            if ((now.getTime() - s.getTimestamp().getTime()) <= (60 * 60 * 1000)) {
                // Last 60 minutes
                if (isSameMinute(n.getTimestamp(), s.getTimestamp())) {
                    // Collecting the values of the same hour
                    weightList.add(s.getWeight());
                    allocatedList.add(s.getAllocated());

                } else {
                    // Switch in the minute - update values and start with next minute
                    OptionalDouble average = weightList.stream().mapToDouble(a -> a).average();
                    n.setWeight((average.isPresent()) ? average.getAsDouble() : 0.0);

                    boolean containsFalse = allocatedList.stream().anyMatch(t -> !t);
                    n.setAllocated(!containsFalse);

                    // New element
                    n = new SensorData(truncate(s.getTimestamp(), MINUTE));
                    list.add(n);

                    weightList.clear();
                    weightList.add(s.getWeight());

                    allocatedList.clear();
                    allocatedList.add(s.getAllocated());
                }


            } else {
                // Older data
                if (isSameHour(n.getTimestamp(), s.getTimestamp())) {
                    // Collecting the values of the same hour
                    weightList.add(s.getWeight());
                    allocatedList.add(s.getAllocated());

                } else {
                    // Switch in the hour - update values and start with next hour
                    OptionalDouble average = weightList.stream().mapToDouble(a -> a).average();
                    n.setWeight((average.isPresent()) ? average.getAsDouble() : 0.0);

                    boolean containsFalse = allocatedList.stream().anyMatch(t -> !t);
                    n.setAllocated(!containsFalse);

                    // New element
                    n = new SensorData(truncate(s.getTimestamp(), HOUR));
                    list.add(n);

                    weightList.clear();
                    weightList.add(s.getWeight());

                    allocatedList.clear();
                    allocatedList.add(s.getAllocated());
                }
            }
        }

        // handle the last element
        if (!weightList.isEmpty()) {
            OptionalDouble average = weightList.stream().mapToDouble(a -> a).average();
            n.setWeight((average.isPresent()) ? average.getAsDouble() : 0.0);

            boolean containsFalse = allocatedList.stream().anyMatch(t -> !t);
            n.setAllocated(!containsFalse);
        }

        return list;
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
        return dataRepository.findById(id).orElse(null);
    }

    /**
     * Return all consumption data from the coffee sensor.
     *
     * @return List with all data
     */
    @GetMapping("/consumption")
    public List<Consumption> getConsumption() {
        LOGGER.info("Collecting all consumption data ...");
        return aggregateConsumption(getData());
    }

    /**
     * Get latest consumption data (from today).
     *
     * @return Consumption
     */
    @GetMapping("/consumption/latest")
    public Consumption getConsumptionLatest() {
        LOGGER.info("Get latest consumption data");
        Date timestamp = addDays(truncate(new Date(), DATE), 0);
        List<Consumption> data = aggregateConsumption(dataRepository.findAfterTimestamp(timestamp));
        return (data.isEmpty()) ? new Consumption() : data.get(0);
    }

    /**
     * Get latest consumption data for the last 7 days.
     *
     * @return Consumption
     */
    @GetMapping("/consumption/7days")
    public List<Consumption> getConsumption7Days() {
        LOGGER.info("Get consumption data for the last 7 days");
        Date timestamp = addDays(truncate(new Date(), DATE), -6);
        return aggregateConsumption(dataRepository.findAfterTimestamp(timestamp));
    }

    /**
     * Creates one consumption object per day.
     *
     * @param data List of sensor data / ordered by timestamp
     * @return List of consumption
     */
    private List<Consumption> aggregateConsumption(List<SensorData> data) {
        List<Consumption> list = new ArrayList<>();

        boolean state = false;
        Consumption c = null;
        for (SensorData s : data) {
            if (c == null || !isSameDay(c.getDay(), s.getTimestamp())) {
                c = new Consumption(truncate(s.getTimestamp(), DATE));
                list.add(c);
            }
            if (state && !s.getAllocated()) {
                c.incrementConsumption();
            }
            state = s.getAllocated();
        }

        return list;
    }

    /**
     * Get latest configuration.
     *
     * @return Config
     */
    @GetMapping("/config")
    public Config getConfig() {
        LOGGER.info("Get latest configuration");
        Config config = configRepository.findTopByOrderByTimestampDesc();
        return (config != null) ? config : new Config(2.8);
    }

    /**
     * Update the configuration.
     *
     * @param config Config
     */
    @PutMapping("/config")
    public GenericResponse<Config> setConfig(@RequestBody Config config) {
        LOGGER.info("Storing the configuration {}", config);
        if (config != null) {
            config.setId(ObjectId.get().toString());
            config.setTimestamp(new Date());
            configRepository.save(config);

            return new GenericResponse<>(200, "Save success", config);
        }

        return new GenericResponse<>(500, "No configuration posted.");
    }

    /**
     * Is the same hour?
     */
    private static boolean isSameHour(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameHour(cal1, cal2);
    }

    /**
     * Is the same hour?
     */
    private static boolean isSameHour(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR);
    }

    /**
     * Is the same minute?
     */
    private static boolean isSameMinute(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMinute(cal1, cal2);
    }

    /**
     * Is the same minute?
     */
    private static boolean isSameMinute(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR) == cal2.get(Calendar.HOUR) &&
                cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }

}
