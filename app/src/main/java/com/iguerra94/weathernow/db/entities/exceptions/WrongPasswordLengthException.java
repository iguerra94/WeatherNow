package com.iguerra94.weathernow.db.entities.exceptions;

public class WrongPasswordLengthException extends Exception {
    public WrongPasswordLengthException(String message) {
        super(message);
    }
}