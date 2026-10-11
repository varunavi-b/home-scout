package com.homescout.model;

public enum FurnishingStatus {
    FURNISHED("Furnished"),
    SEMI_FURNISHED("Semi-furnished"),
    UNFURNISHED("Unfurnished");

    private final String label;

    FurnishingStatus(String label) { this.label = label; }

    public String getLabel() { return label; }

    /** Accepts "furnished", "semi", "unfurnished"; blank means unfurnished. */
    public static FurnishingStatus fromText(String text) {
        if (text == null || text.isBlank()) return UNFURNISHED;
        String t = text.trim().toLowerCase();
        if (t.startsWith("semi")) return SEMI_FURNISHED;
        if (t.startsWith("un")) return UNFURNISHED;
        if (t.startsWith("f")) return FURNISHED;
        return UNFURNISHED;
    }
}
