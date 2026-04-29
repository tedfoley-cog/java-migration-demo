package com.acme.insurance.util;

/**
 * Application-wide constants.
 *
 * FIXME: hardcoded config values — these should be externalized to application.properties
 *        or a configuration service. Tax rate and policy prefix especially change per state.
 */
public final class Constants {

    private Constants() {
    }

    public static final double PREMIUM_TAX_RATE = 0.035;

    public static final double LATE_PAYMENT_PENALTY_RATE = 0.10;

    public static final int GRACE_PERIOD_DAYS = 30;

    public static final String POLICY_NUMBER_PREFIX = "POL";

    public static final String CLAIM_NUMBER_PREFIX = "CLM";

    public static final int MAX_CLAIM_DESCRIPTION_LENGTH = 2000;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final double HIGH_VALUE_CLAIM_THRESHOLD = 50000.00;
}
