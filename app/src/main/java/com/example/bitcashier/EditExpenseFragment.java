package com.example.bitcashier;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Spannable;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitcashier.helpers.CategoryAdapter;
import com.example.bitcashier.helpers.CategoryItem;
import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.Threshold;

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
    private static final String ARG_PARAM3 = "CONTACT_NAME";
    private static final String TAG = "EditExpenseFragment";

    // TODO: Rename and change types of parameters
    private int id;
    private String repeat_mode;
    private String contact_name;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param repeat_mode Parameter 2.
     * @param contact_name Parameter 3.
     * @return A new instance of fragment EditExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditExpenseFragment newInstanceFromContact(String id, String repeat_mode, String contact_name) {
        EditExpenseFragment fragment = new EditExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, repeat_mode);
        args.putString(ARG_PARAM3, contact_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = Integer.parseInt(getArguments().getString(ARG_PARAM1));
            repeat_mode = getArguments().getString(ARG_PARAM2);
            contact_name = (getArguments().getString(ARG_PARAM3) != null) ? getArguments().getString(ARG_PARAM3) : "";
        }
    }

    EditText editAmount, editTitle, editDate, editComment, editContact;
    Button buttonEditDate, buttonEditData, buttonDeleteData;
    ImageButton btnAddNewCategory, btnStyleNotes, btnOpenContacts;
    Spinner spinnerCategory, spinnerPaymentType;
    Switch switchRecurring;

    String selectedDate = "", selectedCategory = "", selectedPaymentType = "";
    DateHelper dateHelper;
    DbHelper expenseDB;
    Expense selectedExpense;
    CategoryAdapter customCategoryAdapter;
    String appPackageName;
    boolean isTextBold = false, isTextItalics = false, isTextUnderline = false;
    double categoryThresholdValue, userTotalExpenseForCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View editExpenseView = inflater.inflate(R.layout.fragment_edit_expense, container, false);

        dateHelper = new DateHelper();
        expenseDB = new DbHelper(editExpenseView.getContext());
        appPackageName = editExpenseView.getContext().getPackageName();
        selectedExpense = expenseDB.getExpenseById(id);
        ArrayList<Category> categoriesList = expenseDB.getCategories();

        editAmount = editExpenseView.findViewById(R.id.editText_editAmount);
        editTitle = editExpenseView.findViewById(R.id.editText_editTitle);
        editDate = editExpenseView.findViewById(R.id.editText_editDate);
        editComment = editExpenseView.findViewById(R.id.editText_editComment);
        switchRecurring = editExpenseView.findViewById(R.id.switch_editRecurring);
        buttonEditDate = editExpenseView.findViewById(R.id.button_editDate);
        buttonEditData = editExpenseView.findViewById(R.id.button_editData);
        btnAddNewCategory = editExpenseView.findViewById(R.id.btn_addNewCategory);
        btnStyleNotes = editExpenseView.findViewById(R.id.btn_styleNotes);
        buttonDeleteData = editExpenseView.findViewById(R.id.button_deleteData);
        spinnerCategory = editExpenseView.findViewById(R.id.spinner_editCategory);
        spinnerPaymentType = editExpenseView.findViewById(R.id.spinner_editPaymentType);

        editDate.setText(dateHelper.getCurrDateInDisplayFormat());
        selectedDate = dateHelper.getCurrDateInStoreFormat();

        btnOpenContacts = editExpenseView.findViewById(R.id.button_editOpenContacts);
        editContact = editExpenseView.findViewById(R.id.et_editSelectedContact);
        editContact.setEnabled(false);
        btnOpenContacts.setEnabled(false);

        ArrayList<CategoryItem> categoryItemsList = addIconsToCategoryList(editExpenseView, categoriesList);
        customCategoryAdapter = new CategoryAdapter(editExpenseView.getContext(), categoryItemsList);
        spinnerCategory.setAdapter(customCategoryAdapter);
        selectedCategory = selectedExpense.getCategory();
        int selectedCatIconId = editExpenseView.getContext().getResources()
                .getIdentifier(appPackageName+":drawable/ic_"+selectedCategory.toLowerCase(), null, null);
        CategoryItem selCategoryItem = new CategoryItem(selectedCatIconId, selectedCategory);
        Log.i(TAG, "IMG ID :: "+selectedCatIconId + " IMG NAME :: ic_"+selectedCategory.toLowerCase());
        Log.i(TAG, "ITEM POS :: "+customCategoryAdapter.getPosition(selCategoryItem));
        spinnerCategory.setSelection(customCategoryAdapter.getPosition(selCategoryItem), true);
        spinnerCategory.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> paymentTypeAdapter = ArrayAdapter.createFromResource(editExpenseView.getContext(),
                R.array.payment_type, android.R.layout.simple_spinner_item);
        paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentType.setAdapter(paymentTypeAdapter);
        spinnerPaymentType.setOnItemSelectedListener(this);

        editAmount.setText(Double.toString(selectedExpense.getAmount()));
        editTitle.setText(selectedExpense.getTitle());
        editDate.setText(selectedExpense.getDateInDisplayFormat());
        editComment.setText(selectedExpense.getNotes());
        switchRecurring.setChecked((selectedExpense.getRecurring().equals("yes")));
        spinnerPaymentType.setSelection(paymentTypeAdapter.getPosition(selectedExpense.getPayment_type()));

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
            editContact.setEnabled(false);
            btnOpenContacts.setEnabled(false);
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

        btnStyleNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStyleNotesDialog(v);
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

        btnOpenContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsFragment contactsFragment = ContactsFragment.newInstance(
                        "EditExpense", String.valueOf(selectedExpense.getId()));
                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.navHostFragment,contactsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        addSelectedCategoryToSpinner(editExpenseView);

        editContact.setText(selectedExpense.getContact_name());
        if(contact_name != null && !contact_name.isEmpty()) {
            editContact.setText(contact_name);
        }
        if(selectedExpense.getCategory().equals("Friend") && repeat_mode.equals("no")) {
            editContact.setEnabled(true);
            btnOpenContacts.setEnabled(true);
        }

        if(!selectedCategory.isEmpty() && repeat_mode.equals("no")) {
            getCategoryThresholdValue();
        }

        // Inflate the layout for this fragment
        return editExpenseView;
    }

    public ArrayList<CategoryItem> addIconsToCategoryList(View view, ArrayList<Category> categories) {
        ArrayList<CategoryItem> categoryItems = new ArrayList<>();

        for (int i=0; i<categories.size(); i++) {
            Category currentCategory = categories.get(i);
            if(!currentCategory.getCategoryName().equals(selectedCategory)) {
                int categoryImageId = view.getContext().getResources()
                        .getIdentifier(appPackageName+":drawable/"+currentCategory.getCategory_icon() , null, null);
//                Log.i(TAG, "addIconsToCategoryList: categoryImageId: " + categoryImageId + " name: " + currentCategory.getCategoryName() + " icon: " + currentCategory.getCategory_icon());
                categoryItems.add(new CategoryItem(categoryImageId, currentCategory.getCategoryName()));
            }
        }

        return categoryItems;
    }

    public void setExpenseDate(View view) {
        int mYear = dateHelper.getCurrYear();
        int mMonth = dateHelper.getCurrMonth();
        int mDay = dateHelper.getCurrDay();

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

    public void getCategoryThresholdValue() {
        Threshold categoryThreshold = expenseDB.getThresholdValue(selectedExpense.getUserName(), selectedCategory);
        categoryThresholdValue = categoryThreshold.getThreshold_value();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(id > 0) {
            if (parent.getId() == R.id.spinner_editCategory) {
                CategoryItem item = (CategoryItem) parent.getSelectedItem();
                selectedCategory = item.getSpinnerItemName();

                if(selectedCategory.equals("Friend") && repeat_mode.equals("no")) {
                    editContact.setEnabled(true);
                    btnOpenContacts.setEnabled(true);
                } else {
                    editContact.setEnabled(false);
                    btnOpenContacts.setEnabled(false);
                }

                getCategoryThresholdValue();
            } else if (parent.getId() == R.id.spinner_editPaymentType) {
                selectedPaymentType = parent.getItemAtPosition(position).toString();
            }
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

    public void addSelectedCategoryToSpinner(View view) {
        int selectedCatIconId = view.getContext().getResources()
                .getIdentifier(appPackageName+":drawable/ic_"+selectedCategory.toLowerCase() , null, null);
        CategoryItem newCategoryItem = new CategoryItem(selectedCatIconId, selectedCategory);
        customCategoryAdapter.add(newCategoryItem);
        customCategoryAdapter.notifyDataSetChanged();
        spinnerCategory.setSelection(customCategoryAdapter.getPosition(newCategoryItem));
    }

    public void addNewCategory(View view, String newCategoryValue) {
        Category newCategory = new Category(newCategoryValue);
        int categoryImageId = view.getContext().getResources()
                .getIdentifier(appPackageName+":drawable/"+newCategory.getCategory_icon() , null, null);
        CategoryItem newCategoryItem = new CategoryItem(categoryImageId, newCategoryValue);
        boolean isInserted =  expenseDB.insertNewCategory(selectedExpense.getUserName(), newCategory);
        if(isInserted) {
            Toast.makeText(view.getContext(),
                    newCategoryValue + " category added successfully",
                    Toast.LENGTH_LONG).show();
            customCategoryAdapter.add(newCategoryItem);
            customCategoryAdapter.notifyDataSetChanged();
            spinnerCategory.setSelection(customCategoryAdapter.getPosition(newCategoryItem));
            selectedCategory = newCategoryValue;
        } else {
            Toast.makeText(view.getContext(),
                    newCategoryValue + " category was not added due to errors",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void showStyleNotesDialog(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        final View addStyledNotesView = getLayoutInflater().inflate(R.layout.dialog_styled_notes,null);

        // EditText is in the dialog box
        final EditText etAddNotes = addStyledNotesView.findViewById(R.id.editText_styledNotes);
        if(!editComment.getText().toString().isEmpty()) {
            etAddNotes.setText(editComment.getText().toString()); // this will yeeeeeeet!
        }

        Button btn_bold = addStyledNotesView.findViewById(R.id.btn_bold);
        Button btn_italics = addStyledNotesView.findViewById(R.id.btn_italics);
        Button btn_underline = addStyledNotesView.findViewById(R.id.btn_underline);
        Button btn_cancel = addStyledNotesView.findViewById(R.id.btn_notesCancel);
        Button btn_okay = addStyledNotesView.findViewById(R.id.btn_notesOkay);
        alert.setView(addStyledNotesView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);

        btn_bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText notesEditText = addStyledNotesView.findViewById(R.id.editText_styledNotes);
                Spannable str = notesEditText.getText();
                int textSelectionStart = 0, textSelectionEnd = str.length();
                if(notesEditText.getSelectionEnd() > notesEditText.getSelectionStart()) {
                    textSelectionStart = notesEditText.getSelectionStart();
                    textSelectionEnd = notesEditText.getSelectionEnd();
                }

                if(!isTextBold) {
                    str.setSpan(new StyleSpan(Typeface.BOLD),
                            textSelectionStart,
                            textSelectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    isTextBold = true;
                } else {
                    StyleSpan[] strStyleSpan = str.getSpans(textSelectionStart,textSelectionEnd, StyleSpan.class);
                    for (StyleSpan styleSpan : strStyleSpan) {
                        if (styleSpan.getStyle() == Typeface.BOLD) {
                            str.removeSpan(styleSpan);
                            isTextBold = false;
                        }
                    }
                }
            }
        });

        btn_italics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText notesEditText = addStyledNotesView.findViewById(R.id.editText_styledNotes);
                Spannable str = notesEditText.getText();
                int textSelectionStart = 0, textSelectionEnd = str.length();
                if(notesEditText.getSelectionEnd() > notesEditText.getSelectionStart()) {
                    textSelectionStart = notesEditText.getSelectionStart();
                    textSelectionEnd = notesEditText.getSelectionEnd();
                }

                if(!isTextItalics) {
                    str.setSpan(new StyleSpan(Typeface.ITALIC),
                            textSelectionStart,
                            textSelectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    isTextItalics = true;
                } else {
                    StyleSpan[] strStyleSpan = str.getSpans(textSelectionStart,textSelectionEnd, StyleSpan.class);
                    for (StyleSpan styleSpan : strStyleSpan) {
                        if (styleSpan.getStyle() == Typeface.ITALIC) {
                            str.removeSpan(styleSpan);
                            isTextItalics = false;
                        }
                    }
                }
            }
        });

        btn_underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText notesEditText = addStyledNotesView.findViewById(R.id.editText_styledNotes);
                Spannable str = notesEditText.getText();
                UnderlineSpan underlineSpan = new UnderlineSpan();
                int textSelectionStart = 0, textSelectionEnd = str.length();
                if(notesEditText.getSelectionEnd() > notesEditText.getSelectionStart()) {
                    textSelectionStart = notesEditText.getSelectionStart();
                    textSelectionEnd = notesEditText.getSelectionEnd();
                }

                if(!isTextUnderline) {
                    str.setSpan(underlineSpan,
                            textSelectionStart,
                            textSelectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    isTextUnderline = true;
                } else {
                    UnderlineSpan[] strStyleSpan = str.getSpans(textSelectionStart,textSelectionEnd, UnderlineSpan.class);
                    for (UnderlineSpan styleSpan : strStyleSpan) {
                        if (styleSpan.getSpanTypeId() == underlineSpan.getSpanTypeId()) {
                            str.removeSpan(styleSpan);
                            isTextUnderline = false;
                        }
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spannable comment = etAddNotes.getText();
                editComment.setText(comment);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public void addRecurringExpense(View view) {
        double amount = 0;
        String amountString = editAmount.getText().toString(),
                title = editTitle.getText().toString(),
                date = editDate.getText().toString(),
                comment = editComment.getText().toString(),
                recurring = (switchRecurring.isChecked()) ? "yes" : "no";

        String contactName = (selectedCategory.equals("Friend")) ? editContact.getText().toString() : "";

        if(!amountString.isEmpty() && !date.isEmpty() && !selectedCategory.isEmpty() && !selectedPaymentType.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
                Expense newExpense = new Expense(amount, title, selectedDate, selectedCategory, selectedPaymentType, comment, recurring, selectedExpense.getUserName(), contactName);
                boolean isInserted =  expenseDB.insertData(newExpense);
                if(isInserted) {
                    Toast.makeText(view.getContext(),"Expense repeated successfully", Toast.LENGTH_LONG).show();

                    RecurringTransactionsFragment recurringFragment = new RecurringTransactionsFragment();
                    FragmentManager manager = getParentFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.navHostFragment,recurringFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(view.getContext(),"Expense was not repeated due to errors", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(),"Please enter only numbers", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),"Please enter all details", Toast.LENGTH_LONG).show();
        }
    }

    public void editExpense(View view) {
        double amount = 0;
        String amountString = editAmount.getText().toString(),
                title = editTitle.getText().toString(),
                date = editDate.getText().toString(),
                comment = editComment.getText().toString(),
                recurring = (switchRecurring.isChecked()) ? "yes" : "no";

        String contactName = (selectedCategory.equals("Friend")) ? editContact.getText().toString() : "";

        if(!amountString.isEmpty() && !date.isEmpty() && !selectedCategory.isEmpty() && !selectedPaymentType.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
                if(amount < 100000) {
                    Expense existingExpense = new Expense(amount, title, selectedDate, selectedCategory, selectedPaymentType, comment, recurring, selectedExpense.getUserName(), contactName);
                    existingExpense.setId(id);
                    boolean isUpdated =  expenseDB.updateExpenseData(existingExpense);
                    if(isUpdated) {
                        Toast.makeText(view.getContext(),"Expense updated successfully", Toast.LENGTH_LONG).show();
                        userTotalExpenseForCategory = expenseDB.getUserTotalExpense(selectedExpense.getUserName(), selectedCategory);
                        if(categoryThresholdValue > 0 && userTotalExpenseForCategory > categoryThresholdValue) {
                            showThresholdDialog(view);
                        } else {
                            returnToTransactionHistory();
                        }
                    } else {
                        Toast.makeText(view.getContext(),"Expense was not updated due to errors", Toast.LENGTH_LONG).show();
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

    public void showThresholdDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
        dialog.setCancelable(false);
        dialog.setTitle("ALERT");
        dialog.setMessage("Threshold exceeded for this category!" );
        dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Okay".
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Action for "Cancel".
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    public void deleteExpense(View view) {
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