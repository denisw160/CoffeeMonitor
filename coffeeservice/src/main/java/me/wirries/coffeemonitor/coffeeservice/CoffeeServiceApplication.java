package me.wirries.coffeemonitor.coffeeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the main class of the application and starts the service.
 *
 * @author denisw
 * @version 1.0
 * @since 31.10.2018
 */
@SpringBootApplication
public class CoffeeServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeServiceApplication.class);

    public static void main(String[] args) {
        LOGGER.info("Starting the CoffeeService ...");
        SpringApplication.run(CoffeeServiceApplication.class, args);
    }

}
