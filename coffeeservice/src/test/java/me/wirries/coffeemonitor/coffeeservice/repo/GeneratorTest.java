package me.wirries.coffeemonitor.coffeeservice.repo;

import me.wirries.coffeemonitor.coffeeservice.CoffeeServiceRepositoryTests;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Date;

/**
 * This test class generates data for testing.
 */
public class GeneratorTest extends CoffeeServiceRepositoryTests {

    @Test
    @Ignore
    public void generateTestData() {
        getTemplate().dropCollection(SensorData.class);

        Date now = new Date();
        for (int i = 1; i <= 500; i++) {
            SensorData data = new SensorData();
            data.setId(ObjectId.get().toString());
            data.setAllocated(!(Math.random() < 0.5));
            Date timestamp = new Date(now.getTime() - (i * 2880000));
            data.setTimestamp(timestamp);
            data.setWeight(i * 1.1);
            getTemplate().save(data);
        }
    }

    @Test
    @Ignore
    public void generateTestData2() {
        int interval = 15;
        double changeNotAllocated = 0.3;
        double minWeight = 0.3;
        double maxWeight = 2.8;

        getTemplate().dropCollection(SensorData.class);

        Date timestamp = new Date();
        SecureRandom random = new SecureRandom();
        for (int i = 1; i <= 172800; i++) {
            SensorData data = new SensorData();
            data.setId(ObjectId.get().toString());
            data.setAllocated(!(random.nextDouble() < changeNotAllocated));
            timestamp = DateUtils.addSeconds(timestamp, interval * -1);
            data.setTimestamp(timestamp);
            data.setWeight(minWeight + (maxWeight - minWeight) * random.nextDouble());
            getTemplate().save(data);
        }
    }

}
