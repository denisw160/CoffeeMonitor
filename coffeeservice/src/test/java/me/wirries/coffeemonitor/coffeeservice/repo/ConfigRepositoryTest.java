package me.wirries.coffeemonitor.coffeeservice.repo;

import me.wirries.coffeemonitor.coffeeservice.CoffeeServiceRepositoryTests;
import me.wirries.coffeemonitor.coffeeservice.model.Config;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link ConfigRepository}.
 */
public class ConfigRepositoryTest extends CoffeeServiceRepositoryTests {

    @Autowired
    private ConfigRepository repository;

    @Test
    public void findTopByOrderByTimestampDesc() {
        Config config = repository.findTopByOrderByTimestampDesc();
        assertNotNull(config);
        assertNotNull(config.getId());
        assertNotNull(config.getTimestamp());
        assertEquals(110.00000000000001, config.getMaxWeight(), 0);
    }

}