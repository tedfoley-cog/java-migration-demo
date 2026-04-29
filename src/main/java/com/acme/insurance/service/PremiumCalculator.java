package com.acme.insurance.service;

import com.acme.insurance.model.Policy;
import com.acme.insurance.util.Constants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * Calculates insurance premiums, pro-rata refunds, and late-payment penalties.
 *
 * BUG: Off-by-one error in pro-rata refund calculation — uses Calendar.DAY_OF_YEAR
 * which does not account for the actual policy term length. For policies that span
 * a year boundary, the remaining-days calculation is wrong, producing incorrect
 * refund amounts. The correct approach would compute elapsed days between two dates.
 */
@Service
public class PremiumCalculator {

    public BigDecimal calculateAnnualPremium(BigDecimal basePremium) {
        BigDecimal tax = basePremium.multiply(BigDecimal.valueOf(Constants.PREMIUM_TAX_RATE));
        return basePremium.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateProRataRefund(Policy policy, Date cancellationDate) {
        // BUG: Off-by-one — uses day-of-year instead of actual elapsed days
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(policy.getEffectiveDate());

        Calendar cancelCal = Calendar.getInstance();
        cancelCal.setTime(cancellationDate);

        // This is wrong for cross-year policies: DAY_OF_YEAR resets at Jan 1
        int elapsedDays = cancelCal.get(Calendar.DAY_OF_YEAR) - startCal.get(Calendar.DAY_OF_YEAR);
        int totalDays = 365;

        int remainingDays = totalDays - elapsedDays;
        if (remainingDays < 0) {
            remainingDays = 0;
        }

        BigDecimal dailyRate = policy.getAnnualPremium()
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
