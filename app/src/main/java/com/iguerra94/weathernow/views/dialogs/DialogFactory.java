package com.iguerra94.weathernow.views.dialogs;

public class DialogFactory {

    private static DialogFactory instance;

    private DialogFactory() {}

    public static DialogFactory getInstance() {
        if (instance == null) {
            instance = new DialogFactory();
        }
        return instance;
    }

    public static IGenericDialog getRegisteringUserDialog() {
        return DialogRegisteringUser.getInstance();
    }

}