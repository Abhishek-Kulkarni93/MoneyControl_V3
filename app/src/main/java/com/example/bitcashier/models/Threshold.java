package com.example.bitcashier.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Threshold {

    public static final String THRESHOLD_TABLE = "threshold";
    public static final String ID = "ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    public static final String THRESHOLD_VALUE = "THRESHOLD_VALUE";

    public static final String CREATE_THRESHOLD_TABLE = "CREATE TABLE IF NOT EXISTS " + THRESHOLD_TABLE + " ("+ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ USER_NAME +" TEXT NOT NULL,"+ CATEGORY_NAME +" TEXT NOT NULL,"+ THRESHOLD_VALUE +" DOUBLE DEFAULT 0) ";
    public static final String CHECK_THRESHOLD_EXISTS_FOR_USER_QUERY = "SELECT COUNT(*) FROM " + THRESHOLD_TABLE + " WHERE " + USER_NAME + "= ? COLLATE NOCASE";
    public static final String GET_THRESHOLD_FOR_CATEGORY_QUERY = "SELECT * FROM " + THRESHOLD_TABLE + " WHERE " + USER_NAME + "= ? COLLATE NOCASE AND "+ CATEGORY_NAME +"= ?";

    private int id;
    private String user_name;
    private String category_name;
    private double threshold_value;

    public Threshold() {
        // Empty constructor
    }

    public Threshold(int id, String user_name, String category_name, double threshold_value) {
        this.id = id;
        this.user_name = user_name;
        this.category_name = category_name;
        this.threshold_value = threshold_value;
    }

    public Threshold(String user_name, String category_name, double threshold_value) {
        this.user_name = user_name;
        this.category_name = category_name;
        this.threshold_value = threshold_value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public double getThreshold_value() {
        return threshold_value;
    }

    public void setThreshold_value(double threshold_value) {
        this.threshold_value = threshold_value;
    }

    /*
    * INSERT INTO threshold (user_name,category_name,threshold_value)
        VALUES
            ("abhi","Rent",350),
            ("abhi","Food",350),
            ("abhi","Shopping",350);
    * */
    public String initDefaultThresholdInsertStatement(String userName) {
        String[] categoriesList = new Category().getDefaultCategories();
        String initInsertStatement = "INSERT INTO "+ THRESHOLD_TABLE +" (" + USER_NAME + ","+ CATEGORY_NAME +","+ THRESHOLD_VALUE +") VALUES ";

        int i = 0;
        StringBuilder values = new StringBuilder();
        while (i < categoriesList.length) {
            values.append("(") // (
                    .append("'").append(userName).append("'") // 'abhi'
                    .append(",'").append(categoriesList[i]).append("'") // ,'Food'
                    .append(",0)");// ,0)
            i++;
            if(i < categoriesList.length)
                values.append(",");
            else values.append(";");
        }

        initInsertStatement += values;

        return initInsertStatement;
    }
}
