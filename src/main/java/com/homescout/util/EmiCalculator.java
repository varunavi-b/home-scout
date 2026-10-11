package com.homescout.util;

import com.homescout.exception.InvalidInputException;

public final class EmiCalculator {
    private static final double DEFAULT_RATE = 8.5;     // % per year
    private static final int DEFAULT_YEARS = 20;

    private EmiCalculator() { }

    /** Monthly EMI = P * r * (1+r)^n / ((1+r)^n - 1) */
    public static double emi(double principal, double annualRatePercent, int years) {
        if (principal <= 0 || annualRatePercent < 0 || years <= 0) {
            throw new InvalidInputException("Principal, rate and years must be valid positive values");
        }
        int months = years * 12;
        double r = annualRatePercent / 12 / 100;
        if (r == 0) return principal / months;
        double growth = Math.pow(1 + r, months);
        return principal * r * growth / (growth - 1);
    }

    /** OVERLOADED: uses a default rate and tenure. */
    public static double emi(double principal) {
        return emi(principal, DEFAULT_RATE, DEFAULT_YEARS);
    }
}
