package com.homescout.service;

import com.homescout.exception.InvalidInputException;
import com.homescout.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

/**
 * Checks passwords. Only a SHA-256 "fingerprint" of each password is stored, never the password itself.
 * (A real website would use BCrypt, but this keeps the project simple.)
 */
public class AuthService {
    private final UserService users;
    private final Map<String, String> passwordHashes = new HashMap<>();

    public AuthService(UserService users) {
        this.users = users;
    }

    public void setPassword(String email, String password) {
        passwordHashes.put(email.trim().toLowerCase(), hash(password));
    }

    public User login(String email, String password) {
        String key = (email == null) ? "" : email.trim().toLowerCase();
        String stored = passwordHashes.get(key);
        if (stored == null || password == null || !stored.equals(hash(password))) {
            throw new InvalidInputException("Wrong email or password");    // same message for both: reveals nothing
        }
        return users.login(key);
    }

    private static String hash(String text) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha.digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
