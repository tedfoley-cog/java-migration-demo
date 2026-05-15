package com.acme.insurance.service;

import com.acme.insurance.model.Policy;
import com.acme.insurance.util.Constants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Calculates insurance premiums, pro-rata refunds, and late-payment penalties.
 *
 * BUG: Off-by-one error in pro-rata refund calculation — uses day-of-year
 * which does not account for the actual policy term length. For policies that span
 * a year boundary, the remaining-days calculation is wrong, producing incorrect
 * refund amounts. The correct approach would compute elapsed days between two dates.
 */
@Service
public class PremiumCalculator {

    public BigDecimal calculateAnnualPremium(BigDecimal basePremium) {
        var tax = basePremium.multiply(BigDecimal.valueOf(Constants.PREMIUM_TAX_RATE));
        return basePremium.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateProRataRefund(Policy policy, LocalDate cancellationDate) {
        // BUG: Off-by-one — uses day-of-year instead of actual elapsed days
        int startDayOfYear = policy.getEffectiveDate().getDayOfYear();
        int cancelDayOfYear = cancellationDate.getDayOfYear();

        // This is wrong for cross-year policies: day-of-year resets at Jan 1
        int elapsedDays = cancelDayOfYear - startDayOfYear;
        int totalDays = 365;

        int remainingDays = totalDays - elapsedDays;
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
