package com.iguerra94.weathernow.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyFieldsException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongEmailFormatException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongPasswordLengthException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void checkEmailFormat(String email) throws WrongEmailFormatException {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new WrongEmailFormatException("El email ingresado no es correcto.");
        }
    }

    public static void checkPasswordLength(String password) throws WrongPasswordLengthException {
        if (password.length() < 6) {
            throw new WrongPasswordLengthException("La clave debe contener como minimo 6 (seis) caracteres alfanumericos.");
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

}
