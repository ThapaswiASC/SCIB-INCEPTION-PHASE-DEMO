package com.myproject.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class ValidationUtils {

    public static boolean isValidUTF8(String input) {
        if (input == null) {
            return true;
        }
        try {
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            String decoded = new String(bytes, StandardCharsets.UTF_8);
            return input.equals(decoded);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBlankOrWhitespace(String input) {
        return input == null || StringUtils.isBlank(input) || input.trim().isEmpty();
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }
}
