package me.wirries.coffeemonitor.coffeeservice.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

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
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${app.web.user}")
    private String username;
    @Value("${app.web.password}")
    private String password;
    @Value("${app.web.logSessions}")
    private boolean logSessions;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (isSecurityRequired()) {
            LOGGER.info("Enabling security for all resources");
            http.authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic().realmName("CoffeeService")
                    .and()
                    .csrf().disable();
            http
                    .sessionManagement()
                    .maximumSessions(50)
                    .sessionRegistry(sessionRegistry());
        } else {
            LOGGER.info("No user/password configured - disable security for all resources");
            http.authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .csrf().disable();
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

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Scheduled(fixedRate = 10000)
    public void reportCurrentTime() {
        if (!logSessions && !isSecurityRequired()) return;

        List<Object> allPrincipals = sessionRegistry().getAllPrincipals();
        if (allPrincipals.isEmpty()) {
            LOGGER.info("No logged in users found");
            return;
        }

        // Collect all (not expired) sessions in a List
        List<SessionInformation> sessions = allPrincipals.stream()
                .filter(u -> !sessionRegistry().getAllSessions(u, false).isEmpty())
                .map(u -> sessionRegistry().getAllSessions(u, false))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Log sessions to LOGGER
        LOGGER.info("Logging {} open sessions ...", sessions.size());
        LOGGER.info("SessionId \t\t\t\t\t\t\t UserId \t Last request");
        for (SessionInformation s : sessions) {
            LOGGER.info("{} \t {} \t\t {}",
                    s.getSessionId(),
                    ((User) s.getPrincipal()).getUsername(),
                    FORMAT.format(s.getLastRequest()));
        }
    }

    private boolean isSecurityRequired() {
        return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
    }

}
