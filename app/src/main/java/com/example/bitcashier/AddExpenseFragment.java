package com.example.bitcashier;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bitcashier.helpers.CategoryAdapter;
import com.example.bitcashier.helpers.CategoryItem;
import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddExpenseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText editAmount, editTitle, editDate, editComment;
    Button btnSelectDate, btnAddData;
    ImageButton btnAddNewCategory;
    Spinner spinnerCategory, spinnerPaymentType;
    Switch switchRecurring;

    String selectedDate = "", selectedCategory = "", selectedPaymentType = "";
    private int mYear, mMonth, mDay;
    DateHelper dateHelper;
    User authUser;
    DbHelper expenseDB;
    CategoryAdapter customCategoryAdapter;
    String appPackageName;

    public AddExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addExpenseView = inflater.inflate(R.layout.fragment_add_expense, container, false);

        expenseDB = new DbHelper(addExpenseView.getContext());
        dateHelper = new DateHelper();
        authUser = getAuthorizedUser();
        appPackageName = addExpenseView.getContext().getPackageName();

        editAmount = addExpenseView.findViewById(R.id.editText_amount);
        editTitle = addExpenseView.findViewById(R.id.editText_title);
        editDate = addExpenseView.findViewById(R.id.editText_date);
        editComment = addExpenseView.findViewById(R.id.editText_comment);
        switchRecurring = addExpenseView.findViewById(R.id.switch_recurring);
        btnSelectDate = addExpenseView.findViewById(R.id.button_selectDate);
        btnAddData = addExpenseView.findViewById(R.id.button_addData);
        btnAddNewCategory = addExpenseView.findViewById(R.id.button_addNewCategory);
        spinnerCategory = addExpenseView.findViewById(R.id.spinner_category);
        spinnerPaymentType = addExpenseView.findViewById(R.id.spinner_paymentType);

        editDate.setText(dateHelper.getCurrDateInDisplayFormat());
        selectedDate = dateHelper.getCurrDateInStoreFormat();

        ArrayList<Category> categoriesList = expenseDB.getCategories();
        ArrayList<CategoryItem> categoryItemsList = addIconsToCategoryList(addExpenseView, categoriesList);
        customCategoryAdapter = new CategoryAdapter(addExpenseView.getContext(), categoryItemsList);
        spinnerCategory.setAdapter(customCategoryAdapter);
        spinnerCategory.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> paymentTypeAdapter = ArrayAdapter.createFromResource(addExpenseView.getContext(),
                R.array.payment_type, android.R.layout.simple_spinner_item);
        paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentType.setAdapter(paymentTypeAdapter);
        spinnerPaymentType.setOnItemSelectedListener(this);

        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View clickView = v;
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                View addCategoryView = getLayoutInflater().inflate(R.layout.dialog_add_new_category,null);
                final EditText etAddCategory = (EditText)addCategoryView.findViewById(R.id.editText_category);
                Button btn_cancel = (Button)addCategoryView.findViewById(R.id.btn_cancel);
                Button btn_add = (Button)addCategoryView.findViewById(R.id.btn_add);
                alert.setView(addCategoryView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String enteredCategoryVal = etAddCategory.getText().toString();

                        if(enteredCategoryVal.isEmpty()) {
                            Toast.makeText(v.getContext(),
                                    "Please enter a value",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if(expenseDB.checkCategoryExists(enteredCategoryVal.trim())) {
                                Toast.makeText(clickView.getContext(),
                                        enteredCategoryVal + " category already exists",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                alertDialog.dismiss();
                                addNewCategory(clickView, enteredCategoryVal);
                            }
                        }

                    }
                });
                alertDialog.show();
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpenseDate(v);
            }
        });

        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense(v);
            }
        });

        // Inflate the layout for this fragment
        return addExpenseView;
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

    public void addNewCategory(View view, String newCategoryValue) {
        Category newCategory = new Category(newCategoryValue);
        int categoryImageId = view.getContext().getResources()
                .getIdentifier(appPackageName+":drawable/"+newCategory.getCategory_icon() , null, null);
        CategoryItem newCategoryItem = new CategoryItem(categoryImageId, newCategoryValue);
        boolean isInserted =  expenseDB.insertNewCategory(newCategory);
        if(isInserted) {
            Toast.makeText(view.getContext(),
                    newCategoryValue + " category added successfully",
                    Toast.LENGTH_LONG).show();
            customCategoryAdapter.add(newCategoryItem);
            customCategoryAdapter.notifyDataSetChanged();
            spinnerCategory.setSelection(customCategoryAdapter.getPosition(newCategoryItem));
        } else {
            Toast.makeText(view.getContext(),
                    newCategoryValue + " category was not added due to errors",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void addExpense(View view) {
        double amount = 0;
        String amountString = editAmount.getText().toString(),
                title = editTitle.getText().toString(),
                date = editDate.getText().toString(),
                comment = editComment.getText().toString(),
                recurring = (switchRecurring.isChecked()) ? "yes" : "no";

        if(!amountString.isEmpty() && !date.isEmpty() && !selectedCategory.isEmpty() && !selectedPaymentType.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
                if(amount < 100000) {
                    Expense newExpense = new Expense(amount, title, selectedDate, selectedCategory, selectedPaymentType, comment, recurring, authUser.getUsername());
                    boolean isInserted =  expenseDB.insertData(newExpense);
                    if(isInserted) {
                        Toast.makeText(view.getContext(),"Expense added successfully", Toast.LENGTH_LONG).show();
                        HomeFragment homeFragment = new HomeFragment();
                        FragmentManager manager = getParentFragmentManager();
                        manager.beginTransaction()
                                .replace(R.id.navHostFragment,homeFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(view.getContext(),"Expense was not added due to errors", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(view.getContext(),"Amount cannot be greater than 5 digits", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(),"Please enter only numbers", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),"Please enter all details", Toast.LENGTH_LONG).show();
        }
    }

    public void resetExpenseForm() {
        editAmount.setText("");
        editTitle.setText("");
        editDate.setText("");
        editComment.setText("");

        spinnerCategory.setSelection(0);
        spinnerPaymentType.setSelection(0);
        switchRecurring.setChecked(false);
    }

    public void setExpenseDate(View view) {
        mYear = dateHelper.getCurrYear();
        mMonth = dateHelper.getCurrMonth();
        mDay = dateHelper.getCurrDay();

        if(!editDate.getText().toString().isEmpty()) {
            DateHelper selectedDateHelper = new DateHelper(editDate.getText().toString(), "/");
            mYear = selectedDateHelper.getGivenYear();
            mMonth = selectedDateHelper.getGivenMonth();
            mDay = selectedDateHelper.getGivenDay();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateHelper changeDateHelper = new DateHelper(year, month, dayOfMonth);
                editDate.setText(changeDateHelper.getGivenDateInDisplayFormat());
                selectedDate = changeDateHelper.getGivenDateInStoreFormat();
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(id > 0) {
            if (parent.getId() == R.id.spinner_category) {
                CategoryItem item = (CategoryItem) parent.getSelectedItem();
                selectedCategory = item.getSpinnerItemName();
                Toast.makeText(parent.getContext(), item.getSpinnerItemName(), Toast.LENGTH_SHORT).show();
            } else if (parent.getId() == R.id.spinner_paymentType) {
                selectedPaymentType = parent.getItemAtPosition(position).toString();
            }
            //Toast.makeText(MainActivity.this, "CAT: " + selectedCategory + " | " + "PT: " + selectedPaymentType, Toast.LENGTH_LONG).show();
        } else {
            if (parent.getId() == R.id.spinner_category) {
                selectedCategory = "";
            } else if (parent.getId() == R.id.spinner_paymentType) {
                selectedPaymentType = "";
            }
        }

    }

    private User getAuthorizedUser() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        return new User(
                settings.getString("authusername", null),
                settings.getString("authuserfullname", null));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
