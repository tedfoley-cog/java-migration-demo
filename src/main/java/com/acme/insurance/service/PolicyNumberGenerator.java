package com.acme.insurance.service;

import com.acme.insurance.util.Constants;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates sequential policy and claim numbers using thread-safe atomic counters.
 */
@Service
public class PolicyNumberGenerator {

    private final JdbcTemplate jdbcTemplate;
    private final Constants constants;

    private final AtomicLong policyCounter = new AtomicLong(1000);
    private final AtomicLong claimCounter = new AtomicLong(5000);

    public PolicyNumberGenerator(JdbcTemplate jdbcTemplate, Constants constants) {
        this.jdbcTemplate = jdbcTemplate;
        this.constants = constants;
    }

    public String nextPolicyNumber() {
        long current = policyCounter.getAndIncrement();
        return Constants.POLICY_NUMBER_PREFIX + "-" + String.format("%06d", current);
    }

    public String nextClaimNumber() {
        long current = claimCounter.getAndIncrement();
        return Constants.CLAIM_NUMBER_PREFIX + "-" + String.format("%06d", current);
    }

    @PostConstruct
    public void initializeCounters() {
        int policyOffset = Constants.POLICY_NUMBER_PREFIX.length() + 2;
        int claimOffset = Constants.CLAIM_NUMBER_PREFIX.length() + 2;

        try {
            var maxPolicy = jdbcTemplate.queryForObject(
                    "SELECT MAX(CAST(SUBSTRING(policy_number, " + policyOffset + ") AS BIGINT)) FROM policies",
                    Long.class);
            if (maxPolicy != null) {
                policyCounter.set(maxPolicy + 1);
            }
        } catch (Exception e) {
            // table might not exist yet on first run
        }

        try {
            var maxClaim = jdbcTemplate.queryForObject(
                    "SELECT MAX(CAST(SUBSTRING(claim_number, " + claimOffset + ") AS BIGINT)) FROM claims",
                    Long.class);
            if (maxClaim != null) {
                claimCounter.set(maxClaim + 1);
            }
        } catch (Exception e) {
            // table might not exist yet on first run
        }
    }
}
