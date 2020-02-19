package com.iguerra94.weathernow.db.entities.exceptions;

public class UserEmailNotRegisteredException extends Exception {
    public UserEmailNotRegisteredException(String message) {
        super(message);
    }
}