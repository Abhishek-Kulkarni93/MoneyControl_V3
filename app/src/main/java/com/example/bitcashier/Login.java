package com.example.bitcashier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.Category;
import com.example.bitcashier.models.Currency;
import com.example.bitcashier.models.User;

public class Login extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnLogin;

    public Login(){
        //empty constructor
    }

    @Override
    public  void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.edit_usernamelogin);
        editPassword = findViewById(R.id.edit_passwordlogin);
        btnLogin = findViewById(R.id.button_login);

    }

    public void btnSignUp(View view) {

        startActivity(new Intent(getApplicationContext(), SignUp.class));

    }

    public void validateUserEntries(View view) {
        String userName = editUsername.getText().toString();
        String password = editPassword.getText().toString();

//        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        if(!userName.isEmpty() && !password.isEmpty()){
            verifyUserCredentials(view, userName, password);
        } else {
            Toast.makeText(view.getContext(), "Please enter all the details", Toast.LENGTH_LONG).show();
        }
    }

    public void verifyUserCredentials(View view, String userName, String password) {
        DbHelper expenseDB = new DbHelper(view.getContext());

        if(expenseDB.checkUserNameExists(userName)) {
            User authorizedUser = expenseDB.verifyUserCredentials(userName);

            if (authorizedUser.getPassword().equals(password)) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("authusername", authorizedUser.getUsername());
                editor.putString("authuserfullname", authorizedUser.getFullName());
                editor.putString("authusercurrencycode", authorizedUser.getCurrency());
                editor.putString("authusercurrencysymbol", Currency.getCurrencySymbol(authorizedUser.getCurrency()));
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(view.getContext(),
                        "Invalid user name or password",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(),
                    "Account with this "+ userName +" username does not exist. Please sign up",
                    Toast.LENGTH_LONG).show();
        }
    }

}
