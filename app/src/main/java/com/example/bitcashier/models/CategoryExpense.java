package com.example.bitcashier.models;

public class CategoryExpense {

    public static final String CATEGORY_NAME = "CATEGORY";
    public static final String YEAR = "YEAR";
    public static final String AMOUNT = "AMOUNT";

    public static final String CATEGORY_EXPENSE_BY_MONTH_QUERY = "SELECT "+ Expense.CATEGORY +", SUM("+ Expense.AMOUNT +") as "+ AMOUNT +" FROM "+ Expense.EXPENSE_TABLE +" WHERE "+ Expense.USER_NAME +" = ? COLLATE NOCASE AND strftime('%m', date) = ? AND strftime('%Y', date) = ? GROUP by "+ Expense.CATEGORY +";";
    public static final String CATEGORY_EXPENSE_BY_YEAR_QUERY = "SELECT strftime('%Y', date) as "+ YEAR +", SUM("+ Expense.AMOUNT +") as "+ AMOUNT +" FROM "+ Expense.EXPENSE_TABLE +" WHERE "+ Expense.USER_NAME +" = ? COLLATE NOCASE GROUP by strftime('%Y', date);";

    private double amount;
    private String category_name;
    private int year;

    public CategoryExpense(double amount, String category_name) {
        this.amount = amount;
        this.category_name = category_name;
    }

    public CategoryExpense(int year, double amount) {
        this.amount = amount;
        this.year = year;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
