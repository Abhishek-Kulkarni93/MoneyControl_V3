package com.example.bitcashier.models;

public class Expense {

    public static final String EXPENSE_TABLE = "expenses";
    public static final String ID = "ID";
    public static final String AMOUNT = "AMOUNT";
    public static final String TITLE = "TITLE";
    public static final String DATE = "DATE";
    public static final String NOTES = "NOTES";
    public static final String CATEGORY = "CATEGORY";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String PAYMENT_TYPE = "PAYMENT_TYPE";
    public static final String RECURRING = "RECURRING";
    public static final String USER_NAME = "USER_NAME";

    public static final String CREATE_EXPENSE_TABLE = "CREATE TABLE IF NOT EXISTS " + EXPENSE_TABLE + " ("+ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ AMOUNT +" DOUBLE NOT NULL,"+ TITLE +" TEXT NOT NULL,"+ DATE +" TEXT NOT NULL,"+ CATEGORY +" TEXT NOT NULL,"+ CONTACT_NAME +" TEXT,"+ PAYMENT_TYPE +" TEXT NOT NULL,"+ NOTES +" TEXT,"+ RECURRING +" TEXT DEFAULT 'no',"+ USER_NAME +" TEXT NOT NULL) ";
    public static final String GET_USER_EXPENSES_QUERY = "SELECT * FROM "+ EXPENSE_TABLE +" WHERE "+ USER_NAME +" = ? COLLATE NOCASE";
    public static final String GET_EXPENSE_BY_ID_QUERY = "SELECT * FROM "+ EXPENSE_TABLE +" WHERE "+ ID +" = ?";
    public static final String USER_EXPENSE_SUM_QUERY = "SELECT SUM("+ AMOUNT +") FROM " + EXPENSE_TABLE +" WHERE "+ USER_NAME +" = ? COLLATE NOCASE";
    public static final String USER_EXPENSE_SUM_BY_CATEGORY_QUERY = USER_EXPENSE_SUM_QUERY + " AND "+ CATEGORY +"= ? COLLATE NOCASE";

    private int id;
    private double amount;
    private String title;
    private String date;
    private String category;
    private String contact_name;
    private String notes;
    private String payment_type;
    private String recurring;
    private String user_name;

    public Expense() {
        // Empty constructor..
    }

    public Expense(double amount, String title, String date, String category, String payment_type, String notes, String recurring, String user_name, String contact_name) {
        this.amount = amount;
        this.title = title;
        this.date = date;
        this.category = category;
        this.notes = notes;
        this.payment_type = payment_type;
        this.recurring = recurring;
        this.user_name = user_name;
        this.contact_name = contact_name;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring) {
        this.recurring = recurring;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    // Format --- 2020-05-16
    public String getDateInStoreFormat() {
        String [] dateParts = this.date.split("/");
        return dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0] + "";
    }

    // Format --- 16/05/2020
    public String getDateInDisplayFormat() {
        String [] dateParts = this.date.split("-");
        return dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0] + "";
    }

    @Override
    public String toString() {
//        return "Expense{" +
//                "id=" + id +
//                ", amount=" + amount +
//                ", title='" + title + '\'' +
//                ", date='" + date + '\'' +
//                ", category='" + category + '\'' +
//                ", notes='" + notes + '\'' +
//                ", payment_type='" + payment_type + '\'' +
//                ", recurring='" + recurring + '\'' +
//                '}';
        return "" +
                id +
                " | " + title +
                " | " + category +
                " | €" + amount +
                " | " + getDateInDisplayFormat() +
                " | " + notes +
                " | " + payment_type +
                " | " + recurring +
                " | " + user_name;
    }
}
