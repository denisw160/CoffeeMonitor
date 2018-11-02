package me.wirries.coffeemonitor.coffeeservice.repo;

import me.wirries.coffeemonitor.coffeeservice.CoffeeServiceRepositoryTests;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link SensorDataRepository}.
 */
public class SensorDataRepositoryTest extends CoffeeServiceRepositoryTests {

    @Autowired
    private SensorDataRepository repository;

    @Test
    public void findTopByOrderByTimestampDesc() {
        SensorData data = repository.findTopByOrderByTimestampDesc();
        assertNotNull(data);
        assertEquals(1.1, data.getWeight(), 0);
        assertNotNull(data.getTimestamp());
        assertNotNull(data.getId());
    }

    @Test
    public void findAfterTimestamp() {
        // TODO Test
    }

}