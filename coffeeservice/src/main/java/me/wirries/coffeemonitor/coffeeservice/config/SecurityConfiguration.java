package me.wirries.coffeemonitor.coffeeservice.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * This is the security configuration of the application. The restriction for basic authentication is only enabled,
 * if app.web.username and ..password are set. Other settings are in the application.yml.
 *
 * @author denisw
 * @version 1.0
 * @since 06.01.2019
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Value("${app.web.user}")
    private String username;
    @Value("${app.web.password}")
    private String password;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (isSecurityRequired()) {
            LOGGER.info("Enabling security for all resources");
            http.authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic().realmName("CoffeeService");
        } else {
            LOGGER.info("No user/password configured - disable security for all resources");
            http.authorizeRequests()
                    .anyRequest().permitAll();
        }
    }

    @Bean
    @Override
    @SuppressWarnings("deprecation")
    protected UserDetailsService userDetailsService() {
        if (isSecurityRequired()) {
            LOGGER.info("Enable login for user {}", this.username);
            UserDetails user =
                    User.withDefaultPasswordEncoder()
                            .username(this.username)
                            .password(this.password)
                            .roles("USER")
                            .build();

            return new InMemoryUserDetailsManager(user);
        } else {
            return new InMemoryUserDetailsManager();
        }
    }

    private boolean isSecurityRequired() {
        return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
    }

}
