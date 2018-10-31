package me.wirries.coffeemonitor.coffeeservice.controller;

import me.wirries.coffeemonitor.coffeeservice.CoffeeServiceRepositoryTests;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link SensorDataController}.
 */
public class SensorDataControllerTest extends CoffeeServiceRepositoryTests {

    @Autowired
    private SensorDataController controller;

    @Test
    public void getData() {
        List<SensorData> data = controller.getData();
        assertNotNull(data);
        assertEquals(100, data.size());
    }

    @Test
    public void getDataLatest() {
        SensorData data = controller.getDataLatest();
        assertNotNull(data);
        assertEquals(1.1, data.getWeight(), 0.0);
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

}