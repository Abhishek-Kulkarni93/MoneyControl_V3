package com.example.bitcashier.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.Income;
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

        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Category.CATEGORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ Expense.EXPENSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ Income.INCOME_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ User.USER_TABLE);
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

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select a category");
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor categoriesCursor = db.rawQuery(Category.GET_ALL_CATEGORIES_QUERY, null);
        while (categoriesCursor.moveToNext()) {
            categories.add(categoriesCursor.getString(0));
        }

        return categories;
    }

    public boolean checkCategoryExists(String newCategory) {
        int categoryCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor categoryCountCursor = db.rawQuery(Category.CHECK_CATEGORY_EXISTS_QUERY, new String[]{newCategory});
        categoryCount = (categoryCountCursor.moveToFirst()) ? categoryCountCursor.getInt(0) : 0;

        return (categoryCount > 0);
    }

    public boolean insertNewCategory(Category newCategory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Category.CATEGORY_NAME, newCategory.getCategoryName());

        long result = db.insert(Category.CATEGORY_TABLE, null, contentValues);

        return result != -1;
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
        }

        resultCursor.close();

        return authUser;
    }

    public boolean insertData(Expense expenseData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Expense.AMOUNT, expenseData.getAmount());
        contentValues.put(Expense.TITLE, expenseData.getTitle());
        contentValues.put(Expense.DATE, expenseData.getDate());
        contentValues.put(Expense.CATEGORY, expenseData.getCategory());
        contentValues.put(Expense.NOTES, expenseData.getNotes());
        contentValues.put(Expense.RECURRING, expenseData.getRecurring());
        contentValues.put(Expense.PAYMENT_TYPE, expenseData.getPayment_type());
        contentValues.put(Expense.USER_NAME, expenseData.getUserName());
        long result = db.insert(Expense.EXPENSE_TABLE, null, contentValues);

        return result != -1;
    }

    @SuppressLint("Recycle")
    public ArrayList<Expense> loadExpenseData(String userName, String category, String payment, String fromDate, String toDate) {

        String getExpensesQuery = "SELECT * FROM " + Expense.EXPENSE_TABLE;

        if(!category.isEmpty() || !payment.isEmpty() || !fromDate.isEmpty()) {
            // Add the where clause
            getExpensesQuery += " WHERE "+ Expense.USER_NAME +"= '"+ userName +"' AND ";

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
                    getExpensesQuery += " and ";
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
                    resultCursor.getString(resultCursor.getColumnIndex(Expense.USER_NAME))
            );

            expenseData.setId(resultCursor.getInt(resultCursor.getColumnIndex(Expense.ID)));

            expensesList.add(expenseData);
        }

        resultCursor.close();

        return expensesList;
    }

    public double getUserTotalExpense(String userName) {
        double userTotalExpense = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor expenseCursor = db.rawQuery(Expense.USER_EXPENSE_SUM_QUERY, new String[]{userName});
        userTotalExpense = (expenseCursor.moveToFirst()) ? expenseCursor.getInt(0) : 0;
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
            expense.setAmount(resultCursor.getDouble(resultCursor.getColumnIndex(Expense.AMOUNT)));
            expense.setTitle(resultCursor.getString(resultCursor.getColumnIndex(Expense.TITLE)));
            expense.setDate(resultCursor.getString(resultCursor.getColumnIndex(Expense.DATE)));
            expense.setCategory(resultCursor.getString(resultCursor.getColumnIndex(Expense.CATEGORY)));
            expense.setPayment_type(resultCursor.getString(resultCursor.getColumnIndex(Expense.PAYMENT_TYPE)));
            expense.setNotes(resultCursor.getString(resultCursor.getColumnIndex(Expense.NOTES)));
            expense.setRecurring(resultCursor.getString(resultCursor.getColumnIndex(Expense.RECURRING)));
            expense.setUserName(resultCursor.getString(resultCursor.getColumnIndex(Expense.USER_NAME)));
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
        contentValues.put(Expense.NOTES, expenseData.getNotes());
        contentValues.put(Expense.RECURRING, expenseData.getRecurring());
        contentValues.put(Expense.PAYMENT_TYPE, expenseData.getPayment_type());
        contentValues.put(Expense.USER_NAME, expenseData.getUserName());

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
        userTotalIncome = (incomeCursor.moveToFirst()) ? incomeCursor.getInt(0) : 0;
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
}
