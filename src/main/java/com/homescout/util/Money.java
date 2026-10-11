package com.homescout.util;

import java.util.Locale;

/** Small utility class: only static members, never instantiated. */
public final class Money {

    private Money() { }   // utility class -> private constructor

    /** Formats rupees as "Rs 45.00 L" (lakh) or "Rs 2.10 Cr" (crore). */
    public static String format(double rupees) {
        if (rupees >= 10_000_000) return String.format(Locale.US, "Rs %.2f Cr", rupees / 10_000_000);
        if (rupees >= 100_000)    return String.format(Locale.US, "Rs %.2f L", rupees / 100_000);
        return String.format(Locale.US, "Rs %,.0f", rupees);
    }
}
