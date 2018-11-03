package me.wirries.coffeemonitor.coffeeservice.config;

import me.wirries.coffeemonitor.coffeeservice.handler.SensorMessageHandler;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * This is the mqtt configuration of the application. Other settings are in the application.yml.
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
@Configuration
public class MqttConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfiguration.class);

    private static final String MQTT_URL = "tcp://localhost:1883";
    private static final String MQTT_TOPIC = "me/wirries/coffeesensor";
    private static final String CLIENT_ID = "coffeeservice";

    /**
     * The import channel for mqtt.
     *
     * @return a new {@link DirectChannel}
     */
    @Bean
    public MessageChannel coffeeSensorInputChannel() {
        return new DirectChannel();
    }

    /**
     * Create the message producer and subscripts on the topic "me/wirries/coffeesensor".
     *
     * @return a new {@link MessageProducer} for the CoffeeSensor MQTT Topic
     */
    @Bean
    public MessageProducer coffeeSensorProducer() {
        LOGGER.info("Registering MQTT message producer for queue {} on server {} with client id {}",
                MQTT_TOPIC, MQTT_URL, CLIENT_ID);

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(MQTT_URL, CLIENT_ID, MQTT_TOPIC);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(coffeeSensorInputChannel());
        return adapter;
    }

    /**
     * This is the handler for the incoming mqtt message from the coffee sensor.
     *
     * @return the handler for the sensor data
     */
    @Bean
    @Autowired
    @ServiceActivator(inputChannel = "coffeeSensorInputChannel")
    public MessageHandler coffeeSensorHandler(SensorDataRepository repository) {
        return new SensorMessageHandler(repository);
    }

}
