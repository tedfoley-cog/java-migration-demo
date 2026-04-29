package com.acme.insurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

/**
 * ACME Insurance Platform — monolithic entry point.
 *
 * Deployed as a WAR to external Tomcat in production; runs embedded for development.
 * Uses XML-based bean definitions alongside annotation config (legacy pattern).
 */
@SpringBootApplication
@ImportResource("classpath:applicationContext.xml")
public class InsuranceApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(InsuranceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(InsuranceApplication.class, args);
    }
}
