package com.example.bitcashier;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    FloatingActionButton fabAddExpense, fabAddIncome;;
    TextView tvUserName, tvTotalIncome, tvTotalExpense, tvTotalBalance;
    String userCurrencySymbol = "";

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        fabAddExpense = homeFragmentView.findViewById(R.id.fab_addExpense);
        fabAddIncome = homeFragmentView.findViewById(R.id.fab_addIncome);

        tvUserName = homeFragmentView.findViewById(R.id.tv_userName);
        tvTotalIncome = homeFragmentView.findViewById(R.id.tv_totalIncome);
        tvTotalExpense = homeFragmentView.findViewById(R.id.tv_totalExpense);
        tvTotalBalance = homeFragmentView.findViewById(R.id.tv_totalBalance);

        User authUser = getAuthorizedUser();
        tvUserName.setText("Hello, " + authUser.getFullName());

        DbHelper expenseDB = new DbHelper(homeFragmentView.getContext());
        double userTotalIncome = 0, userTotalExpense = 0, userTotalBalance = 0;
        userTotalIncome = expenseDB.getUserTotalIncome(authUser.getUsername());
        userTotalExpense = expenseDB.getUserTotalExpense(authUser.getUsername(),"");
        if(userTotalIncome > userTotalExpense) {
            userTotalBalance = userTotalIncome - userTotalExpense;
        }

        if(userTotalExpense > userTotalIncome) {
            showExpenseExceededDialog(homeFragmentView);
        }

        tvTotalIncome.setText(userCurrencySymbol + String.format("%.2f", userTotalIncome));
        tvTotalExpense.setText(userCurrencySymbol + String.format("%.2f", userTotalExpense));
        tvTotalBalance.setText(userCurrencySymbol + String.format("%.2f", userTotalBalance));

        fabAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAddIncomeFragment(v);
            }
        });

        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAddExpenseFragment(v);
            }
        });
        // Inflate the layout for this fragment
        return homeFragmentView;
    }

    public void loadAddIncomeFragment(View view) {
        AddIncomeFragment addIncomeFragment = new AddIncomeFragment();

        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.navHostFragment,addIncomeFragment)
                .addToBackStack(null)
                .commit();
    }

    public void loadAddExpenseFragment(View view) {
        AddExpenseFragment addExpenseFragment = new AddExpenseFragment();

        FragmentManager manager = getParentFragmentManager();
        manager.beginTransaction()
                .replace(R.id.navHostFragment,addExpenseFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showExpenseExceededDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
        dialog.setCancelable(false);
        dialog.setTitle("ALERT");
        dialog.setMessage("Expenses have exceeded your income!" );
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

    private User getAuthorizedUser() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String username = settings.getString("authusername", null);
        userCurrencySymbol = settings.getString(username+"-currencysymbol", "€");
        return new User(
                settings.getString("authusername", null),
                settings.getString("authuserfullname", null));
    }
}
