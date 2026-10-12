package com.homescout.web;

/** Makes sure "go back to ..." addresses can only point inside our own website. */
public final class Redirects {

    private Redirects() { }

    public static String safe(String next, String fallback) {
        boolean ok = next != null
                && next.startsWith("/")
                && !next.startsWith("//")
                && !next.contains("\\")
                && !next.contains("\r")
                && !next.contains("\n");
        return ok ? next : fallback;
    }
}
