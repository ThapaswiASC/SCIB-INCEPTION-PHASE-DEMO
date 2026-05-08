package com.myproject.exceptions;

public class ColumnNotFoundException extends RuntimeException {

    public ColumnNotFoundException(String columnId) {
        super(String.format("Column with ID %s not found", columnId));
    }
}