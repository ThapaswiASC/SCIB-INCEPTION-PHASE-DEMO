package com.myproject.utils;

public class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isWhitespaceOnly(String value) {
        return value != null && value.trim().isEmpty();
    }

    public static boolean containsSpecialCharacters(String value) {
        if (value == null) {
            return false;
        }
        // Allow alphanumeric, spaces, and common punctuation
        return !value.matches("^[a-zA-Z0-9\\s.,!?'-]*$");
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
}
