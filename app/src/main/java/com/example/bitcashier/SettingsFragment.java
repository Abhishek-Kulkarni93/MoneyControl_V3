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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Spinner spinnerChangeCurrency;
    ImageButton imageBtnChangeCurrency;
    String selectedCurrency = "", selectedCurrencySymbol = "", userCurrencySymbol = "";
    User authUser;
    DbHelper expenseDB;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        authUser = getAuthorizedUser();
        expenseDB = new DbHelper(settingsView.getContext());

        String currentUserCurrency = Currency.getCurrencySpinnerValue(authUser.getCurrency());

        spinnerChangeCurrency = settingsView.findViewById(R.id.spinner_changeCurrency);
        imageBtnChangeCurrency = settingsView.findViewById(R.id.button_changeCurrency);

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(settingsView.getContext(),
                R.array.currency, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChangeCurrency.setAdapter(currencyAdapter);
        spinnerChangeCurrency.setSelection(currencyAdapter.getPosition(currentUserCurrency));
        spinnerChangeCurrency.setOnItemSelectedListener(this);

        imageBtnChangeCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserCurrency();
            }
        });

        // Inflate the layout for this fragment
        return settingsView;
    }

    public void changeUserCurrency() {
        String selectedCurrencyCode = Currency.getCurrencyCode(selectedCurrency);
        if (expenseDB.updateUserCurrency(authUser.getUsername(), selectedCurrencyCode)) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("authusercurrencycode", selectedCurrencyCode);
            editor.putString("authusercurrencysymbol", selectedCurrencySymbol);
            editor.apply();

            HomeFragment homeFragment = new HomeFragment();
            FragmentManager manager = getParentFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.navHostFragment,homeFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(getContext(),
                    " User currency change not successful",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(id > 0) {
            selectedCurrency = parent.getItemAtPosition(position).toString();
            extractCurrencySymbol();
        } else {
            selectedCurrency = "";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void extractCurrencySymbol() {
        String regex = "\\p{Sc}";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectedCurrency);
        while (matcher.find())
        {
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            System.out.println(" : " + matcher.group());
            selectedCurrencySymbol = matcher.group();
        }
    }

    private User getAuthorizedUser() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userCurrencySymbol = settings.getString("authusercurrencysymbol", "€");
        return new User(
                settings.getString("authusername", null),
                settings.getString("authuserfullname", null),
                settings.getString("authusercurrencycode", "EUR"));
    }
}
