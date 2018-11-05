package me.wirries.coffeemonitor.coffeeservice.controller;

import me.wirries.coffeemonitor.coffeeservice.CoffeeServiceRepositoryTests;
import me.wirries.coffeemonitor.coffeeservice.model.Alive;
import me.wirries.coffeemonitor.coffeeservice.model.Config;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests for {@link ApiController}.
 */
public class ApiControllerTest extends CoffeeServiceRepositoryTests {

    @Autowired
    private ApiController controller;

    @Test
    public void getAlive() throws Exception {
        Alive alive = controller.getAlive();
        assertNotNull(alive.getTimestamp());
        assertTrue(alive.isAlive());

        // old data
        TimeUnit.SECONDS.sleep(5);
        alive = controller.getAlive();
        assertNotNull(alive.getTimestamp());
        assertFalse(alive.isAlive());

        // no data
        getTemplate().dropCollection(SensorData.class);
        alive = controller.getAlive();
        assertNotNull(alive.getTimestamp());
        assertFalse(alive.isAlive());
    }

    @Test
    public void getData() {
        List<SensorData> data = controller.getData();
        assertNotNull(data);
        assertEquals(100, data.size());

        // no data
        getTemplate().dropCollection(SensorData.class);
        data = controller.getData();
        assertNotNull(data);
        assertTrue(data.isEmpty());
    }

    @Test
    public void getDataLatest() {
        SensorData data = controller.getDataLatest();
        assertNotNull(data);
        assertEquals(1.1, data.getWeight(), 0.0);

        // no data
        getTemplate().dropCollection(SensorData.class);
        data = controller.getDataLatest();
        assertNotNull(data);
        assertNull(data.getId());
    }

    @Test
    public void getData7Days() {
        // TODO Test
    }

    @Test
    public void getDataById() {
        SensorData data = controller.getData().get(0);
        assertNotNull(data);

        SensorData dataId = controller.getDataById(data.getId());
        assertEquals(data.getId(), dataId.getId());
        assertEquals(data.getTimestamp(), dataId.getTimestamp());
        assertEquals(data.getWeight(), dataId.getWeight());
        assertEquals(data.getAllocated(), dataId.getAllocated());

        data = controller.getDataById("unknown");
        assertNull(data);
    }

    @Test
    public void getConfig() {
        Config config = controller.getConfig();
        assertNotNull(config);
        assertNotNull(config.getId());
        assertNotNull(config.getTimestamp());
        assertNotNull(config.getMaxWeight());

        // no data
        getTemplate().dropCollection(Config.class);
        config = controller.getConfig();
        assertNotNull(config);
        assertNull(config.getId());
    }

    @Test
    public void setConfig() {
        Config config = controller.getConfig();
        assertNotNull(config);
        String id = config.getId();
        assertNotNull(id);

        Config c = new Config();
        c.setMaxWeight(5.0);
        c.setPotWeight(2.0);
        controller.setConfig(c);

        config = controller.getConfig();
        assertNotNull(config);
        assertNotEquals(id, config.getId());
        assertEquals(5.0, config.getMaxWeight(), 0.0);

        // no data
        getTemplate().dropCollection(Config.class);
        config = controller.getConfig();
        assertNotNull(config);
        assertNull(config.getId());

        c = new Config();
        c.setMaxWeight(5.0);
        c.setPotWeight(2.0);
        controller.setConfig(c);

        config = controller.getConfig();
        assertNotNull(config);
        assertEquals(5.0, config.getMaxWeight(), 0.0);
    }

    @Test
    public void getConsumption() {
        // TODO Test
    }

    @Test
    public void getConsumptionLatest() {
        // TODO Test
    }

    @Test
    public void getConsumption7Days() {
        // TODO Test
    }

}