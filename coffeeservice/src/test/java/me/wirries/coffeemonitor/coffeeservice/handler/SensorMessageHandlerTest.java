package me.wirries.coffeemonitor.coffeeservice.handler;

import me.wirries.coffeemonitor.coffeeservice.CoffeeServiceApplicationTests;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import static org.junit.Assert.*;

/**
 * Tests for {@link SensorMessageHandler}.
 */
public class SensorMessageHandlerTest extends CoffeeServiceApplicationTests {

    private SensorMessageHandler handler;
    private SensorData saved;

    @Before
    public void setUp() throws Exception {
        SensorDataRepository mock = Mockito.mock(SensorDataRepository.class);
        Mockito.when(mock.save(Mockito.any(SensorData.class))).then(new Answer<SensorData>() {
            @Override
            public SensorData answer(InvocationOnMock invocation) throws Throwable {
                SensorData sensorData = invocation.getArgument(0);
                saved = sensorData;
                return sensorData;
            }
        });
        handler = new SensorMessageHandler(mock);
        saved = null;
    }

    @Test
    public void handleMessage() {
        assertNull(saved);
        handler.handleMessage(new Message<Object>() {
            @Override
            public Object getPayload() {
                return "{\"timestamp\": \"2018-10-31T20:20:32.541000\", \"allocated\": true, \"weight\": 0.8961355408188962}";
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        });

        assertNotNull(saved);
        assertEquals(saved.getAllocated(), true);
        assertEquals(saved.getWeight(), 0.8961355408188962, 0.0);
        assertNotNull(saved.toString());
    }

    @Test
    public void handleMessageError() {
        assertNull(saved);
        handler.handleMessage(new Message<Object>() {
            @Override
            public Object getPayload() {
                return "{\"timestamp\": \"2018-10-31T20:\", \"allocated\": true, \"weight\": 0.8961355408188962}";
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        });

        assertNull(saved);
    }

}