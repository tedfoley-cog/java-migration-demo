package com.acme.insurance.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application-wide configuration values, externalized to application.properties.
 *
 * Static accessors are kept for backward compatibility — the values are injected
 * at startup and written to static fields via init methods.
 */
@Component
public final class Constants {

    public static double PREMIUM_TAX_RATE = 0.035;
    public static double LATE_PAYMENT_PENALTY_RATE = 0.10;
    public static int GRACE_PERIOD_DAYS = 30;
    public static String POLICY_NUMBER_PREFIX = "POL";
    public static String CLAIM_NUMBER_PREFIX = "CLM";
    public static final int MAX_CLAIM_DESCRIPTION_LENGTH = 2000;
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static double HIGH_VALUE_CLAIM_THRESHOLD = 50000.00;

    @Value("${insurance.premium-tax-rate:0.035}")
    public void setPremiumTaxRate(double rate) {
        PREMIUM_TAX_RATE = rate;
    }

    @Value("${insurance.late-payment-penalty-rate:0.10}")
    public void setLatePaymentPenaltyRate(double rate) {
        LATE_PAYMENT_PENALTY_RATE = rate;
    }

    @Value("${insurance.grace-period-days:30}")
    public void setGracePeriodDays(int days) {
        GRACE_PERIOD_DAYS = days;
    }

    @Value("${insurance.policy-number-prefix:POL}")
    public void setPolicyNumberPrefix(String prefix) {
        POLICY_NUMBER_PREFIX = prefix;
    }

    @Value("${insurance.claim-number-prefix:CLM}")
    public void setClaimNumberPrefix(String prefix) {
        CLAIM_NUMBER_PREFIX = prefix;
    }

    @Value("${insurance.high-value-claim-threshold:50000.00}")
    public void setHighValueClaimThreshold(double threshold) {
        HIGH_VALUE_CLAIM_THRESHOLD = threshold;
    }
}
