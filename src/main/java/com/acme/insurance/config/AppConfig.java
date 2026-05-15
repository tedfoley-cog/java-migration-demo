package com.acme.insurance.config;

import com.acme.insurance.service.PolicyNumberGenerator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Application configuration.
 *
 * Replaces the legacy applicationContext.xml — all bean definitions are now
 * Java-based. The policyNumberInitializer that was defined in XML is now
 * handled by an @EventListener on ApplicationReadyEvent.
 */
@Configuration
public class AppConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializePolicyCounters(ApplicationReadyEvent event) {
        var generator = event.getApplicationContext().getBean(PolicyNumberGenerator.class);
        generator.initializeCounters();
    }
}
