package com.acme.insurance.service;

import com.acme.insurance.model.Policy;
import com.acme.insurance.util.Constants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Calculates insurance premiums, pro-rata refunds, and late-payment penalties.
 */
@Service
public class PremiumCalculator {

    public BigDecimal calculateAnnualPremium(BigDecimal basePremium) {
        var tax = basePremium.multiply(BigDecimal.valueOf(Constants.PREMIUM_TAX_RATE));
        return basePremium.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateProRataRefund(Policy policy, LocalDate cancellationDate) {
        long elapsedDays = ChronoUnit.DAYS.between(policy.getEffectiveDate(), cancellationDate);
        long totalDays = ChronoUnit.DAYS.between(policy.getEffectiveDate(), policy.getExpirationDate());

        if (totalDays <= 0) {
            return BigDecimal.ZERO;
        }

        long remainingDays = totalDays - elapsedDays;
        if (remainingDays < 0) {
            remainingDays = 0;
        }

        var dailyRate = policy.getAnnualPremium()
                .divide(BigDecimal.valueOf(totalDays), 6, RoundingMode.HALF_UP);

        return dailyRate.multiply(BigDecimal.valueOf(remainingDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateLatePenalty(BigDecimal premiumDue, int daysLate) {
        if (daysLate <= Constants.GRACE_PERIOD_DAYS) {
            return BigDecimal.ZERO;
        }
        return premiumDue.multiply(BigDecimal.valueOf(Constants.LATE_PAYMENT_PENALTY_RATE))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMonthlyInstallment(BigDecimal annualPremium) {
        return annualPremium.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }
}
