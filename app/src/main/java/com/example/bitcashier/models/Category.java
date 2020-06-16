package com.example.bitcashier.models;

public class Category {

    public static final String CATEGORY_TABLE = "categories";
    public static final String ID = "ID";
    public static final String CATEGORY_NAME = "CATEGORY_NAME";
    public static final String CATEGORY_ICON = "CATEGORY_ICON";
    public static final String CATEGORY_IMAGE = "CATEGORY_IMAGE";

    public static final String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CATEGORY_NAME + " TEXT NOT NULL,"+ CATEGORY_ICON +" TEXT,"+ CATEGORY_IMAGE +" TEXT)";

    public static final String CATEGORY_COUNT_QUERY = "SELECT COUNT(*) FROM " + CATEGORY_TABLE;
    public static final String GET_ALL_CATEGORIES_QUERY = "SELECT * FROM " + CATEGORY_TABLE;
    public static final String GET_ALL_CATEGORY_NAMES_QUERY = "SELECT "+ CATEGORY_NAME +" FROM " + CATEGORY_TABLE;
    public static final String CHECK_CATEGORY_EXISTS_QUERY = CATEGORY_COUNT_QUERY + " WHERE " + CATEGORY_NAME + "= ? COLLATE NOCASE";
    public static final String GET_CATEGORY_BY_NAME_QUERY = "SELECT * FROM " + CATEGORY_TABLE + " WHERE " + CATEGORY_NAME + "= ? COLLATE NOCASE";

    private int id;
    private String category_name;
    private String category_icon;
    private String category_image;
    private String[] defaultCategories = {"Rent", "Food", "Shopping", "Entertainment", "Travel", "Education", "Medical", "Utilities", "Friend", "Others"};

    public Category() {
        // Empty constructor..
    }

    // Default Category Icon & Image Constructor
    public Category(String name) {
        this.id = 0;
        this.category_name = name;
        this.category_icon = "ic_others";
        this.category_image = "cv_others";
    }

    public Category(String name, String icon, String image) {
        this.category_name = name;
        this.category_icon = icon;
        this.category_image = image;
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

    public String getCategory_icon() {
        return category_icon;
    }

    public void setCategory_icon(String category_icon) {
        this.category_icon = category_icon;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    /*
    * INSERT INTO categories (name,icon,image)
        VALUES
            ("Rent","ic_rent","cv_rent"),
            ("Food","ic_food","cv_food"),
            ("Shopping","ic_shopping","cv_shopping");
    * */
    public String initCategoryInsertStatement() {
        String initInsertStatement = "INSERT INTO "+ CATEGORY_TABLE +" (" + CATEGORY_NAME + ","+ CATEGORY_ICON +","+ CATEGORY_IMAGE +") VALUES ";

        int i = 0;
        StringBuilder values = new StringBuilder();
        while (i < defaultCategories.length) {
            values.append("(") // (
                    .append("'").append(defaultCategories[i]).append("'") // 'Rent'
                    .append(",'ic_").append(defaultCategories[i].toLowerCase()).append("'") // ,'ic_rent'
                    .append(",'cv_").append(defaultCategories[i].toLowerCase()).append("'") // ,'cv_rent'
                    .append(")"); // )
            i++;
            if(i < defaultCategories.length)
                values.append(",");
            else values.append(";");
        }

        initInsertStatement += values;

        return initInsertStatement;
    }

    public String[] getDefaultCategories() {
        return defaultCategories;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", category_name='" + category_name + '\'' +
                '}';
    }
}
