package com.homescout.util;

import com.homescout.exception.InvalidInputException;

/** Input validation helpers used by the console UI. Every failure throws InvalidInputException. */
public final class Validator {

    private Validator() { }

    public static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) throw new InvalidInputException(field + " must not be empty");
        return value.trim();
    }

    public static double parsePositiveDouble(String text, String field) {
        requireNonBlank(text, field);
        try {
            double value = Double.parseDouble(text.trim());
            if (value <= 0) throw new InvalidInputException(field + " must be greater than zero");
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(field + " must be a number, but got: " + text);
        }
    }

    public static int parseInt(String text, String field, int min, int max) {
        requireNonBlank(text, field);
        try {
            int value = Integer.parseInt(text.trim());
            if (value < min || value > max) {
                throw new InvalidInputException(field + " must be between " + min + " and " + max);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(field + " must be a whole number, but got: " + text);
        }
    }

    public static boolean yes(String text) {
        return text != null && (text.trim().equalsIgnoreCase("y") || text.trim().equalsIgnoreCase("yes"));
    }
}
