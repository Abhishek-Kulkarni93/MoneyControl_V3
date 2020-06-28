package com.example.bitcashier;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bitcashier.helpers.CategoryAdapter;
import com.example.bitcashier.helpers.CategoryItem;
import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.helpers.ExpenseArrayAdapter;
import com.example.bitcashier.helpers.PreferencesHelper;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionHistoryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    ListView expenseListView;
    Spinner spinnerCategory, spinnerPayment;
    EditText editFromDate, editToDate;
    Button btnSearchData, btnFromDate, btnToDate, btnResetForm;

    String selectedCategory = "", selectedPayment = "", selectedFromDate = "", selectedToDate = "";
    private int mYear, mMonth, mDay;
    DateHelper dateHelper;
    User authUser;
    CategoryAdapter customCategoryAdapter;
    String appPackageName;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View transactionHistoryView = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        dateHelper = new DateHelper();
        DbHelper expenseDB = new DbHelper(transactionHistoryView.getContext());
        appPackageName = transactionHistoryView.getContext().getPackageName();

        editFromDate = transactionHistoryView.findViewById(R.id.editText_fromDate);
        editToDate = transactionHistoryView.findViewById(R.id.editText_toDate);
        expenseListView = transactionHistoryView.findViewById(R.id.lv_expensesList);
        spinnerCategory = transactionHistoryView.findViewById(R.id.spinner_filter_category);
        spinnerPayment = transactionHistoryView.findViewById(R.id.spinner_filter_payment);
        btnSearchData = transactionHistoryView.findViewById(R.id.button_searchData);
        btnFromDate = transactionHistoryView.findViewById(R.id.button_selectFromDate);
        btnToDate = transactionHistoryView.findViewById(R.id.button_selectToDate);
        btnResetForm = transactionHistoryView.findViewById(R.id.button_resetForm);

        ArrayList<Category> categoriesList = expenseDB.getCategories();
        ArrayList<CategoryItem> categoryItemsList = addIconsToCategoryList(transactionHistoryView, categoriesList);
        customCategoryAdapter = new CategoryAdapter(transactionHistoryView.getContext(), categoryItemsList);
        spinnerCategory.setAdapter(customCategoryAdapter);
        spinnerCategory.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(transactionHistoryView.getContext(),
                R.array.payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(paymentAdapter);
        spinnerPayment.setOnItemSelectedListener(this);

        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFromDateValue(v);
            }
        });

        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToDateValue(v);
            }
        });

        btnSearchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpenseData(v);
            }
        });

        btnResetForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSearchForm(v);
            }
        });

        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expense expense = (Expense) parent.getAdapter().getItem(position);

                EditExpenseFragment editExpenseFragment = EditExpenseFragment.newInstance(
                        String.valueOf(expense.getId()), "no");

                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.navHostFragment,editExpenseFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        authUser = new PreferencesHelper(transactionHistoryView.getContext())
                .getAuthenticatedUser();

        getExpenseData(transactionHistoryView);
        // Inflate the layout for this fragment
        return transactionHistoryView;
    }

    public ArrayList<CategoryItem> addIconsToCategoryList(View view, ArrayList<Category> categories) {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();

        for (int i=0; i<categories.size(); i++) {
            Category currentCategory = categories.get(i);
            int categoryImageId = view.getContext().getResources()
                    .getIdentifier(appPackageName+":drawable/"+currentCategory.getCategory_icon() , null, null);
            categoryItems.add(new CategoryItem(categoryImageId, currentCategory.getCategoryName()));
        }

        return categoryItems;
    }

    public void setFromDateValue(View view) {
        mYear = dateHelper.getCurrYear();
        mMonth = dateHelper.getCurrMonth();
        mDay = dateHelper.getCurrDay();

        if(!editFromDate.getText().toString().isEmpty()) {
            DateHelper selectedFromDateHelper = new DateHelper(editFromDate.getText().toString(), "/");
            mYear = selectedFromDateHelper.getGivenYear();
            mMonth = selectedFromDateHelper.getGivenMonth();
            mDay = selectedFromDateHelper.getGivenDay();
        }

        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateHelper fromDateHelper = new DateHelper(year, month, dayOfMonth);
                editFromDate.setText(fromDateHelper.getGivenDateInDisplayFormat());
                selectedFromDate = fromDateHelper.getGivenDateInStoreFormat();
            }
        }, mYear, mMonth, mDay);
        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        fromDatePickerDialog.show();

    }

    public void setToDateValue(View view) {
        mYear = dateHelper.getCurrYear();
        mMonth = dateHelper.getCurrMonth();
        mDay = dateHelper.getCurrDay();

        if(!editToDate.getText().toString().isEmpty()) {
            DateHelper selectedToDateHelper = new DateHelper(editToDate.getText().toString(), "/");
            mYear = selectedToDateHelper.getGivenYear();
            mMonth = selectedToDateHelper.getGivenMonth();
            mDay = selectedToDateHelper.getGivenDay();
        }

        DatePickerDialog toDatePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateHelper toDateHelper = new DateHelper(year, month, dayOfMonth);
                editToDate.setText(toDateHelper.getGivenDateInDisplayFormat());
                selectedToDate = toDateHelper.getGivenDateInStoreFormat();
            }
        }, mYear, mMonth, mDay);
        toDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        toDatePickerDialog.show();

    }

    public void resetSearchForm(View view) {
        selectedCategory = "";
        selectedPayment = "";
        selectedFromDate = "";
        selectedToDate = "";

        editFromDate.setText("");
        editToDate.setText("");
        spinnerCategory.setSelection(0);
        spinnerPayment.setSelection(0);

        getExpenseData(view);
    }

    public void getExpenseData(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        ArrayList<Expense> expensesList = expenseDB.loadExpenseData(authUser.getUsername(),selectedCategory,selectedPayment,selectedFromDate,selectedToDate);
        ExpenseArrayAdapter expenseAdapter = new ExpenseArrayAdapter(view.getContext(), R.layout.card_view_row_item, expensesList);
        expenseListView.setAdapter(expenseAdapter);

        if (expensesList.size() ==  0) {
            Toast.makeText(view.getContext(),"No expenses found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(id > 0) {
            if (parent.getId() == R.id.spinner_filter_category) {
                CategoryItem item = (CategoryItem) parent.getSelectedItem();
                selectedCategory = item.getSpinnerItemName();
            } else if (parent.getId() == R.id.spinner_filter_payment) {
                selectedPayment = parent.getItemAtPosition(position).toString();
            }
            //Toast.makeText(MainActivity.this, "CAT: " + selectedCategory + " | " + "PT: " + selectedPaymentType, Toast.LENGTH_LONG).show();
        } else {
            if (parent.getId() == R.id.spinner_filter_category) {
                selectedCategory = "";
            } else if (parent.getId() == R.id.spinner_filter_payment) {
                selectedPayment = "";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
