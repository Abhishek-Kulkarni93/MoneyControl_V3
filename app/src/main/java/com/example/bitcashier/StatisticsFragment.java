package com.example.bitcashier;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.CategoryExpense;
import com.example.bitcashier.models.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
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

    PieChart expensesPieChart;
    Spinner spinnerFilterOptions, spinnerMonth, spinnerYear;

    DbHelper expenseDB;
    DateHelper dateHelper;
    User authUser;
    String userCurrencySymbol = "", selectedFilterOption = "", selectedMonth = "", selectedMonthString = "", selectedYear = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View statisticsFragmentView = inflater.inflate(R.layout.fragment_statistics, container, false);
        expenseDB = new DbHelper(statisticsFragmentView.getContext());
        dateHelper = new DateHelper();
        authUser = getAuthorizedUser();

        expensesPieChart = statisticsFragmentView.findViewById(R.id.pie_expensesChart);
        expensesPieChart.getDescription().setEnabled(false);
        expensesPieChart.setDragDecelerationFrictionCoef(0.95f);
        expensesPieChart.setDrawRoundedSlices(true);
        expensesPieChart.setCenterText("Expenses ("+ userCurrencySymbol +")");
        expensesPieChart.animate();

        Legend l = expensesPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        expensesPieChart.setEntryLabelColor(Color.BLACK);

        spinnerFilterOptions = statisticsFragmentView.findViewById(R.id.sp_filterOptions);
        spinnerMonth = statisticsFragmentView.findViewById(R.id.sp_filterMonth);
        spinnerYear = statisticsFragmentView.findViewById(R.id.sp_filterYear);

        ArrayAdapter<CharSequence> filterOptionsAdapter = ArrayAdapter.createFromResource(statisticsFragmentView.getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        filterOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterOptions.setAdapter(filterOptionsAdapter);
        spinnerFilterOptions.setOnItemSelectedListener(this);

        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<String>(statisticsFragmentView.getContext(), android.R.layout.simple_list_item_1,
                DateHelper.MONTHS);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthsAdapter);
        spinnerMonth.setOnItemSelectedListener(this);

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(statisticsFragmentView.getContext(), android.R.layout.simple_list_item_1,
                DateHelper.yearsList(20));
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearsAdapter);
        spinnerYear.setOnItemSelectedListener(this);

        selectedMonth = DateHelper.appendZero(dateHelper.getCurrMonth()+1);
        selectedYear = String.valueOf(dateHelper.getCurrYear());
        selectedMonthString = DateHelper.MONTHS[dateHelper.getCurrMonth()+1];
        getAndSetPieChartData(statisticsFragmentView, "monthly");

        // Inflate the layout for this fragment
        return statisticsFragmentView;
    }

    public void getAndSetPieChartData(View view, String filterBy) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<CategoryExpense> expenseArrayList;

        if (filterBy.equals("monthly")) {
            expenseArrayList = expenseDB.getMonthlyCategoryExpenseData(
                    authUser.getUsername(), selectedMonth, selectedYear
            );
        } else {
            expenseArrayList = expenseDB.getYearlyCategoryExpenseData(
                    authUser.getUsername(), selectedYear
            );
        }

        if (expenseArrayList.size() > 0) {
            for (CategoryExpense categoryExpense : expenseArrayList) {
                pieEntries.add(new PieEntry((float) categoryExpense.getAmount(), categoryExpense.getCategory_name()));
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categories");
            pieDataSet.setSelectionShift(5f);

            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            pieDataSet.setColors(colors);

            PieData pieData = new PieData(pieDataSet);
            pieData.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return userCurrencySymbol + String.format("%.2f", value);
                }
            });
            pieData.setValueTextSize(14f);
            pieData.setValueTextColor(Color.BLACK);

            String centerText = "Expenses ("+ userCurrencySymbol +")\n("+ selectedMonthString +" - "+ selectedYear +")";
            if (filterBy.equals("yearly"))
                centerText = "Expenses ("+ userCurrencySymbol +")\n("+ selectedYear +")";

            expensesPieChart.setCenterText(new SpannableString( centerText ));
            expensesPieChart.animate();
            expensesPieChart.setData(pieData);
            expensesPieChart.invalidate();
        } else {
            if (filterBy.equals("monthly"))
                Toast.makeText(view.getContext(),
                    "No expenses found for "+ selectedMonthString +" "+ selectedYear,
                    Toast.LENGTH_LONG).show();
            else
                Toast.makeText(view.getContext(),
                        "No expenses found for "+ selectedYear,
                        Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(id > 0) {
            if (parent.getId() == R.id.sp_filterOptions) {
                selectedFilterOption = parent.getItemAtPosition(position).toString();
            } else if (parent.getId() == R.id.sp_filterMonth) {
                selectedMonthString = parent.getItemAtPosition(position).toString();
                selectedMonth = DateHelper.appendZero(
                        DateHelper.getMonthNumber(selectedMonthString)
                );
            }  else if (parent.getId() == R.id.sp_filterYear) {
                selectedYear = parent.getItemAtPosition(position).toString();
            }

            if (selectedFilterOption.equals("Category Expenses for a Year")) {
                getAndSetPieChartData(view, "yearly");
                spinnerMonth.setEnabled(false);
            } else if (selectedFilterOption.equals("Category Expenses for a Month")) {
                spinnerMonth.setEnabled(true);
                if (!selectedMonth.isEmpty())
                    getAndSetPieChartData(view, "monthly");
            }
        } else {
            if (parent.getId() == R.id.sp_filterOptions) {
                selectedFilterOption = "";
            } else if (parent.getId() == R.id.sp_filterMonth) {
                selectedMonth = "";
            }  else if (parent.getId() == R.id.sp_filterYear) {
                selectedYear = "";
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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