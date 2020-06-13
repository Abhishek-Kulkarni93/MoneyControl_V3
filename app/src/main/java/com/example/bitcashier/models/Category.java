package com.example.bitcashier.models;

import android.content.res.Resources;
import android.util.Log;

import java.util.HashMap;

public class Category {

    public static final String CATEGORY_TABLE = "categories";
    public static final String ID = "ID";
    public static final String CATEGORY_NAME = "CATEGORY_NAME";

    public static final String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CATEGORY_NAME + " TEXT NOT NULL)";

    public static final String CATEGORY_COUNT_QUERY = "SELECT COUNT(*) FROM " + CATEGORY_TABLE;
    public static final String GET_ALL_CATEGORIES_QUERY = "SELECT "+ CATEGORY_NAME +" FROM " + CATEGORY_TABLE;
    public static final String CHECK_CATEGORY_EXISTS_QUERY = CATEGORY_COUNT_QUERY + " WHERE " + CATEGORY_NAME + "= ? COLLATE NOCASE";

    private int id;
    private String category_name;
    private String[] defaultCategories = {"Rent", "Food", "Shopping", "Entertainment", "Travel", "Education", "Medical", "Utilities", "Lent to a Friend", "Others"};
    private HashMap<String, String> defaultCategoryImages;

    public Category() {
        // Empty constructor..
        this.buildCategoryImageMap();
    }

    public Category(String name) {
        this.category_name = name;
        this.buildCategoryImageMap();
    }

    /*
     * {
     *   "rent": "cv_rent",
     *   "food": "cv_food",
     *   "shopping": "cv_shopping",
     *   "entertainment": "cv_entertainment",
     *   "travel": "cv_travel",
     *   "education": "cv_education",
     *   "medical": "medical",
     *   "utilities": "cv_utilities",
     *   "loan_friend": "cv_friend",
     *   "others": "cv_others",
     * }
     * */
    private void buildCategoryImageMap() {
        defaultCategoryImages = new HashMap<>();
        int i = 0;
        while (i < defaultCategories.length) {
            if(defaultCategories[i].equals("Lent to a Friend")) {
                this.defaultCategoryImages.put(
                        "loan_friend",
                        "cv_"+defaultCategories[i].toLowerCase()
                );
            } else {
                this.defaultCategoryImages.put(
                        defaultCategories[i].toLowerCase(),
                        "cv_"+defaultCategories[i].toLowerCase()
                );
            }
        }
    }

    public String getCategoryImageForCardView(String categoryKey) {
        return defaultCategoryImages.get(categoryKey.toLowerCase());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    public HashMap<String, String> getDefaultCategoryImages() {
        return defaultCategoryImages;
    }

    /*
        * INSERT INTO artists (name)
            VALUES
                ("Buddy Rich"),
                ("Candido"),
                ("Charlie Byrd");
        * */
    public String initCategoryInsertStatement() {
        String initInsertStatement = "INSERT INTO "+ CATEGORY_TABLE +" (" + CATEGORY_NAME + ") VALUES ";

        int i = 0;
        StringBuilder values = new StringBuilder();
        while (i < defaultCategories.length) {
            values.append("('").append(defaultCategories[i]).append("')");
            i++;
            if(i < defaultCategories.length)
                values.append(",");
            else values.append(";");
        }

        initInsertStatement += values;

        return initInsertStatement;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", category_name='" + category_name + '\'' +
                '}';
    }
}
