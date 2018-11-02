package me.wirries.coffeemonitor.coffeeservice;

import me.wirries.coffeemonitor.coffeeservice.model.Config;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;

/**
 * Abstract test class for testing with the database. Test drop the data and create test data.
 */
public abstract class CoffeeServiceRepositoryTests extends CoffeeServiceApplicationTests {

    @Autowired
    private MongoTemplate template;

    @Autowired
    private SensorDataRepository repository;

    @Before
    public void setUp() {
        // SensorData
        template.dropCollection(SensorData.class);

        Date now = new Date();
        for (int i = 1; i <= 100; i++) {
            SensorData data = new SensorData();
            data.setId(ObjectId.get().toString());
            data.setAllocated(false);
            data.setTimestamp(new Date(now.getTime() - (i * 1000) - 25000));
            data.setWeight(i * 1.1);
            template.save(data);
        }

        // Config
        template.dropCollection(Config.class);
        Config config = new Config();
        config.setId(ObjectId.get().toString());
        config.setTimestamp(new Date(now.getTime()));
        config.setMaxWeight(100 * 1.1);
        template.save(config);
    }

    public MongoTemplate getTemplate() {
        return template;
    }

    public SensorDataRepository getRepository() {
        return repository;
    }

}