package com.example.bitcashier;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bitcashier.helpers.CategoryItem;
import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.CategoryExpense;
import com.example.bitcashier.models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
    BarChart expensesBarChart;
    Spinner spinnerFilterOptions, spinnerMonth, spinnerYear;

    DbHelper expenseDB;
    User authUser;
    String userCurrencySymbol = "", selectedFilterOption = "", selectedMonth = "", selectedYear = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View statisticsFragmentView = inflater.inflate(R.layout.fragment_statistics, container, false);
        expenseDB = new DbHelper(statisticsFragmentView.getContext());
        authUser = getAuthorizedUser();

        expensesPieChart = statisticsFragmentView.findViewById(R.id.pie_expensesChart);
//        expensesBarChart = statisticsFragmentView.findViewById(R.id.bar_expensesChart);
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

        // Inflate the layout for this fragment
        return statisticsFragmentView;
    }

    public ArrayList<PieEntry> pieChartDataSet() {
        ArrayList<PieEntry> dataSet = new ArrayList<>();
        ArrayList<CategoryExpense> expenseArrayList = expenseDB.getCategoryWiseExpenseData(
                authUser.getUsername(), selectedMonth, selectedYear
        );

        for (CategoryExpense categoryExpense : expenseArrayList) {
            dataSet.add(new PieEntry((float) categoryExpense.getAmount(), categoryExpense.getCategory_name()));
        }

        return dataSet;
    }

    public void buildPieChart(ArrayList<PieEntry> pieChartDataSet) {
        PieDataSet pieDataSet = new PieDataSet(pieChartDataSet, "Expenses Data");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueLineColor(R.color.colorAccent);
        pieDataSet.setValueTextSize(14f);

        PieData pieData = new PieData(pieDataSet);

        expensesPieChart.setData(pieData);
        expensesPieChart.getDescription().setEnabled(false);
        expensesPieChart.setCenterText("Expenses ("+ userCurrencySymbol +")");
        expensesPieChart.invalidate();
        expensesPieChart.animate();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(id > 0) {
            if (parent.getId() == R.id.sp_filterOptions) {
                selectedFilterOption = parent.getItemAtPosition(position).toString();
            } else if (parent.getId() == R.id.sp_filterMonth) {
                selectedMonth = DateHelper.appendZero(
                        DateHelper.getMonthNumber(parent.getItemAtPosition(position).toString())
                );
            }  else if (parent.getId() == R.id.sp_filterYear) {
                selectedYear = parent.getItemAtPosition(position).toString();
            }

            if (selectedFilterOption.equals("Category Expenses for a Month") && !selectedMonth.isEmpty() && !selectedYear.isEmpty()) {
                ArrayList<PieEntry> pieEntryArrayList = pieChartDataSet();
                buildPieChart(pieEntryArrayList);
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