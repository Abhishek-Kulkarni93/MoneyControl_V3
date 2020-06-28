package com.example.bitcashier;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bitcashier.helpers.DateHelper;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.helpers.ExpenseArrayAdapter;
import com.example.bitcashier.helpers.PreferencesHelper;
import com.example.bitcashier.models.Expense;
import com.example.bitcashier.models.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecurringTransactionsFragment extends Fragment {

    ListView recurringExpenseListView;

    User authUser;

    public RecurringTransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recurringExpenseView = inflater.inflate(R.layout.fragment_recurring_transactions, container, false);

        recurringExpenseListView = recurringExpenseView.findViewById(R.id.lv_recurringExpensesList);

        recurringExpenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expense expense = (Expense) parent.getAdapter().getItem(position);

                EditExpenseFragment editExpenseFragment = EditExpenseFragment.newInstance(
                        String.valueOf(expense.getId()),"yes");

                FragmentManager manager = getParentFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.navHostFragment,editExpenseFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        authUser = new PreferencesHelper(recurringExpenseView.getContext())
                .getAuthenticatedUser();

        getRecurringExpenseData(recurringExpenseView);
        // Inflate the layout for this fragment
        return recurringExpenseView;
    }

    public void getRecurringExpenseData(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        ArrayList<Expense> expensesList = expenseDB.loadRecurringExpenses(authUser.getUsername());
//        ArrayAdapter<Expense> expenseAdapter = new ArrayAdapter<>(view.getContext(), R.layout.expense_row_item, expensesList);
        ExpenseArrayAdapter expenseAdapter = new ExpenseArrayAdapter(view.getContext(), R.layout.card_view_row_item, expensesList);
        recurringExpenseListView.setAdapter(expenseAdapter);

        if (expensesList.size() ==  0) {
            Toast.makeText(view.getContext(),"No recurring expenses found", Toast.LENGTH_LONG).show();
        }
    }
}
