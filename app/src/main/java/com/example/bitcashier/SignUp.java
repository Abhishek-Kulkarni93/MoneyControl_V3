package com.example.bitcashier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bitcashier.helpers.DbHelper;
import com.example.bitcashier.models.User;


public class SignUp extends AppCompatActivity {

    EditText editUserName, editPassword, editConfirmPassword, editFullName;
    Button buttonRegister;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editUserName = (EditText)findViewById(R.id.edit_username_signup);
        editFullName = (EditText)findViewById(R.id.edit_fullname_signup);
        editPassword = (EditText)findViewById(R.id.edit_password_signup);
        editConfirmPassword = (EditText)findViewById(R.id.edit_confirm_password_signup);
        buttonRegister = (Button)findViewById(R.id.btn_register_signup);

    }

    public void validateUserEntries(View view) {

        String userName = editUserName.getText().toString();
        String userFullName = editFullName.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if(!userName.isEmpty() && !userFullName.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
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
}
