package com.example.bitcashier.models;

public class Income {
    public static final String INCOME_TABLE = "income";
    public static final String ID = "ID";
    public static final String AMOUNT = "AMOUNT";
    public static final String DATE = "DATE";
    public static final String NOTES = "NOTES";
    public static final String USER_NAME = "USER_NAME";

    public static final String CREATE_INCOME_TABLE = "CREATE TABLE IF NOT EXISTS " + INCOME_TABLE + " ("+ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ AMOUNT +" REAL NOT NULL,"+ DATE +" TEXT NOT NULL,"+ NOTES +" TEXT,"+ USER_NAME +" TEXT NOT NULL) ";
    public static final String GET_USER_INCOME_QUERY = "SELECT * FROM " + INCOME_TABLE+" WHERE "+ USER_NAME +" = ? COLLATE NOCASE";
    public static final String USER_INCOME_SUM_QUERY = "SELECT SUM("+ AMOUNT +") FROM " + INCOME_TABLE+" WHERE "+ USER_NAME +" = ? COLLATE NOCASE";

    private int id;
    private double amount;
    private String date;
    private String notes;
    private String user_name;

    public Income() {
        // Empty constructor..
    }

    public Income(double amount, String date, String notes, String user_name) {
        this.amount = amount;
        this.date = date;
        this.notes = notes;
        this.user_name = user_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    // Format --- 16/05/2020
    public String getDateInDisplayFormat() {
        String [] dateParts = this.date.split("-");
        return dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0] + "";
    }

    @Override
    public String toString() {
//        return "Income{" +
//                "id=" + id +
//                ", amount=" + amount +
//                ", date='" + date + '\'' +
//                ", notes='" + notes + '\'' +
//                ", user_name='" + user_name + '\'' +
//                '}';
        return "" +
                id +
                " | €" + amount +
                " | " + getDateInDisplayFormat() +
                " | " + notes +
                " | " + user_name;
    }
}
