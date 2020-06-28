package com.example.bitcashier;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.text.Spannable;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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
import com.example.bitcashier.helpers.PreferencesHelper;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.Threshold;
import com.example.bitcashier.models.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddExpenseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "CONTACT_NAME";
    private static final String TAG = "AddExpenseFragment";

    // TODO: Rename and change types of parameters
    private String contact_name;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contactName Parameter 1.
     * @return A new instance of fragment AddExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExpenseFragment newInstance(String contactName) {
        AddExpenseFragment fragment = new AddExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, contactName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contact_name = getArguments().getString(ARG_PARAM1);
        }
    }

    EditText editAmount, editTitle, editDate, editComment, editContact;
    Button btnSelectDate, btnAddData;
    ImageButton btnAddNewCategory, btnStyleNotes, btnOpenContacts;
    Spinner spinnerCategory, spinnerPaymentType;
    Switch switchRecurring;

    String selectedDate = "", selectedCategory = "", selectedPaymentType = "";
    DateHelper dateHelper;
    User authUser;
    DbHelper expenseDB;
    CategoryAdapter customCategoryAdapter;
    String appPackageName;
    boolean isTextBold = false, isTextItalics = false, isTextUnderline = false;
    double categoryThresholdValue, userTotalExpenseForCategory;

    public AddExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addExpenseView = inflater.inflate(R.layout.fragment_add_expense, container, false);

        authUser = new PreferencesHelper(addExpenseView.getContext())
                .getAuthenticatedUser();

        expenseDB = new DbHelper(addExpenseView.getContext());
        dateHelper = new DateHelper();
        appPackageName = addExpenseView.getContext().getPackageName();


        editAmount = addExpenseView.findViewById(R.id.editText_amount);
        editTitle = addExpenseView.findViewById(R.id.editText_title);
        editDate = addExpenseView.findViewById(R.id.editText_date);
        editComment = addExpenseView.findViewById(R.id.editText_comment);
        switchRecurring = addExpenseView.findViewById(R.id.switch_recurring);
        btnSelectDate = addExpenseView.findViewById(R.id.button_selectDate);
        btnAddData = addExpenseView.findViewById(R.id.button_addData);
        btnAddNewCategory = addExpenseView.findViewById(R.id.button_addNewCategory);
        btnStyleNotes = addExpenseView.findViewById(R.id.button_styleNotes);
        spinnerCategory = addExpenseView.findViewById(R.id.spinner_category);
        spinnerPaymentType = addExpenseView.findViewById(R.id.spinner_paymentType);

        editDate.setText(dateHelper.getCurrDateInDisplayFormat());
        selectedDate = dateHelper.getCurrDateInStoreFormat();

        btnOpenContacts = addExpenseView.findViewById(R.id.button_openContacts);
        editContact = addExpenseView.findViewById(R.id.editText_selectedContact);
        editContact.setEnabled(false);
        btnOpenContacts.setEnabled(false);

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
                final EditText etAddCategory = addCategoryView.findViewById(R.id.editText_category);
                Button btn_cancel = addCategoryView.findViewById(R.id.btn_cancel);
                Button btn_add = addCategoryView.findViewById(R.id.btn_add);
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

        btnOpenContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactsFragment contactsFragment = ContactsFragment.newInstance("AddExpense", "");
                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.navHostFragment,contactsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        if(contact_name != null && !contact_name.isEmpty()) {
            editContact.setText(contact_name);
            int selectedCatIconId = addExpenseView.getContext().getResources()
                    .getIdentifier(appPackageName+":drawable/ic_friend" , null, null);
            CategoryItem newCategoryItem = new CategoryItem(selectedCatIconId, "Friend");
            customCategoryAdapter.add(newCategoryItem);
            customCategoryAdapter.notifyDataSetChanged();
            spinnerCategory.setSelection(customCategoryAdapter.getPosition(newCategoryItem));
        }

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
        boolean isInserted =  expenseDB.insertNewCategory(authUser.getUsername(), newCategory);
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

    public void addExpense(View view) {
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
                    Currency expenseAmountObj = new Currency(amount, authUser.getCurrency());
                    Expense newExpense = new Expense(expenseAmountObj.getEuroAmount(), title, selectedDate, selectedCategory, selectedPaymentType, comment, recurring, authUser.getUsername(), contactName);
                    boolean isInserted =  expenseDB.insertData(newExpense);
                    if(isInserted) {
                        Toast.makeText(view.getContext(),"Expense added successfully", Toast.LENGTH_LONG).show();
                        userTotalExpenseForCategory = expenseDB.getUserTotalExpense(authUser.getUsername(), selectedCategory);

                        if(categoryThresholdValue > 0 && userTotalExpenseForCategory > categoryThresholdValue) {
                            showThresholdDialog(view);
                        } else {
                            HomeFragment homeFragment = new HomeFragment();
                            FragmentManager manager = getParentFragmentManager();
                            manager.beginTransaction()
                                    .replace(R.id.navHostFragment,homeFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
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

                if(selectedCategory.equals("Friend")) {
                    editContact.setEnabled(true);
                    btnOpenContacts.setEnabled(true);
                } else {
                    editContact.setEnabled(false);
                    btnOpenContacts.setEnabled(false);
                }

                getCategoryThresholdValue();
            } else if (parent.getId() == R.id.spinner_paymentType) {
                selectedPaymentType = parent.getItemAtPosition(position).toString();
            }
        } else {
            if (parent.getId() == R.id.spinner_category) {
                selectedCategory = "";
            } else if (parent.getId() == R.id.spinner_paymentType) {
                selectedPaymentType = "";
            }
        }
    }

    public void getCategoryThresholdValue() {
        Threshold categoryThreshold = expenseDB.getThresholdValue(authUser.getUsername(), selectedCategory);
        categoryThresholdValue = categoryThreshold.getThreshold_value();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
