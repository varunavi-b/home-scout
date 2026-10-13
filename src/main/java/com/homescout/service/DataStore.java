package com.homescout.service;

import com.homescout.exception.DataFileException;
import com.homescout.exception.InvalidInputException;
import com.homescout.exception.PropertyNotFoundException;
import com.homescout.model.Buyer;
import com.homescout.model.Enquiry;
import com.homescout.model.Property;
import com.homescout.util.FileHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Knows WHERE and HOW the data is saved.
 *   properties.dat : every listing, saved as Java objects (serialization, via your FileHandler)
 *   extras.txt     : favourites and enquiries, one short line each, e.g.  FAV|riya@mail.com|1001
 */
public class DataStore {
    private final Path propertiesFile;
    private final Path extrasFile;

    public DataStore(Path folder) {
        this.propertiesFile = folder.resolve("properties.dat");
        this.extrasFile = folder.resolve("extras.txt");
    }

    public boolean hasSavedProperties() {
        return Files.exists(propertiesFile);
    }

    public List<Property> loadProperties() throws DataFileException {
        return FileHandler.loadBinary(propertiesFile);
    }

    public void saveProperties(List<Property> properties) throws DataFileException {
        FileHandler.saveBinary(propertiesFile, properties);
    }

    public void saveExtras(UserService users, EnquiryService enquiries) throws DataFileException {
        List<String> lines = new ArrayList<>();
        for (Buyer buyer : users.getAllOfType(Buyer.class)) {
            for (Property p : buyer.getFavourites()) {
                lines.add("FAV|" + buyer.getEmail() + "|" + p.getId());
            }
        }
        for (Enquiry e : enquiries.all()) {
            // the message is URL-encoded so that "|" or line breaks inside it cannot break the file format
            lines.add("ENQ|" + e.getBuyer().getEmail() + "|" + e.getProperty().getId() + "|"
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
        try {
            Path parent = extrasFile.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            Files.write(extrasFile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataFileException("Could not save " + extrasFile, e);
        }
    }

    /** Puts favourites and enquiries back. Lines that no longer make sense are skipped. */
    public void loadExtras(UserService users, EnquiryService enquiries, Catalog<Property> catalog)
            throws DataFileException {
        if (!Files.exists(extrasFile)) return;
        List<String> lines;
        try {
            lines = Files.readAllLines(extrasFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataFileException("Could not read " + extrasFile, e);
        }
        for (String line : lines) {
            String[] parts = line.split("\\|", 4);
            try {
                if (parts[0].equals("FAV") && parts.length == 3) {
                    Buyer buyer = (Buyer) users.login(parts[1]);
                    buyer.addFavourite(catalog.findById(Integer.parseInt(parts[2])));
                } else if (parts[0].equals("ENQ") && parts.length == 4) {
                    Buyer buyer = (Buyer) users.login(parts[1]);
                    Property property = catalog.findById(Integer.parseInt(parts[2]));
                    enquiries.send(buyer, property, URLDecoder.decode(parts[3], StandardCharsets.UTF_8));
                }
            } catch (PropertyNotFoundException | InvalidInputException
                     | NumberFormatException | ClassCastException ex) {
                // deleted property, unknown user, damaged line: just skip it
            }
        }
    }
}