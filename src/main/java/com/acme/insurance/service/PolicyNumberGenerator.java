package com.acme.insurance.service;

import com.acme.insurance.util.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Generates sequential policy and claim numbers.
 *
 * BUG: Race condition — the counter is not synchronized. Under concurrent
 * requests, two policies can receive the same policy number, causing a
 * unique-constraint violation. The read-then-increment is not atomic.
 */
@Service
public class PolicyNumberGenerator {

    private final JdbcTemplate jdbcTemplate;

    private long policyCounter = 1000;
    private long claimCounter = 5000;

    public PolicyNumberGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String nextPolicyNumber() {
        // BUG: not synchronized — concurrent calls can produce duplicate numbers
        long current = policyCounter;
        policyCounter = current + 1;
        return Constants.POLICY_NUMBER_PREFIX + "-" + String.format("%06d", current);
    }

    public String nextClaimNumber() {
        // Same race condition pattern
        long current = claimCounter;
        claimCounter = current + 1;
        return Constants.CLAIM_NUMBER_PREFIX + "-" + String.format("%06d", current);
    }

    public void initializeCounters() {
        try {
            var maxPolicy = jdbcTemplate.queryForObject(
                    "SELECT MAX(CAST(SUBSTRING(policy_number, 5) AS BIGINT)) FROM policies",
                    Long.class);
            if (maxPolicy != null) {
                policyCounter = maxPolicy + 1;
            }
        } catch (Exception e) {
            // table might not exist yet on first run
        }

        try {
            var maxClaim = jdbcTemplate.queryForObject(
                    "SELECT MAX(CAST(SUBSTRING(claim_number, 5) AS BIGINT)) FROM claims",
                    Long.class);
            if (maxClaim != null) {
                claimCounter = maxClaim + 1;
            }
        } catch (Exception e) {
            // table might not exist yet on first run
        }
    }
}
