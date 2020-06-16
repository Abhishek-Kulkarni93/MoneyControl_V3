package com.example.bitcashier;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.Threshold;
import com.example.bitcashier.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThresholdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThresholdFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThresholdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThresholdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThresholdFragment newInstance(String param1, String param2) {
        ThresholdFragment fragment = new ThresholdFragment();
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

    Spinner spinnerCategory;
    TextView tvCurrentThreshold, tvCurrentExpense;
    EditText etThreshold;
    Button buttonEditThreshold;

    DbHelper expenseDB;
    User authUser;
    int currentThresholdId = 0;
    String selectedCategory = "", userCurrencySymbol = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View thresholdView = inflater.inflate(R.layout.fragment_threshold, container, false);

        expenseDB = new DbHelper(thresholdView.getContext());
        authUser = getAuthorizedUser();

        spinnerCategory = thresholdView.findViewById(R.id.spinner_selectThresholdCategory);
        tvCurrentThreshold = thresholdView.findViewById(R.id.tv_currentThreshold);
        tvCurrentExpense = thresholdView.findViewById(R.id.tv_currentExpense);
        etThreshold = thresholdView.findViewById(R.id.editText_threshold);
        buttonEditThreshold = thresholdView.findViewById(R.id.btn_setThreshold);

        ArrayList<String> categoriesList = expenseDB.getCategoryStringList();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(thresholdView.getContext(), android.R.layout.simple_list_item_1,
                categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(this);

        buttonEditThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedCategory.isEmpty()) {
                    editCategoryThresholdValue(v);
                } else {
                    Toast.makeText(v.getContext(),"Please select a category", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return thresholdView;
    }

    public void getCategoryThresholdAndExpenseValue(View view) {
        Threshold categoryThreshold = expenseDB.getThresholdValue(authUser.getUsername(), selectedCategory);
        currentThresholdId = categoryThreshold.getId();

        double categoryExpenseValue = expenseDB.getUserTotalExpense(authUser.getUsername(), selectedCategory);

        tvCurrentThreshold.setText(userCurrencySymbol+categoryThreshold.getThreshold_value());
        etThreshold.setText(Double.toString(categoryThreshold.getThreshold_value()));

        tvCurrentExpense.setText(userCurrencySymbol + String.format("%.2f", categoryExpenseValue));

        if(categoryThreshold.getThreshold_value() == 0 && categoryExpenseValue == 0) {
            Toast.makeText(view.getContext(),
                    "No expenses found and threshold value not set for "+ selectedCategory +" category",
                    Toast.LENGTH_LONG).show();
        } else {
            String toastMsg = "";
            if(categoryThreshold.getThreshold_value() == 0) {
                toastMsg = "Threshold value not set for "+ selectedCategory +" category";
            } else if(categoryExpenseValue == 0) {
                toastMsg = "No expenses found in "+ selectedCategory +" category";
            }
            Toast.makeText(view.getContext(), toastMsg, Toast.LENGTH_LONG).show();
        }
    }

    public void editCategoryThresholdValue(View view) {
        double newThresholdValue = Double.parseDouble(etThreshold.getText().toString());

        if(newThresholdValue < 100000) {
            Threshold editedThresholdData = new Threshold(currentThresholdId, authUser.getUsername(), selectedCategory, newThresholdValue);

            if(expenseDB.updateCategoryThresholdValue(editedThresholdData)) {
                Toast.makeText(view.getContext(),"Threshold updated successfully", Toast.LENGTH_LONG).show();
                getCategoryThresholdAndExpenseValue(view);
            } else {
                Toast.makeText(view.getContext(),"Threshold was not set due to errors", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),"Threshold cannot be greater than 5 digits", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvCurrentThreshold.setText(userCurrencySymbol+0.0);
        tvCurrentExpense.setText(userCurrencySymbol+0.0);
        selectedCategory = "";
        etThreshold.setText("");

        if(id > 0) {
            selectedCategory = parent.getItemAtPosition(position).toString();
            getCategoryThresholdAndExpenseValue(view);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private User getAuthorizedUser() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = settings.getString("authusername", null);
        userCurrencySymbol = settings.getString(username+"-currencysymbol", "€");
        return new User(
                settings.getString("authusername", null),
                settings.getString("authuserfullname", null));
    }
}