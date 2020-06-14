package com.example.bitcashier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bitcashier.helpers.CategoryItem;
import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editUserName, editPassword, editConfirmPassword, editFullName;
    Button buttonRegister;
    Spinner currencySpinner;
    String selectedCurrency = "", selectedCurrencySymbol = "";

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editUserName = findViewById(R.id.edit_username_signup);
        editFullName = findViewById(R.id.edit_fullname_signup);
        editPassword = findViewById(R.id.edit_password_signup);
        editConfirmPassword = findViewById(R.id.edit_confirm_password_signup);
        buttonRegister = findViewById(R.id.btn_register_signup);
        currencySpinner = findViewById(R.id.spinner_currency);

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(SignUp.this,
                R.array.currency, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);
        currencySpinner.setOnItemSelectedListener(this);

    }

    public void extractCurrencySymbol(View view) {
        String regex = "\\p{Sc}";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectedCurrency);
        while (matcher.find())
        {
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            System.out.println(" : " + matcher.group());
            selectedCurrencySymbol = matcher.group();
//            Toast.makeText(view.getContext(), "Symbol: "+ matcher.group(), Toast.LENGTH_LONG).show();
        }
    }

    public void validateUserEntries(View view) {

        String userName = editUserName.getText().toString();
        String userFullName = editFullName.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if(!userName.isEmpty() && !userFullName.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !selectedCurrency.isEmpty()) {
            if (password.equals(confirmPassword)) {
                addUser(view);
            } else {
                Toast.makeText(view.getContext(), "Password(s) do not match", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(), "Please enter all the details", Toast.LENGTH_LONG).show();
        }

    }

    public void addUser(View view) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        String userName = editUserName.getText().toString();

        if(!expenseDB.checkUserNameExists(userName)) {
            String fullName = editFullName.getText().toString();
            String password = editPassword.getText().toString();

            User newUser = new User(userName, fullName, password);
            boolean isInserted =  expenseDB.insertNewUser(newUser);

            if(isInserted) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("authusername", userName);
                editor.putString("authuserfullname", fullName);
                editor.putString(userName+"-authusercurrency", selectedCurrency);
                editor.putString(userName+"-currencysymbol", selectedCurrencySymbol);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), AppInfo.class));
            } else {
                Toast.makeText(view.getContext(),
                        "User account not created due to errors",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),
                    userName + " already exists! Please choose another",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(id > 0) {
            selectedCurrency = parent.getItemAtPosition(position).toString();
            extractCurrencySymbol(view);
        } else {
            selectedCurrency = "";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
