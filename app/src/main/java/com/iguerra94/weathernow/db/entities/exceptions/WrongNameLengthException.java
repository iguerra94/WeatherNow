package com.iguerra94.weathernow.db.entities.exceptions;

public class WrongNameLengthException extends Exception {
    public WrongNameLengthException(String message) {
        super(message);
    }
}
