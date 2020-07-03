package com.example.bitcashier;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bitcashier.helpers.HelpAdapter;


public class HelpActivity extends AppCompatActivity {

    Toolbar helpToolbar;
    //TextView helptext;
    ListView helpFAQList;
    String [] questionAnswers = { "CURRENCY \n \n " ,
            "How do I change the currency of an expense \n\n Go to the settings → Change the currency from the drop-down and confirm by selecting on the ✓  \n If you'd like to use a currency not currently on the list, just contact support and we'd be happy to add it for you :)" ,
            "Can I set a default currency?\n\n " + "Yes! While you sign up, you will be asked for a default currency. \n If required, default currency can be changed later from the settings.\n",
            "Can BitCashier do currency conversion between multiple currencies? \n\n  Yes, we are at present working on the feature. Will include the steps soon.",
            "What if BitCashier does not include my currency? \n\n We'd be happy to add it for you! Just email us at support@bitcoders.com and let us know which currency you'd like to use – we're usually able to add it to our website and apps within a day or two.\n",
            "GENERAL \n",
            "How do I use Bitcashier?  \n\n Bitcashier is an app for money control. It lets you add various transactions and keep track of your budget, and then it helps you to plan your expenditure accordingly. Here's a quick overview of how it works.\n" +
                    "\n" +
                    "First, sign up for an account!\n" +
                    "\n" +
                    "Next, Add your income sources in add income page. \n" +
                    "\n" +
                    "Then hit the \"Add expense\" button to add all your transactions. You'll be asked for various details about your expense, like the category to which the transaction belong, total cost, title, payment type, date , recurring or not and recurring notes if any. As soon as you hit \"Add expense\", your transaction details will be added\n" +
                    "\n" +
                    "Later, after you've added a bunch of expenses, you'll may probably need to edit your transactions or repeat a certain transaction. You can do that by going to transaction history → select the transaction → update or delete expense as per your need.\n" +
                    "Else to repeat a transaction go to recurring transactions → select the transaction → click repeat expense.\n" +
                    "\n" +
                    "Those are the basics, and should be enough to get you started. If you have more questions, check out the rest of our helpdesk articles!\n",
            "Why do I have to add contacts while I add expense? Can I add a friend without an email address or phone number? \n\n The add contacts feature is only available for friend category, which can be used for scenarios like money lent to friend X. So, you can add the contact of X while adding the expense.\n" +
                    "\n" +
                    "When you select the contact X it will take whatever details you have saved while adding contact for X, it can be email or phone number.\n",
            "How do I export my transactions to a spreadsheet or Excel? \n\n" +
                    "BY Clicking the EXPORT DATA button on transaction History page.\n",
            "Help, I can not log in to my account! \n\n" +
                    "Make sure you're using the correct username – Sometimes people sign up for Bitcashier with one username, then try to sign in later using a different username. If Bitcashier tells you that it can't find an account for your username, it may be because you signed up with a different username, or because you spelled your username slightly differently.\n",
             "MOBILE \n\n",
            "Is there a Bitcashier app for Windows Phone? \n\n No, it is only supported on Android phones",
            "Why is not my password working?\n\n Make sure you're using the correct password – Sometimes people sign up for Bitcashier with one password, then try to sign in later using a different password. If Bitcashier tells you that your password is invalid, it may be because you signed up with a different password, or because you spelled your password slight differently.",
            "ALL ARTICLES \n\n",
            "How do I create a recurring expense? \n\n To add a new expense as recurring, select the recurring transaction button while adding an expense.\n" +
                    "Else, to repeat an existing transaction go to recurring transactions page → select the transaction → click repeat expense.\n",
            "How do I Update an expense? \n\n To add a new expense as recurring, select the recurring transaction button while adding an expense.\n" +
                    "Else, to repeat an existing transaction go to recurring transactions page → select the transaction → click repeat expense.\n",
            "How do I Delete an expense? \n \n" +
                    "To delete your transactions, Go to transaction history → select the transaction → Click delete expense → Select yes in the confirmation pop up.\n",
            "How can I delete a recurring transaction? \n\n To delete your recurring transactions, Go to transaction history → select the recurring transaction → Click delete expense → Select yes in the confirmation pop up.",
            "Where can I view the monthly or yearly expenses? \n \n" +
                    "To view the monthly or yearly expenses. \n" +
                    "Goto transaction history → set the required filters → click search history.\n" +
                    "Go to statistics → select a filter option → select month and year → Data will be visible in form of charts\n"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Intent intent= getIntent();
        //String header= (String) getIntent().getExtras().get("HELP");
        String header = intent.getStringExtra("HELP");
        helpToolbar = findViewById(R.id.help_toolbar);
        //helpToolbar.setTitle(header);



        helpFAQList = findViewById(R.id.help_listView);
        HelpAdapter helpAdapter= new HelpAdapter(HelpActivity.this,questionAnswers);
        helpFAQList.setAdapter(helpAdapter);
    }
}
