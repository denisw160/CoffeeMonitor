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
 * <p>
 * Supported JSON structures:
 * <p>
 * - {"timestamp": "2019-01-04T10:54:49.517000", "allocated": true, "weight": 1.0406128215270019}
 * - {"allocated": true, "weight": 1.0406128215270019}
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
public class SensorMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMessageHandler.class);

    private static final String PATTERN_LONG = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String PATTERN_SHORT = "yyyy-MM-dd'T'HH:mm:ss";

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
                String ts = getTimestamp(obj);
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

    /**
     * Get the timestamp from JSON data or generate by the system.
     *
     * @param obj JSON object
     * @return timestamp
     */
    private String getTimestamp(JsonNode obj) {
        if (obj.has("timestamp")) {
            return StringUtils.substring(obj.get("timestamp").asText(), 0, 23);
        } else {
            // Format "2019-01-04T10:54:49.517"
            SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_LONG);
            return sdf.format(new Date());
        }
    }

    /**
     * Parse given timestamp (String) to Date.
     *
     * @param ts Timestamp
     * @return Date
     * @throws ParseException Exception during parsing
     */
    private Date parseTimestamp(String ts) throws ParseException {
        String pattern = PATTERN_LONG;
        if (StringUtils.length(ts) == 19) {
            pattern = PATTERN_SHORT;
        }
        return new SimpleDateFormat(pattern).parse(ts);
    }

}
