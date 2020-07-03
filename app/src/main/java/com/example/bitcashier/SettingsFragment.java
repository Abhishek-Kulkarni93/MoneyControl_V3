package com.example.bitcashier;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.helpers.PreferencesHelper;
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
    Button btnHelp, btnAbout;
    ListView settingsListView;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        PreferencesHelper prefsHelper = new PreferencesHelper(settingsView.getContext());
        authUser = prefsHelper.getAuthenticatedUser();
        userCurrencySymbol = prefsHelper.getAuthUserCurrencySymbol();

        expenseDB = new DbHelper(settingsView.getContext());

        String currentUserCurrency = Currency.getCurrencySpinnerValue(authUser.getCurrency());

        spinnerChangeCurrency = settingsView.findViewById(R.id.spinner_changeCurrency);
        imageBtnChangeCurrency = settingsView.findViewById(R.id.button_changeCurrency);
        btnHelp =settingsView.findViewById(R.id.btn_settingsHelp);
        btnAbout=settingsView.findViewById(R.id.btn_settingsAbout);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickHelp();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAbout();
            }
        });

        //settingsListView = settingsView.findViewById(R.id.settings_listView);

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

//        ArrayAdapter<String> settingAdapter =  new ArrayAdapter<String>(settingsView.getContext() ,android.R.layout.simple_list_item_1,
//                getResources().getStringArray(R.array.settings_fragment));
//        settingsListView.setAdapter(settingAdapter);
//        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent= new Intent(settingsListView.getContext(), HelpActivity.class);
//                intent.putExtra("HELP", settingsListView.getItemIdAtPosition(position)).toString();
//                //intent.putExtra("About", settingsListView.getItemIdAtPosition(1));
//                startActivity(intent);
//            }
//        });

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

    public void onClickHelp (){
        //startActivity(new Intent(getApplicationContext(), HelpActivity.class));
        Intent intent = new Intent(btnHelp.getContext(),HelpActivity.class);
        startActivity(intent);
    }
    public void onClickAbout (){
        //startActivity(new Intent(getApplicationContext(), HelpActivity.class));
        Intent intent = new Intent(btnAbout.getContext(),AboutAppActivity.class);
        startActivity(intent);
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

}
