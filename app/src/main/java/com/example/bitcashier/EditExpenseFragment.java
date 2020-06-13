package com.example.bitcashier;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Expense;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditExpenseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = Expense.ID;
    private static final String ARG_PARAM2 = "REPEAT_MODE";

    // TODO: Rename and change types of parameters
    private int id;
    private String repeat_mode;

    public EditExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param repeat_mode Parameter 2.
     * @return A new instance of fragment EditExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditExpenseFragment newInstance(String id, String repeat_mode) {
        EditExpenseFragment fragment = new EditExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, repeat_mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = Integer.parseInt(getArguments().getString(ARG_PARAM1));
            repeat_mode = getArguments().getString(ARG_PARAM2);
        }
    }

    EditText editAmount, editTitle, editDate, editComment;
    Button buttonEditDate, buttonEditData, buttonDeleteData;
    ImageButton btnAddNewCategory;
    Spinner spinnerCategory, spinnerPaymentType;
    Switch switchRecurring;

    String selectedDate = "", selectedCategory = "", selectedPaymentType = "";
    private int mYear, mMonth, mDay;
    DateHelper dateHelper;
    Expense selectedExpense;
    ArrayAdapter<String> categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_expense, container, false);

        dateHelper = new DateHelper();
        final DbHelper expenseDB = new DbHelper(view.getContext());

        editAmount = view.findViewById(R.id.editText_editAmount);
        editTitle = view.findViewById(R.id.editText_editTitle);
        editDate = view.findViewById(R.id.editText_editDate);
        editComment = view.findViewById(R.id.editText_editComment);
        switchRecurring = view.findViewById(R.id.switch_editRecurring);
        buttonEditDate = view.findViewById(R.id.button_editDate);
        buttonEditData = view.findViewById(R.id.button_editData);
        btnAddNewCategory = view.findViewById(R.id.btn_addNewCategory);
        buttonDeleteData = view.findViewById(R.id.button_deleteData);
        spinnerCategory = view.findViewById(R.id.spinner_editCategory);
        spinnerPaymentType = view.findViewById(R.id.spinner_editPaymentType);

        editDate.setText(dateHelper.getCurrDateInDisplayFormat());
        selectedDate = dateHelper.getCurrDateInStoreFormat();

        ArrayList<String> categoriesList = expenseDB.getCategories();
        categoryAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1,
                categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> paymentTypeAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.payment_type, android.R.layout.simple_spinner_item);
        paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentType.setAdapter(paymentTypeAdapter);
        spinnerPaymentType.setOnItemSelectedListener(this);

        // TODO: User name to be added
        selectedExpense = getExpenseData(view);

        editAmount.setText(Double.toString(selectedExpense.getAmount()));
        editTitle.setText(selectedExpense.getTitle());
        editDate.setText(selectedExpense.getDateInDisplayFormat());
        editComment.setText(selectedExpense.getNotes());
        switchRecurring.setChecked((selectedExpense.getRecurring().equals("yes")));
        spinnerCategory.setSelection(categoryAdapter.getPosition(selectedExpense.getCategory()));
        spinnerPaymentType.setSelection(paymentTypeAdapter.getPosition(selectedExpense.getPayment_type()));
        selectedCategory = selectedExpense.getCategory();
        selectedPaymentType = selectedExpense.getPayment_type();
        selectedDate = selectedExpense.getDate();

        if(repeat_mode.equals("yes")) {
            buttonEditData.setText("Repeat Expense");
            editAmount.setKeyListener(null);
            editAmount.setFocusable(false);
            editAmount.setTextColor(Color.GRAY);
            editTitle.setKeyListener(null);
            editTitle.setFocusable(false);
            editTitle.setTextColor(Color.GRAY);
            spinnerCategory.setEnabled(false);
            spinnerPaymentType.setEnabled(false);
            switchRecurring.setClickable(false);
            buttonDeleteData.setVisibility(View.INVISIBLE);
            btnAddNewCategory.setVisibility(View.INVISIBLE);
        }

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

        buttonEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpenseDate(v);
            }
        });

        buttonEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeat_mode.equals("no")) {
                    editExpense(v);
                } else if(repeat_mode.equals("yes")) {
                    addRecurringExpense(v);
                }

            }
        });

        buttonDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View clickView = v;
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setCancelable(false);
                dialog.setTitle("Delete Expense");
                dialog.setMessage("Are you sure you want to delete this expense?" );
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Delete".
                        deleteExpense(clickView);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public Expense getExpenseData(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        return expenseDB.getExpenseById(id);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(id > 0) {
            if (parent.getId() == R.id.spinner_editCategory) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            } else if (parent.getId() == R.id.spinner_editPaymentType) {
                selectedPaymentType = parent.getItemAtPosition(position).toString();
            }
            //Toast.makeText(MainActivity.this, "CAT: " + selectedCategory + " | " + "PT: " + selectedPaymentType, Toast.LENGTH_LONG).show();
        } else {
            if (parent.getId() == R.id.spinner_editCategory) {
                selectedCategory = "";
            } else if (parent.getId() == R.id.spinner_editPaymentType) {
                selectedPaymentType = "";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addNewCategory(View view, String newCategoryValue) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        Category newCategory = new Category(newCategoryValue);
        boolean isInserted =  expenseDB.insertNewCategory(newCategory);
        if(isInserted) {
            Toast.makeText(view.getContext(),
                    newCategoryValue + " category added successfully",
                    Toast.LENGTH_LONG).show();
            categoryAdapter.add(newCategoryValue);
            categoryAdapter.notifyDataSetChanged();
            spinnerCategory.setSelection(categoryAdapter.getPosition(newCategoryValue));
        } else {
            Toast.makeText(view.getContext(),
                    newCategoryValue + " category was not added due to errors",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void addRecurringExpense(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        double amount = 0;
        String amountString = editAmount.getText().toString(),
                title = editTitle.getText().toString(),
                date = editDate.getText().toString(),
                comment = editComment.getText().toString(),
                recurring = (switchRecurring.isChecked()) ? "yes" : "no";

        if(!amountString.isEmpty() && !date.isEmpty() && !selectedCategory.isEmpty() && !selectedPaymentType.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
                // TODO: User name to be added
                Expense newExpense = new Expense(amount, title, selectedDate, selectedCategory, selectedPaymentType, comment, recurring, selectedExpense.getUserName());
                boolean isInserted =  expenseDB.insertData(newExpense);
                if(isInserted) {
                    Toast.makeText(view.getContext(),"Expense repeated successfully", Toast.LENGTH_LONG).show();
                    Log.println(Log.INFO, "ADDDATA", "Data Inserted");

                    RecurringTransactionsFragment recurringFragment = new RecurringTransactionsFragment();
                    FragmentManager manager = getParentFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.navHostFragment,recurringFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(view.getContext(),"Expense was not repeated due to errors", Toast.LENGTH_LONG).show();
                    Log.println(Log.INFO, "ADDDATA", "Data not inserted");
                }
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(),"Please enter only numbers", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),"Please enter all details", Toast.LENGTH_LONG).show();
        }
    }

    public void editExpense(View view) {

        DbHelper expenseDB = new DbHelper(view.getContext());

        double amount = 0;
        String amountString = editAmount.getText().toString(),
                title = editTitle.getText().toString(),
                date = editDate.getText().toString(),
                comment = editComment.getText().toString(),
                recurring = (switchRecurring.isChecked()) ? "yes" : "no";

        if(!amountString.isEmpty() && !date.isEmpty() && !selectedCategory.isEmpty() && !selectedPaymentType.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
                Expense existingExpense = new Expense(amount, title, selectedDate, selectedCategory, selectedPaymentType, comment, recurring, selectedExpense.getUserName());
                existingExpense.setId(id);
                boolean isUpdated =  expenseDB.updateExpenseData(existingExpense);
                if(isUpdated) {
                    Toast.makeText(view.getContext(),"Expense updated successfully", Toast.LENGTH_LONG).show();
                    returnToTransactionHistory();
                } else {
                    Toast.makeText(view.getContext(),"Expense was not updated due to errors", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(),"Please enter only numbers", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),"Please enter all details", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteExpense(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        if(expenseDB.deleteExpenseData(id)) {
            Toast.makeText(view.getContext(),"Expense deleted successfully", Toast.LENGTH_LONG).show();
            returnToTransactionHistory();
        } else {
            Toast.makeText(view.getContext(),"Expense was not deleted due to errors", Toast.LENGTH_LONG).show();
        }
    }

    public void returnToTransactionHistory() {
        TransactionHistoryFragment transactionHistoryFragment = new TransactionHistoryFragment();
        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.navHostFragment,transactionHistoryFragment)
                .addToBackStack(null)
                .commit();
    }
}