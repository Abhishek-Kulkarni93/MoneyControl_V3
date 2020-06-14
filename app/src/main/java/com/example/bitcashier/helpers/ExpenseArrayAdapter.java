package com.example.bitcashier.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bitcashier.R;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Expense;

import java.util.ArrayList;

public class ExpenseArrayAdapter extends ArrayAdapter<Expense> {

    private static final String TAG = "ExpenseArrayAdapter";
    private Context context;
    private ArrayList<Expense> expenseArrayList;

    public ExpenseArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Expense> objects) {
        super(context, resource, objects);

        this.context = context;
        this.expenseArrayList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //get the expense we are displaying
        Expense expense = expenseArrayList.get(position);
        DbHelper expenseDB = new DbHelper(context);
        Category xpCategoryData = expenseDB.getSingleCategory(expense.getCategory());

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View expenseItemCardView = inflater.inflate(R.layout.card_view_row_item, null);
        String appPackageName = context.getPackageName();

        TextView tvExpenseTitle = (TextView) expenseItemCardView.findViewById(R.id.tv_title);
        TextView tvExpenseAmount = (TextView) expenseItemCardView.findViewById(R.id.tv_amount);
        TextView tvExpenseDate = (TextView) expenseItemCardView.findViewById(R.id.tv_date);
        ImageView ivExpenseCategory = (ImageView) expenseItemCardView.findViewById(R.id.img_category);

        tvExpenseTitle.setText(expense.getTitle());
        tvExpenseAmount.setText("€"+ expense.getAmount());
        tvExpenseDate.setText(expense.getDateInDisplayFormat());

        int categoryImageId = context.getResources()
                .getIdentifier(appPackageName+":drawable/"+xpCategoryData.getCategory_image() , null, null);

        ivExpenseCategory.setImageResource(categoryImageId);

        return expenseItemCardView;
    }
}
