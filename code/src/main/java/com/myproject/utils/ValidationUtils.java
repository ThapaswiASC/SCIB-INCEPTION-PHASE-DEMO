package com.myproject.utils;

import com.myproject.exceptions.InvalidInputException;

public class ValidationUtils {

    private ValidationUtils() {
        // Utility class
    }

    public static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidInputException("Title cannot be empty or contain only whitespace");
        }
        if (title.length() > 255) {
            throw new InvalidInputException("Title cannot exceed 255 characters");
        }
    }

    public static void validateDescription(String description) {
        if (description != null && description.length() > 10000) {
            throw new InvalidInputException("Description cannot exceed 10000 characters");
        }
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
}