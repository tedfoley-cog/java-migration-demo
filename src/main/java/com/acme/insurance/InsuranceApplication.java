package com.acme.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ACME Insurance Platform — application entry point.
 *
 * Runs as an executable JAR with embedded Tomcat.
 * XML configuration and WAR packaging have been removed in the migration
 * to Spring Boot 3.x — all beans are now auto-configured or declared
 * in @Configuration classes.
 */
@SpringBootApplication
public class InsuranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsuranceApplication.class, args);
    }
}
