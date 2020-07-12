package com.example.bitcashier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutAppActivity extends AppCompatActivity {

    TextView aboutHome,aboutSidenav,aboutExpense,aboutIncome,aboutTransaction,aboutThreshold,aboutStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        aboutHome =(TextView)findViewById(R.id.about_home);
        aboutSidenav =(TextView)findViewById(R.id.about_sidenavigation);
        aboutExpense =(TextView)findViewById(R.id.about_addexpense);
        aboutIncome =(TextView)findViewById(R.id.about_addincome);
        aboutTransaction =(TextView)findViewById(R.id.about_transactionhistory);
        aboutThreshold =(TextView)findViewById(R.id.about_threshold);
        aboutStatistics = (TextView)findViewById(R.id.about_statistics);

    }
}