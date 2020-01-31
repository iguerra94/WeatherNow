package com.iguerra94.weathernow.utils;

import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.views.exceptions.EmptyFieldsException;

public class UserUtils {

    public static void checkEmptyFieldsAtRegister(User user) throws EmptyFieldsException {
        if (user.getFirstName().isEmpty() ||
            user.getLastName().isEmpty() ||
            user.getEmail().isEmpty() ||
            user.getPassword().isEmpty()
        ) {
            throw new EmptyFieldsException("Debe completar todos los campos obligatorios.");
        }
    }
}
