package com.acme.insurance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Additional Java-based configuration.
 *
 * Most beans are still defined in applicationContext.xml — this class exists
 * because the team partially migrated from XML to annotations years ago but
 * never finished the effort.
 */
@Configuration
public class AppConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
