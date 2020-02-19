package com.iguerra94.weathernow.db.entities.exceptions;

public class UserWrongPasswordException extends Exception {
    public UserWrongPasswordException(String message) {
        super(message);
    }
}