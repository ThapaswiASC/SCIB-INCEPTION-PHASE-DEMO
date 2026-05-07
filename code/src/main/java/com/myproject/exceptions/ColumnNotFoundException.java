package com.myproject.exceptions;

public class ColumnNotFoundException extends RuntimeException {
    public ColumnNotFoundException(String columnId) {
        super("Column not found with ID: " + columnId);
    }
}
