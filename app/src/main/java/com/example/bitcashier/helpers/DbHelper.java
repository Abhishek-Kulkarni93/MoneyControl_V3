package com.example.bitcashier.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.CategoryExpense;
import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.Income;
import com.example.bitcashier.models.Threshold;
import com.example.bitcashier.models.User;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "bitcashier.db";

    public DbHelper( Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Category.CREATE_CATEGORY_TABLE);
        db.execSQL(Expense.CREATE_EXPENSE_TABLE);
        db.execSQL(Income.CREATE_INCOME_TABLE);
        db.execSQL(User.CREATE_USER_TABLE);
        db.execSQL(Threshold.CREATE_THRESHOLD_TABLE);

        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Category.CATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ Expense.EXPENSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ Income.INCOME_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ User.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ Threshold.THRESHOLD_TABLE);
        onCreate(db);
    }

    public void insertDefaultCategories(SQLiteDatabase db) {
        int categoryCount = 0;

        Cursor categoryCountCursor = db.rawQuery(Category.CATEGORY_COUNT_QUERY, null);
        categoryCount = (categoryCountCursor.moveToFirst()) ? categoryCountCursor.getInt(0) : 0;

        if(categoryCount == 0) {
            db.execSQL(new Category().initCategoryInsertStatement());
        }

        categoryCountCursor.close();
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("Select a category"));
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor categoriesCursor = db.rawQuery(Category.GET_ALL_CATEGORIES_QUERY, null);
        while (categoriesCursor.moveToNext()) {
            Category category = new Category(
                    categoriesCursor.getString(categoriesCursor.getColumnIndex(Category.CATEGORY_NAME)),
                    categoriesCursor.getString(categoriesCursor.getColumnIndex(Category.CATEGORY_ICON)),
                    categoriesCursor.getString(categoriesCursor.getColumnIndex(Category.CATEGORY_IMAGE))
            );

            category.setId(categoriesCursor.getInt(categoriesCursor.getColumnIndex(Category.ID)));

            categories.add(category);
        }
        categoriesCursor.close();

        return categories;
    }

    public ArrayList<String> getCategoryStringList() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Select a category");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor categoriesCursor = db.rawQuery(Category.GET_ALL_CATEGORY_NAMES_QUERY, null);
        while (categoriesCursor.moveToNext()) {
            categories.add(categoriesCursor.getString(categoriesCursor.getColumnIndex(Category.CATEGORY_NAME)));
        }
        categoriesCursor.close();

        return categories;
    }

    public boolean checkCategoryExists(String newCategory) {
        int categoryCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor categoryCountCursor = db.rawQuery(Category.CHECK_CATEGORY_EXISTS_QUERY, new String[]{newCategory});
        categoryCount = (categoryCountCursor.moveToFirst()) ? categoryCountCursor.getInt(0) : 0;
        categoryCountCursor.close();

        return (categoryCount > 0);
    }

    public Category getSingleCategory(String categoryName) {
        Category category = new Category();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor resultCursor = db.rawQuery(Category.GET_CATEGORY_BY_NAME_QUERY, new String[]{categoryName});

        /*
         * Table Index 0 -- ID
         * Table Index 1 -- name
         * Table Index 2 -- icon
         * Table Index 3 -- image
         * */
        while (resultCursor.moveToNext()) {
            category.setId(resultCursor.getInt(resultCursor.getColumnIndex(Category.ID)));
            category.setCategoryName(resultCursor.getString(resultCursor.getColumnIndex(Category.CATEGORY_NAME)));
            category.setCategory_icon(resultCursor.getString(resultCursor.getColumnIndex(Category.CATEGORY_ICON)));
            category.setCategory_image(resultCursor.getString(resultCursor.getColumnIndex(Category.CATEGORY_IMAGE)));
        }

        resultCursor.close();

        return category;
    }

    public boolean insertNewCategory(String userName, Category newCategory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues categoryContentValues = new ContentValues();
        categoryContentValues.put(Category.CATEGORY_NAME, newCategory.getCategoryName());
        categoryContentValues.put(Category.CATEGORY_ICON, newCategory.getCategory_icon());
        categoryContentValues.put(Category.CATEGORY_IMAGE, newCategory.getCategory_image());

        long categoryInsertResult = db.insert(Category.CATEGORY_TABLE, null, categoryContentValues);

        ContentValues thresholdContentValues = new ContentValues();
        thresholdContentValues.put(Threshold.USER_NAME, userName);
        thresholdContentValues.put(Threshold.CATEGORY_NAME, newCategory.getCategoryName());
        thresholdContentValues.put(Threshold.THRESHOLD_VALUE, 0);

        long thresholdInsertResult = db.insert(Threshold.THRESHOLD_TABLE, null, thresholdContentValues);

        return (categoryInsertResult != -1 && thresholdInsertResult != -1);
    }

    public boolean checkUserNameExists(String userName) {
        int userCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor userCountCursor = db.rawQuery(User.CHECK_USER_EXISTS_QUERY, new String[]{userName});
        userCount = (userCountCursor.moveToFirst()) ? userCountCursor.getInt(0) : 0;
        userCountCursor.close();

        return (userCount > 0);
    }

    public boolean insertNewUser(User newUser) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(User.USER_NAME, newUser.getUsername());
        contentValues.put(User.FULL_NAME, newUser.getFullName());
        contentValues.put(User.PASSWORD, newUser.getPassword());
        contentValues.put(User.CURRENCY, newUser.getCurrency());

        long result = db.insert(User.USER_TABLE, null, contentValues);

        return result != -1;
    }

    public User verifyUserCredentials(String userName) {
        User authUser = new User();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor resultCursor = db.rawQuery(User.GET_USER_QUERY, new String[]{userName});

        /*
         * Table Index 0 -- User Name
         * Table Index 1 -- Full Name
         * Table Index 1 -- Password
         * */
        while (resultCursor.moveToNext()) {
            authUser.setUsername(resultCursor.getString(resultCursor.getColumnIndex(User.USER_NAME)));
            authUser.setFullName(resultCursor.getString(resultCursor.getColumnIndex(User.FULL_NAME)));
            authUser.setPassword(resultCursor.getString(resultCursor.getColumnIndex(User.PASSWORD)));
            authUser.setCurrency(resultCursor.getString(resultCursor.getColumnIndex(User.CURRENCY)));
        }

        resultCursor.close();

        return authUser;
    }

    public boolean updateUserCurrency(String userName, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(User.CURRENCY, currency);

        String whereClause = User.USER_NAME+" = ?";
        String[] whereArgs = {userName};

        return db.update(User.USER_TABLE, contentValues, whereClause, whereArgs) > 0;
    }

    public void insertDefaultCategoryThresholdValues(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int thresholdCount = 0;

        Cursor thresholdCountCursor = db.rawQuery(Threshold.CHECK_THRESHOLD_EXISTS_FOR_USER_QUERY, new String[]{userName});
        thresholdCount = (thresholdCountCursor.moveToFirst()) ? thresholdCountCursor.getInt(0) : 0;

        if(thresholdCount == 0) {
            db.execSQL(new Threshold().initDefaultThresholdInsertStatement(userName));
        }

        thresholdCountCursor.close();
    }

    public boolean updateCategoryThresholdValue(Threshold thresholdData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Threshold.USER_NAME, thresholdData.getUser_name());
        contentValues.put(Threshold.CATEGORY_NAME, thresholdData.getCategory_name());
        contentValues.put(Threshold.THRESHOLD_VALUE, thresholdData.getThreshold_value());

        String whereClause = Threshold.ID+" = ?";
        String[] whereArgs = {String.valueOf(thresholdData.getId())};

        return db.update(Threshold.THRESHOLD_TABLE, contentValues, whereClause, whereArgs) > 0;
    }

    public Threshold getThresholdValue(String userName, String categoryName) {
        Threshold threshold = new Threshold();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor resultCursor = db.rawQuery(Threshold.GET_THRESHOLD_FOR_CATEGORY_QUERY, new String[]{userName,categoryName});

        /*
         * Table Index 0 -- ID
         * Table Index 1 -- user_name
         * Table Index 2 -- category_name
         * Table Index 3 -- threshold_value
         * */
        while (resultCursor.moveToNext()) {
            threshold.setId(resultCursor.getInt(resultCursor.getColumnIndex(Threshold.ID)));
            threshold.setUser_name(resultCursor.getString(resultCursor.getColumnIndex(Threshold.USER_NAME)));
            threshold.setCategory_name(resultCursor.getString(resultCursor.getColumnIndex(Threshold.CATEGORY_NAME)));
            threshold.setThreshold_value(resultCursor.getDouble(resultCursor.getColumnIndex(Threshold.THRESHOLD_VALUE)));
        }

        resultCursor.close();

        return threshold;
    }

    public boolean insertData(Expense expenseData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Expense.AMOUNT, expenseData.getAmount());
        contentValues.put(Expense.TITLE, expenseData.getTitle());
        contentValues.put(Expense.DATE, expenseData.getDate());
        contentValues.put(Expense.CATEGORY, expenseData.getCategory());
        contentValues.put(Expense.CONTACT_NAME, expenseData.getContact_name());
        contentValues.put(Expense.NOTES, expenseData.getNotes());
        contentValues.put(Expense.RECURRING, expenseData.getRecurring());
        contentValues.put(Expense.PAYMENT_TYPE, expenseData.getPayment_type());
        contentValues.put(Expense.USER_NAME, expenseData.getUserName());
        contentValues.put(Expense.CURRENCY, expenseData.getCurrency());
        long result = db.insert(Expense.EXPENSE_TABLE, null, contentValues);

        return result != -1;
    }

    public ArrayList<Expense> loadExpenseData(String userName, String category, String payment, String fromDate, String toDate) {

        String getExpensesQuery = "SELECT * FROM " + Expense.EXPENSE_TABLE + " WHERE "+ Expense.USER_NAME +"='"+ userName +"'";

        if(!category.isEmpty() || !payment.isEmpty() || !fromDate.isEmpty()) {
            // Add the where clause
            getExpensesQuery += " AND ";

            // Select * from expenses where category='Shopping'
            if(!category.isEmpty()) {
                getExpensesQuery += " "+ Expense.CATEGORY +"='" + category + "'";
            }

            // Select * from expenses where payment_type='Cash'
            if(!payment.isEmpty()) {
                if(!category.isEmpty()) {
                    getExpensesQuery += " AND ";
                }
                getExpensesQuery += " "+ Expense.PAYMENT_TYPE +"='" + payment + "'";
            }

            // Select * from expenses where category='Shopping' and payment_type='Cash' and date(date) between '2020-05-01' and '2020-05-10'
            if(!fromDate.isEmpty() && !toDate.isEmpty()) {
                if(!category.isEmpty() || !payment.isEmpty()) {
                    getExpensesQuery += " AND ";
                }
                getExpensesQuery += " date(date) BETWEEN '" + fromDate + "' AND '" + toDate + "'";
            }
        }

        // Date sorting
        getExpensesQuery += " ORDER BY date(date) DESC";

        System.out.println("QUERY: " + getExpensesQuery);

        return executeQueryAndParseResults(getExpensesQuery);
    }

    public ArrayList<Expense> loadRecurringExpenses(String userName) {

        String getRecurringExpensesQuery = "SELECT * FROM "
                + Expense.EXPENSE_TABLE +" WHERE "
                + Expense.USER_NAME +"= '"+ userName +"' AND "
                + Expense.RECURRING +"='yes' ORDER BY date(date) DESC";

        System.out.println("QUERY: " + getRecurringExpensesQuery);

        return executeQueryAndParseResults(getRecurringExpensesQuery);
    }

    private ArrayList<Expense> executeQueryAndParseResults(String selectQuery) {

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Expense> expensesList = new ArrayList<>();
        Cursor resultCursor = db.rawQuery(selectQuery, null);

        /*
         * Table Index 0 -- ID
         * Table Index 1 -- Amount
         * Table Index 2 -- title
         * Table Index 3 -- date
         * Table Index 4 -- category
         * Table Index 5 -- payment_type
         * Table Index 6 -- notes
         * Table Index 7 -- recurring
         * Table Index 8 -- user_name
         * Table Index 9 -- currency
         * */
        while (resultCursor.moveToNext()) {
            Expense expenseData = new Expense(
                    resultCursor.getDouble(resultCursor.getColumnIndex(Expense.AMOUNT)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.TITLE)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.DATE)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.CATEGORY)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.PAYMENT_TYPE)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.NOTES)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.RECURRING)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.USER_NAME)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.CONTACT_NAME)),
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.CURRENCY))
            );

            expenseData.setId(resultCursor.getInt(resultCursor.getColumnIndex(Expense.ID)));

            expensesList.add(expenseData);
        }

        resultCursor.close();

        return expensesList;
    }

    public double getUserTotalExpense(String userName, String category) {
        double userTotalExpense = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String expenseQuery = "";
        String[] selectionArgs;
        if(category.isEmpty()) {
            selectionArgs = new String[]{userName};
            expenseQuery = Expense.USER_EXPENSE_SUM_QUERY;
        } else {
            selectionArgs = new String[]{userName,category};
            expenseQuery = Expense.USER_EXPENSE_SUM_BY_CATEGORY_QUERY;
        }

        Cursor expenseCursor = db.rawQuery(expenseQuery, selectionArgs);
        userTotalExpense = (expenseCursor.moveToFirst()) ? expenseCursor.getDouble(0) : 0;
        expenseCursor.close();

        return userTotalExpense;
    }

    public Expense getExpenseById(int expenseId) {
        Expense expense = new Expense();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor resultCursor = db.rawQuery(Expense.GET_EXPENSE_BY_ID_QUERY, new String[]{String.valueOf(expenseId)});

        /*
         * Table Index 0 -- ID
         * Table Index 1 -- Amount
         * Table Index 2 -- title
         * Table Index 3 -- date
         * Table Index 4 -- category
         * Table Index 5 -- payment_type
         * Table Index 6 -- notes
         * Table Index 7 -- recurring
         * Table Index 8 -- user_name
         * */
        while (resultCursor.moveToNext()) {
            expense.setId(resultCursor.getInt(resultCursor.getColumnIndex(Expense.ID)));
            expense.setAmount(resultCursor.getDouble(resultCursor.getColumnIndex(Expense.AMOUNT)));
            expense.setTitle(resultCursor.getString(resultCursor.getColumnIndex(Expense.TITLE)));
            expense.setDate(resultCursor.getString(resultCursor.getColumnIndex(Expense.DATE)));
            expense.setCategory(resultCursor.getString(resultCursor.getColumnIndex(Expense.CATEGORY)));
            expense.setPayment_type(resultCursor.getString(resultCursor.getColumnIndex(Expense.PAYMENT_TYPE)));
            expense.setNotes(resultCursor.getString(resultCursor.getColumnIndex(Expense.NOTES)));
            expense.setRecurring(resultCursor.getString(resultCursor.getColumnIndex(Expense.RECURRING)));
            expense.setUserName(resultCursor.getString(resultCursor.getColumnIndex(Expense.USER_NAME)));
            expense.setContact_name(resultCursor.getString(resultCursor.getColumnIndex(Expense.CONTACT_NAME)));
            expense.setCurrency(resultCursor.getString(resultCursor.getColumnIndex(Expense.CURRENCY)));
        }

        resultCursor.close();

        return expense;
    }

    public boolean updateExpenseData(Expense expenseData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Expense.AMOUNT, expenseData.getAmount());
        contentValues.put(Expense.TITLE, expenseData.getTitle());
        contentValues.put(Expense.DATE, expenseData.getDate());
        contentValues.put(Expense.CATEGORY, expenseData.getCategory());
        contentValues.put(Expense.CONTACT_NAME, expenseData.getContact_name());
        contentValues.put(Expense.NOTES, expenseData.getNotes());
        contentValues.put(Expense.RECURRING, expenseData.getRecurring());
        contentValues.put(Expense.PAYMENT_TYPE, expenseData.getPayment_type());
        contentValues.put(Expense.USER_NAME, expenseData.getUserName());
        contentValues.put(Expense.CURRENCY, expenseData.getCurrency());

        String whereClause = Expense.ID+" = ?";
        String[] whereArgs = {String.valueOf(expenseData.getId())};

        return db.update(Expense.EXPENSE_TABLE, contentValues, whereClause, whereArgs) > 0;
    }

    public boolean deleteExpenseData(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = Expense.ID+" = ?";
        String[] whereArgs = {String.valueOf(expenseId)};

        return db.delete(Expense.EXPENSE_TABLE, whereClause, whereArgs) > 0;
    }

    public boolean insertUserIncome(Income incomeData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Income.AMOUNT, incomeData.getAmount());
        contentValues.put(Income.DATE, incomeData.getDate());
        contentValues.put(Income.NOTES, incomeData.getNotes());
        contentValues.put(Income.USER_NAME, incomeData.getUserName());
        long result = db.insert(Income.INCOME_TABLE, null, contentValues);

        return result != -1;
    }

    public ArrayList<Income> getUserIncome(String userName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Income> incomeList = new ArrayList<>();
        Cursor resultCursor = db.rawQuery(Income.GET_USER_INCOME_QUERY, new String[]{userName});

        /*
         * Table Index 0 -- ID
         * Table Index 1 -- Amount
         * Table Index 2 -- Date
         * Table Index 3 -- notes
         * Table Index 4 -- user_name
         * */
        while (resultCursor.moveToNext()) {
            Income incomeData = new Income(
                    resultCursor.getDouble(resultCursor.getColumnIndex(Income.AMOUNT)),
                    resultCursor.getString(resultCursor.getColumnIndex(Income.DATE)),
                    resultCursor.getString(resultCursor.getColumnIndex(Income.NOTES)),
                    resultCursor.getString(resultCursor.getColumnIndex(Income.USER_NAME))
            );

            incomeData.setId(resultCursor.getInt(resultCursor.getColumnIndex(Income.ID)));

            incomeList.add(incomeData);
        }

        resultCursor.close();

        return incomeList;
    }

    public double getUserTotalIncome(String userName) {
        double userTotalIncome = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor incomeCursor = db.rawQuery(Income.USER_INCOME_SUM_QUERY, new String[]{userName});
        userTotalIncome = (incomeCursor.moveToFirst()) ? incomeCursor.getDouble(0) : 0;
        incomeCursor.close();

        return userTotalIncome;
    }

    public boolean updateUserIncome(Income incomeData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Income.AMOUNT, incomeData.getAmount());
        contentValues.put(Income.DATE, incomeData.getDate());
        contentValues.put(Income.NOTES, incomeData.getNotes());
        contentValues.put(Income.USER_NAME, incomeData.getUserName());

        String whereClause = Income.ID+" = ?";
        String[] whereArgs = {String.valueOf(incomeData.getId())};

        return db.update(Income.INCOME_TABLE, contentValues, whereClause, whereArgs) > 0;
    }

    public boolean deleteUserIncome(int incomeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = Income.ID+" = ?";
        String[] whereArgs = {String.valueOf(incomeId)};

        return db.delete(Income.INCOME_TABLE, whereClause, whereArgs) > 0;
    }

    public ArrayList<CategoryExpense> getMonthlyCategoryExpenseData(String userName, String month, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CategoryExpense> expenseArrayList = new ArrayList<>();

        Cursor resultCursor = db.rawQuery(CategoryExpense.CATEGORY_EXPENSE_FOR_MONTH_QUERY, new String[]{userName,month,year});

        /*
         * Table Index 0 -- category
         * Table Index 1 -- amount
         * */
        while (resultCursor.moveToNext()) {
            CategoryExpense categoryExpense = new CategoryExpense(
                    resultCursor.getDouble(resultCursor.getColumnIndex(CategoryExpense.AMOUNT)),
                    resultCursor.getString(resultCursor.getColumnIndex(CategoryExpense.CATEGORY_NAME))
            );

            expenseArrayList.add(categoryExpense);
        }

        resultCursor.close();

        return expenseArrayList;
    }

    public ArrayList<CategoryExpense> getYearlyCategoryExpenseData(String userName, String year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CategoryExpense> expenseArrayList = new ArrayList<>();

        Cursor resultCursor = db.rawQuery(CategoryExpense.CATEGORY_EXPENSE_FOR_YEAR_QUERY, new String[]{userName,year});

        /*
         * Table Index 0 -- category
         * Table Index 1 -- amount
         * */
        while (resultCursor.moveToNext()) {
            CategoryExpense categoryExpense = new CategoryExpense(
                    resultCursor.getDouble(resultCursor.getColumnIndex(CategoryExpense.AMOUNT)),
                    resultCursor.getString(resultCursor.getColumnIndex(CategoryExpense.CATEGORY_NAME))
            );

            expenseArrayList.add(categoryExpense);
        }

        resultCursor.close();

        return expenseArrayList;
    }

    public ArrayList<CategoryExpense> getYearWiseExpenseData(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<CategoryExpense> expenseArrayList = new ArrayList<>();

        Cursor resultCursor = db.rawQuery(CategoryExpense.CATEGORY_EXPENSE_BY_YEAR_QUERY, new String[]{userName});

        /*
         * Table Index 0 -- category
         * Table Index 1 -- amount
         * */
        while (resultCursor.moveToNext()) {
            CategoryExpense categoryExpense = new CategoryExpense(
                    resultCursor.getInt(resultCursor.getColumnIndex(CategoryExpense.YEAR)),
                    resultCursor.getDouble(resultCursor.getColumnIndex(CategoryExpense.AMOUNT))
            );

            expenseArrayList.add(categoryExpense);
        }

        resultCursor.close();

        return expenseArrayList;
    }
}
