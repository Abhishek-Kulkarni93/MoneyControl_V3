package com.example.bitcashier;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bitcashier.helpers.HelpAdapter;


public class HelpActivity extends AppCompatActivity {

    Toolbar helpToolbar;
    //TextView helptext;
    ListView helpFAQList;
    String [] questionAnswers = { "How do I change the currency of an expense" + "/n" + "   check", "Can I set a default currency?", "Can BitCashier do currency conversion between multiple currencies?",
            "What if BitCashier does not include my currency?","How do I use Bitcashier?","Why do I have to add contacts while I add expense? Can I add a friend without an email address or phone number?",
            "How do I export my transactions to a spreadsheet or Excel?","Help, I can not log in to my account!",
            "Is there a Bitcashier app for Windows Phone?","Why is not my password working?","How do I create a recurring expense?","How do I Update an expense?",
            "How do I Delete an expense?","How can I delete a recurring transaction?","Where can I view the monthly or yearly expenses?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        helpToolbar = findViewById(R.id.help_toolbar);
        helpFAQList = findViewById(R.id.help_listView);
        HelpAdapter helpAdapter= new HelpAdapter(HelpActivity.this,questionAnswers);
        helpFAQList.setAdapter(helpAdapter);
    }
}
