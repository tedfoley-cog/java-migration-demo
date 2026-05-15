package com.acme.insurance.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date utility class using java.time API.
 */
public final class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    private DateUtils() {
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public static LocalDate addMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    public static LocalDate addYears(LocalDate date, int years) {
        return date.plusYears(years);
    }

    public static int daysBetween(LocalDate start, LocalDate end) {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }

    public static boolean isExpired(LocalDate expirationDate) {
        return expirationDate.isBefore(LocalDate.now());
    }

    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format: " + dateStr, e);
        }
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.toLocalDate().format(DATE_FORMATTER);
    }

    public static int getYear(LocalDate date) {
        return date.getYear();
    }

    public static int getMonth(LocalDate date) {
        return date.getMonthValue();
    }
}
