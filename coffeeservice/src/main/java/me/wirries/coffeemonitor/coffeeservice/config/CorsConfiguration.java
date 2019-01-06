package me.wirries.coffeemonitor.coffeeservice.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This is the CORS configuration of the application. Other settings are in the application.yml.
 *
 * @author denisw
 * @version 1.0
 * @since 01.11.2018
 */
@Configuration
public class CorsConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsConfiguration.class);

    @Value("${app.web.cors.allowedOrigins}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if (StringUtils.isNotBlank(allowedOrigins)) {
                    LOGGER.info("Enable CORS - allowed origins only from: {}", allowedOrigins);
                    registry.addMapping("/api/*").allowedOrigins(allowedOrigins);
                } else {
                    registry.addMapping("/api/*");
                }
            }
        };
    }

}
