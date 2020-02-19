package com.iguerra94.weathernow.db.entities.exceptions;

public class EmptyPasswordException extends Exception {
    public EmptyPasswordException(String message) {
        super(message);
    }
}
