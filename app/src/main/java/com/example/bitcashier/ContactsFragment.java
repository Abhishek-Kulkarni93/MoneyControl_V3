package com.example.bitcashier;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "CALLED_FROM";
    private static final String ARG_PARAM2 = "EXPENSE_ID";
    private static final String TAG = "ContactsFragment";

    View contactsFragmentView;
    ListView lvContacts;
    ArrayAdapter<String> contactsAdapter;
    EditText editSearchContacts;

    // TODO: Rename and change types of parameters
    private String called_from, expense_id;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param calledFrom Parameter 1.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String calledFrom, String expenseId) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, calledFrom);
        args.putString(ARG_PARAM2, expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            called_from = getArguments().getString(ARG_PARAM1);
            expense_id = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactsFragmentView = inflater.inflate(R.layout.fragment_contacts, container, false);

        lvContacts = contactsFragmentView.findViewById(R.id.contactsList);
        lvContacts.setAdapter(contactsAdapter);
        editSearchContacts = contactsFragmentView.findViewById(R.id.editText_searchContacts);

//        Toast.makeText(contactsFragmentView.getContext(),"Expense ID: " + expense_id, Toast.LENGTH_LONG).show();

        editSearchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0 || count >= 3) {
                    getContacts(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedContact = parent.getAdapter().getItem(position).toString();
            String[] splitContact = selectedContact.split(" \\| ");

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            if (called_from.equals("AddExpense")) {
                transaction
                        .replace(R.id.navHostFragment, AddExpenseFragment.newInstance(splitContact[0]))
                        .addToBackStack(null)
                        .commit();
            } else if (called_from.equals("EditExpense")) {
                transaction
                        .replace(R.id.navHostFragment, EditExpenseFragment.newInstanceFromContact(
                                expense_id, "no", splitContact[0]))
                        .addToBackStack(null)
                        .commit();
            }
            }
        });

        getContacts("");

        // Inflate the layout for this fragment
        return contactsFragmentView;
    }

    public void getContacts(String searchQuery) {
        ArrayList<String> contactList = new ArrayList<>();

        String selection = null;
        String[] selectionArgs = null;
        if (!searchQuery.isEmpty()) {
            selection = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?";
            selectionArgs = new String[]{"%" + searchQuery + "%"};
        }

        String contactsSortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";
        Cursor contactCursor = getActivity().getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                contactsSortOrder
        );

        if(contactCursor.getCount() > 0) {
            while (contactCursor.moveToNext()) {
                String completeContactString = "";
                String contactId = "", contactName = "", phoneNumber = "";

                contactId = contactCursor.getString(contactCursor.getColumnIndex(
                        ContactsContract.Contacts._ID
                ));
                contactName = contactCursor.getString(contactCursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                ));

                int hasPhoneNumber = Integer.parseInt(
                        contactCursor.getString(contactCursor.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                        ))
                );

                if(hasPhoneNumber > 0) {
                    Cursor phoneNumberCursor = getActivity().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null
                    );

                    if(phoneNumberCursor.moveToNext()) {
                        phoneNumber = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                        ));
                    }

                    phoneNumberCursor.close();
                }

                completeContactString = contactName;
                if (!phoneNumber.isEmpty())
                    completeContactString += " | " + phoneNumber;

                contactList.add(completeContactString);
            }
        }
        contactCursor.close();

        contactsAdapter = new ArrayAdapter<>(contactsFragmentView.getContext(),
                android.R.layout.simple_list_item_1,contactList);
        lvContacts.setAdapter(contactsAdapter);

    }

}