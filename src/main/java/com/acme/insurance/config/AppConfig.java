package com.acme.insurance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Application configuration.
 *
 * Replaces the legacy applicationContext.xml — all bean definitions are now
 * Java-based. Counter initialization is handled by @PostConstruct on
 * PolicyNumberGenerator, which runs during bean creation before the web
 * server starts accepting requests.
 */
@Configuration
public class AppConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
