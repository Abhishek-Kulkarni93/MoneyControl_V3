package com.example.bitcashier.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.User;

public class PreferencesHelper {

    private User authenticatedUser;
    private String authUserCurrencySymbol;

    public PreferencesHelper(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(
                ctx.getPackageName() + "_preferences",
                0);

        this.setAuthenticatedUser(
                new User(
                        prefs.getString("authusername", null),
                        prefs.getString("authuserfullname", null),
                        prefs.getString("authusercurrencycode", "EUR"))
        );

        this.setAuthUserCurrencySymbol(
                Currency.getCurrencySymbol(
                        this.authenticatedUser.getCurrency()
                )
        );
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getAuthUserCurrencySymbol() {
        return authUserCurrencySymbol;
    }

    public void setAuthUserCurrencySymbol(String authUserCurrencySymbol) {
        this.authUserCurrencySymbol = authUserCurrencySymbol;
    }
}
