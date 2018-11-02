package me.wirries.coffeemonitor.coffeeservice.handler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.wirries.coffeemonitor.coffeeservice.model.SensorData;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is handler converts the message from mqtt to {@link SensorData} and stores in the database.
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
public class SensorMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMessageHandler.class);

    private SensorDataRepository repository;

    /**
     * Constructor for setup the handler.
     *
     * @param repository Repository for storage the data
     */
    public SensorMessageHandler(SensorDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        LOGGER.debug("Receiving message: {}", message.getPayload());

        String msg = message.getPayload().toString();
        if (StringUtils.isNotBlank(msg)) {
            try {
                // Parse payload to json
                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getFactory();
                JsonParser parser = factory.createParser(msg);
                JsonNode obj = mapper.readTree(parser);

                // Create new object from payload
                SensorData data = new SensorData();
                data.setId(ObjectId.get().toString());
                String ts = StringUtils.substring(obj.get("timestamp").asText(), 0, 23);
                Date timestamp = parseTimestamp(ts);
                data.setTimestamp(timestamp);
                data.setAllocated(obj.get("allocated").asBoolean(false));
                data.setWeight(obj.get("weight").asDouble(0.0));
                repository.save(data);

            } catch (Exception e) {
                LOGGER.error("Error while converting/storing message: " + msg, e);
            }
        }
    }

    private Date parseTimestamp(String ts) throws ParseException {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        if (StringUtils.length(ts) == 19) {
            pattern = "yyyy-MM-dd'T'HH:mm:ss";
        }
        return new SimpleDateFormat(pattern).parse(ts);
    }

}
