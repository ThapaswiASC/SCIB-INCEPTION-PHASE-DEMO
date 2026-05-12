package com.myproject.utils;

import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    /**
     * Extracts user ID from authentication context.
     * In a real implementation, this would extract from JWT token or security context.
     * For now, returns a mock user ID for demonstration.
     */
    public String getCurrentUserId() {
        // Mock implementation - in production, extract from SecurityContextHolder
        return "user123";
    }

    /**
     * Validates if the current user has access to the specified user's resources.
     */
    public boolean hasAccessToUser(String userId) {
        String currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}