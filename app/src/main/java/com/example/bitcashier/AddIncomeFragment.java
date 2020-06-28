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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.helpers.PreferencesHelper;
import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.Income;
import com.example.bitcashier.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddIncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddIncomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText editAmount, editDate, editIncomeNotes;
    Button btnSelectIncomeDate, btnAddIncome;
    ListView incomeListView;

    String selectedDate = "";
    private int mYear, mMonth, mDay;
    DateHelper dateHelper;
    User authUser;

    public AddIncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddIncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddIncomeFragment newInstance(String param1, String param2) {
        AddIncomeFragment fragment = new AddIncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View addIncomeView = inflater.inflate(R.layout.fragment_add_income, container, false);

        dateHelper = new DateHelper();

        authUser = new PreferencesHelper(addIncomeView.getContext())
                .getAuthenticatedUser();

        editAmount = addIncomeView.findViewById(R.id.editText_incomeAmount);
        editDate = addIncomeView.findViewById(R.id.editText_incomeDate);
        editIncomeNotes = addIncomeView.findViewById(R.id.editText_incomeNotes);
        btnSelectIncomeDate = addIncomeView.findViewById(R.id.button_selectIncomeDate);
        btnAddIncome = addIncomeView.findViewById(R.id.button_addIncome);
        incomeListView = addIncomeView.findViewById(R.id.lv_incomeList);

        editDate.setText(dateHelper.getCurrDateInDisplayFormat());
        selectedDate = dateHelper.getCurrDateInStoreFormat();

        btnSelectIncomeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIncomeDate(v);
            }
        });

        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIncome(v);
            }
        });

        getUserIncomeData(addIncomeView);

        // Inflate the layout for this fragment
        return addIncomeView;
    }

    public void setIncomeDate(View view) {
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

    public void getUserIncomeData(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        ArrayList<Income> incomeList = expenseDB.getUserIncome(authUser.getUsername());
        ArrayAdapter<Income> incomeArrayAdapter = new ArrayAdapter<>(view.getContext(), R.layout.expense_row_item, incomeList);
        incomeListView.setAdapter(incomeArrayAdapter);

        if (incomeList.size() ==  0) {
            Toast.makeText(view.getContext(),"No income data added by user", Toast.LENGTH_LONG).show();
        }
    }

    public void addIncome(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        double amount = 0;
        String amountString = editAmount.getText().toString(),
                date = editDate.getText().toString(),
                notes = editIncomeNotes.getText().toString();

        if(!amountString.isEmpty() && !date.isEmpty()) {
            try {
                amount = Double.parseDouble(amountString);
                if(amount < 100000) {
                    Currency incomeAmountObj = new Currency(amount, authUser.getCurrency());
                    Income newUserIncome = new Income(incomeAmountObj.getEuroAmount(), selectedDate, notes, authUser.getUsername());
                    boolean isInserted =  expenseDB.insertUserIncome(newUserIncome);
                    if(isInserted) {
                        Toast.makeText(view.getContext(),
                                "Income added successfully",
                                Toast.LENGTH_LONG).show();

                        HomeFragment homeFragment = new HomeFragment();
                        FragmentManager manager = getParentFragmentManager();
                        manager.beginTransaction()
                                .replace(R.id.navHostFragment,homeFragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(view.getContext(),
                                "Income was not added due to errors",
                                Toast.LENGTH_LONG).show();
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
}