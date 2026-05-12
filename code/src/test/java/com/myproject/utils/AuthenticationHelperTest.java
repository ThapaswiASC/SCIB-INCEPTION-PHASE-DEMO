package com.myproject.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationHelperTest {

    private AuthenticationHelper authenticationHelper;

    @BeforeEach
    void setUp() {
        authenticationHelper = new AuthenticationHelper();
    }

    @Test
    void getCurrentUserId_ReturnsMockUserId() {
        // Act
        String userId = authenticationHelper.getCurrentUserId();

        // Assert
        assertNotNull(userId);
        assertEquals("user123", userId);
    }

    @Test
    void getCurrentUserId_ConsistentAcrossMultipleCalls() {
        // Act
        String userId1 = authenticationHelper.getCurrentUserId();
        String userId2 = authenticationHelper.getCurrentUserId();

        // Assert
        assertEquals(userId1, userId2);
    }

    @Test
    void hasAccessToUser_SameUserId_ReturnsTrue() {
        // Arrange
        String currentUserId = authenticationHelper.getCurrentUserId();

        // Act
        boolean hasAccess = authenticationHelper.hasAccessToUser(currentUserId);

        // Assert
        assertTrue(hasAccess);
    }

    @Test
    void hasAccessToUser_DifferentUserId_ReturnsFalse() {
        // Act
        boolean hasAccess = authenticationHelper.hasAccessToUser("differentUser");

        // Assert
        assertFalse(hasAccess);
    }

    @Test
    void hasAccessToUser_NullUserId_ReturnsFalse() {
        // Act
        boolean hasAccess = authenticationHelper.hasAccessToUser(null);

        // Assert
        assertFalse(hasAccess);
    }
}