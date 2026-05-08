package com.myproject.exceptions;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(String fromStatus, String toStatus) {
        super(String.format("Cannot transition from %s to %s", fromStatus, toStatus));
    }
}