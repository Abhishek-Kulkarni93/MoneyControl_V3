package com.example.bitcashier;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.bitcashier.helpers.PreferencesHelper;
import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    FloatingActionButton fabAddExpense, fabAddIncome;;
    TextView tvUserName, tvTotalIncome, tvTotalExpense, tvTotalBalance;
    PieChart overviewPieChart;

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

        overviewPieChart = homeFragmentView.findViewById(R.id.pie_overviewChart);
        overviewPieChart.getDescription().setEnabled(false);
        overviewPieChart.setDragDecelerationFrictionCoef(0.95f);
        overviewPieChart.setDrawRoundedSlices(true);
        overviewPieChart.setCenterText("Overview");

        Legend l = overviewPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        overviewPieChart.setEntryLabelColor(Color.BLACK);

        PreferencesHelper prefsHelper = new PreferencesHelper(homeFragmentView.getContext());
        User authUser = prefsHelper.getAuthenticatedUser();
        userCurrencySymbol = prefsHelper.getAuthUserCurrencySymbol();

        tvUserName.setText("Hello, " + authUser.getFullName());

        DbHelper expenseDB = new DbHelper(homeFragmentView.getContext());
        double userTotalBalance = 0;

        double userTotalIncome = expenseDB.getUserTotalIncome(authUser.getUsername());
        double userTotalExpense = expenseDB.getUserTotalExpense(authUser.getUsername(),"");

        if(userTotalIncome > userTotalExpense) {
            userTotalBalance = userTotalIncome - userTotalExpense;
        }

        if(userTotalExpense > userTotalIncome) {
            showExpenseExceededDialog(homeFragmentView);
        }

        Currency totalIncomeObj = new Currency(userTotalIncome, authUser.getCurrency());
        Currency totalExpenseObj = new Currency(userTotalExpense, authUser.getCurrency());
        Currency totalBalanceObj = new Currency(userTotalBalance, authUser.getCurrency());

        tvTotalIncome.setText(userCurrencySymbol + String.format("%.2f", totalIncomeObj.getOtherAmount()));
        tvTotalExpense.setText(userCurrencySymbol + String.format("%.2f", totalExpenseObj.getOtherAmount()));
        tvTotalBalance.setText(userCurrencySymbol + String.format("%.2f", totalBalanceObj.getOtherAmount()));

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

        ArrayList<PieEntry> overviewData = new ArrayList<>();
        overviewData.add(new PieEntry((float) totalIncomeObj.getOtherAmount(), "Income"));
        overviewData.add(new PieEntry((float) totalExpenseObj.getOtherAmount(), "Expense"));
        overviewData.add(new PieEntry((float) totalBalanceObj.getOtherAmount(), "Balance"));

        PieDataSet pieDataSet = new PieDataSet(overviewData, "Overview");
        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return userCurrencySymbol + String.format("%.2f", value);
            }
        });
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.BLACK);

        overviewPieChart.setData(pieData);
        overviewPieChart.animate();

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

}
