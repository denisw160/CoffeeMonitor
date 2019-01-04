package me.wirries.coffeemonitor.coffeeservice.config;

import me.wirries.coffeemonitor.coffeeservice.handler.SensorMessageHandler;
import me.wirries.coffeemonitor.coffeeservice.repo.SensorDataRepository;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

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

    @Value("${app.mqtt.url}")
    private String mqttUrl;
    @Value("${app.mqtt.topic}")
    private String mqttTopic;

    @Value("${app.mqtt.user}")
    private String mqttUser;
    @Value("${app.mqtt.password}")
    private String mqttPassword;

    @Value("${app.mqtt.trustStore}")
    private String mqttTrustStore;
    @Value("${app.mqtt.trustStorePassword}")
    private String mqttTrustStorePassword;

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
                mqttTopic, mqttUrl, getClientId());

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(getClientId(), mqttClientFactory(), mqttTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(coffeeSensorInputChannel());
        return adapter;
    }

    /**
     * MqttPahoClientFactory method establishes the URL of the server along with the host and
     * port, the username, and the password for connecting to the determined broker.
     * SSL connection to the broker is possible with the correct trustStore provided.
     * <p>
     * Howto generate a trustStore:
     * - Import your root CAs and intermediate CAs, for example with Let'sEncrypt
     * keytool -import -trustcacerts -alias DSTRootCAX3 -file DSTRootCAX3.crt -keystore trustStore.jks
     * keytool -import -trustcacerts -alias LetsEncryptAuthorityX3  -file LetsEncryptAuthorityX3.crt -keystore trustStore.jks
     * - or update your system trust store
     *
     * @return factory with given variables
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttUrl});

        if (StringUtils.isNotBlank(mqttTrustStore)) {
            LOGGER.info("Setup the trustStore");
            Properties sslClientProps = new Properties();
            sslClientProps.setProperty("com.ibm.ssl.trustStore", mqttTrustStore);
            sslClientProps.setProperty("com.ibm.ssl.trustStorePassword", mqttTrustStorePassword);
            //sslClientProps.setProperty("com.ibm.ssl.keyStore", "...");
            //sslClientProps.setProperty("com.ibm.ssl.keyStorePassword", "...");
            options.setSSLProperties(sslClientProps);
        }

        if (StringUtils.isNotBlank(mqttUser) && StringUtils.isNotBlank(mqttPassword)) {
            LOGGER.info("Setup login for MQTT server");
            options.setUserName(mqttUser);
            options.setPassword(mqttPassword.toCharArray());
        }

        factory.setConnectionOptions(options);
        return factory;
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

    /**
     * Return the hostname of the system.
     *
     * @return hostname
     */
    private String getClientId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("Unable to get local hostname");
            return "UNKNOWN";
        }
    }

}
