package com.homescout.web;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.Property;
import com.homescout.util.EmiCalculator;
import com.homescout.util.Money;

/** The answer shown by the loan (EMI) calculator on the detail page. */
public class EmiResult {
    private final String loanAmount;
    private final String monthlyEmi;
    private final String totalPayable;
    private final String totalInterest;
    private final int months;

    private EmiResult(String loanAmount, String monthlyEmi, String totalPayable, String totalInterest, int months) {
        this.loanAmount = loanAmount;
        this.monthlyEmi = monthlyEmi;
        this.totalPayable = totalPayable;
        this.totalInterest = totalInterest;
        this.months = months;
    }

    public static EmiResult calculate(Property p, double ratePercent, int years, int downPercent) {
        if (downPercent < 0 || downPercent > 90) {
            throw new InvalidInputException("Down payment must be between 0% and 90%");
        }
        if (years < 1 || years > 40) {
            throw new InvalidInputException("Years must be between 1 and 40");
        }
        if (ratePercent < 0 || ratePercent > 30) {
            throw new InvalidInputException("Interest rate must be between 0% and 30%");
        }
        double loan = p.getPrice() * (100 - downPercent) / 100.0;
        double emi = EmiCalculator.emi(loan, ratePercent, years);
        double total = emi * years * 12;
        return new EmiResult(Money.format(loan), Money.format(emi), Money.format(total),
                Money.format(total - loan), years * 12);
    }

    public String getLoanAmount() { return loanAmount; }
    public String getMonthlyEmi() { return monthlyEmi; }
    public String getTotalPayable() { return totalPayable; }
    public String getTotalInterest() { return totalInterest; }
    public int getMonths() { return months; }
}
