package com.example.bitcashier.models;

public class User {

    public static final String USER_TABLE = "users";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME ="USER_NAME";
    public static final String FULL_NAME ="FULL_NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String CURRENCY = "CURRENCY";

    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " ("+ USER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ USER_NAME +" TEXT NOT NULL UNIQUE,"+ FULL_NAME +" TEXT NOT NULL,"+ PASSWORD +" TEXT NOT NULL,"+ CURRENCY +" TEXT NOT NULL) ";
    public static final String USER_COUNT_QUERY = "SELECT COUNT(*) FROM " + USER_TABLE;
    public static final String CHECK_USER_EXISTS_QUERY = USER_COUNT_QUERY + " WHERE " + USER_NAME + "= ? COLLATE NOCASE";
    public static final String GET_USER_QUERY = "SELECT "+ USER_NAME +","+ FULL_NAME +","+ PASSWORD +","+ CURRENCY +" FROM " + USER_TABLE + " WHERE " + USER_NAME + "= ? COLLATE NOCASE";

    private int user_id;
    private String user_name;
    private String full_name;
    private String password;
    private String currency;

    public User() {
        // Empty constructor..
    }

    public User(String user_name, String full_name) {
        this.user_name = user_name;
        this.full_name = full_name;
    }

    public User(String user_name, String full_name, String currency) {
        this.user_name = user_name;
        this.full_name = full_name;
        this.currency = currency;
    }

    public User(String user_name, String full_name, String password, String currency) {
        this.user_name = user_name;
        this.full_name = full_name;
        this.password = password;
        this.currency = currency;
    }

    public int getUserId() {
        return user_id;
    }
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() { return  user_name;}
    public void setUsername(String user_name){ this.user_name = user_name; }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
