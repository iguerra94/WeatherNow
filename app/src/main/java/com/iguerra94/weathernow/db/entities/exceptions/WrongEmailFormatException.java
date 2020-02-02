package com.iguerra94.weathernow.db.entities.exceptions;

public class WrongEmailFormatException extends Exception {
    public WrongEmailFormatException(String message) {
        super(message);
    }
}