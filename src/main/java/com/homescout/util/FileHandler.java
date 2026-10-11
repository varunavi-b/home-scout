package com.homescout.util;

import com.homescout.exception.DataFileException;
import com.homescout.model.Property;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** PERSISTENCE: object serialization for saving/loading, CSV for a human-readable export. */
public final class FileHandler {

    private FileHandler() { }

    /** Saves all properties to one binary file (try-with-resources closes the stream for us). */
    public static void saveBinary(Path file, List<Property> properties) throws DataFileException {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(new ArrayList<>(properties));
            }
        } catch (IOException e) {
            throw new DataFileException("Could not save listings to " + file, e);
        }
    }

    /** Loads properties; a missing file simply means "no saved data yet". Shows try-catch-FINALLY. */
    @SuppressWarnings("unchecked")
    public static List<Property> loadBinary(Path file) throws DataFileException {
        if (!Files.exists(file)) return new ArrayList<>();
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(Files.newInputStream(file));
            List<Property> loaded = (List<Property>) in.readObject();
            int highestId = loaded.stream().mapToInt(Property::getId).max().orElse(0);
            Property.syncCounter(highestId);              // new ids continue after the loaded ones
            return loaded;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new DataFileException("Could not read listings from " + file, e);
        } finally {
            if (in != null) {                             // finally ALWAYS runs
                try {
                    in.close();
                } catch (IOException ignored) {
                    // nothing useful to do if close fails
                }
            }
        }
    }

    /** Writes a CSV report that can be opened in Excel. */
    public static void exportCsv(Path file, List<Property> properties) throws DataFileException {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(file, StandardCharsets.UTF_8))) {
                w.println("id,type,title,city,price,area_sqft,status,furnishing,estimated_tax,amenities");
                for (Property p : properties) {
                    w.println(String.join(",",
                            String.valueOf(p.getId()),
                            p.getType().getLabel(),
                            csv(p.getTitle()),
                            csv(p.getCity()),
                            String.format(Locale.US, "%.0f", p.getPrice()),
                            String.format(Locale.US, "%.0f", p.getArea()),
                            p.getStatus().getLabel(),
                            p.getFurnishing().getLabel(),
                            String.format(Locale.US, "%.0f", p.calculateTax()),
                            csv(String.join("; ", p.getAmenities()))));
                }
            }
        } catch (IOException e) {
            throw new DataFileException("Could not export CSV to " + file, e);
        }
    }

    private static String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
