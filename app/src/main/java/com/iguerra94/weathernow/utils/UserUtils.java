package com.iguerra94.weathernow.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.iguerra94.weathernow.databinding.ActivitySignupBinding;
import com.iguerra94.weathernow.db.entities.User;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyEmailException;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyFieldsException;
import com.iguerra94.weathernow.db.entities.exceptions.EmptyPasswordException;
import com.iguerra94.weathernow.db.entities.exceptions.UserDataNotChangedException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongEmailFormatException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongNameLengthException;
import com.iguerra94.weathernow.db.entities.exceptions.WrongPasswordLengthException;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsKeys;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsManager;
import com.iguerra94.weathernow.utils.sharedPrefs.SharedPrefsValues;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {

    public static void checkEmptyFieldsAtRegister(Context context, User user, ActivitySignupBinding binding) throws EmptyFieldsException {
        String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "Debes completar este dato." : "You must complete this data.";

        if (!user.getFirstName().isEmpty() && !user.getLastName().isEmpty() && !user.getEmail().isEmpty() && !user.getPassword().isEmpty()) return;

        throw new EmptyFieldsException(message);
    }

    public static void checkEmptyEmailAtLogin(Context context, String email) throws EmptyEmailException {
        if (email.isEmpty()) {
            String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "Debes completar el email." : "You must complete the email.";

            throw new EmptyEmailException(message);
        }
    }

    public static void checkEmptyPasswordAtLogin(Context context, String password) throws EmptyPasswordException {
        if (password.isEmpty()) {
            String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "Debes completar la clave." : "You must complete the password.";

            throw new EmptyPasswordException(message);
        }
    }

    public static void checkEmailFormat(Context context, String email) throws WrongEmailFormatException {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "El email ingresado no es correcto." : "The email entered is not correct.";

            throw new WrongEmailFormatException(message);
        }
    }

    public static void checkNamesLength(Context context, User user) throws WrongNameLengthException {
        String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "Debe contener al menos 3 (seis) caracteres alfanumericos." : "Must contain at least 3 (three) alphanumeric characters.";

        if (user.getFirstName().length() >= 3 && user.getLastName().length() >= 3) return;

        throw new WrongNameLengthException(message);
    }

    public static void checkPasswordLength(Context context, String password) throws WrongPasswordLengthException {
        if (password.length() < 6) {
            String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "La clave debe contener como minimo 6 (seis) caracteres alfanumericos." : "The password must contain at least 6 (six) alphanumeric characters.";

            throw new WrongPasswordLengthException(message);
        }
    }

    public static void checkNewPasswordLength(Context context, String password) throws WrongPasswordLengthException {
        if (password.length() > 0 && password.length() < 6) {
            String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
            String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "La nueva clave debe contener como minimo 6 (seis) caracteres alfanumericos." : "The new password must contain at least 6 (six) alphanumeric characters.";

            throw new WrongPasswordLengthException(message);
        }
    }

    public static void checkIfUserDataNotChanged(Context context, User user) throws UserDataNotChangedException {
        String currentLocale = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.APP_LANGUAGE_LOCALE);
        String message = (currentLocale.equals(SharedPrefsValues.APP_LANGUAGE_LOCALE.SPANISH)) ? "Para actualizar los datos del usuario, algun dato ingresado debe ser distinto a los que estan registrados." : "To update user data, some data entered must be different from those registered.";

        String currentUserProfileImageUri = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.CURRENT_USER_PROFILE_IMAGE_URI);
        String currentUserGivenName = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.CURRENT_USER_GIVEN_NAME);
        String currentUserFamilyName = SharedPrefsManager.getInstance(context).readString(SharedPrefsKeys.CURRENT_USER_FAMILY_NAME);

        if (!user.getProfileImageUri().equals(currentUserProfileImageUri) || !user.getFirstName().equals(currentUserGivenName) || !user.getLastName().equals(currentUserFamilyName)) return;

        throw new UserDataNotChangedException(message);
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