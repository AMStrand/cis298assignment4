package edu.kvcc.cis298.cis298assignment4;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by David Barnes on 11/3/2015.
 */
public class BeverageFragment extends Fragment {

    //String key that will be used to send data between fragments
    private static final String ARG_BEVERAGE_ID = "crime_id";

        // Request codes to verify intent:
    private static final int REQUEST_CONTACT = 0;
    private static final int REQUEST_EMAIL = 1;

    //private class level vars for the model properties
    private EditText mId;
    private EditText mName;
    private EditText mPack;
    private EditText mPrice;
    private CheckBox mActive;
    private Button mContactButton;
    private Button mEmailButton;

        // String array to hold the contact info:
    private String contactName = "";
    private String contactEmail = "";

    //Private var for storing the beverage that will be displayed with this fragment
    private Beverage mBeverage;

    //Public method to get a properly formatted version of this fragment
    public static BeverageFragment newInstance(String id) {
        //Make a bungle for fragment args
        Bundle args = new Bundle();
        //Put the args using the key defined above
        args.putString(ARG_BEVERAGE_ID, id);

        //Make the new fragment, attach the args, and return the fragment
        BeverageFragment fragment = new BeverageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //When created, get the beverage id from the fragment args.
        String beverageId = getArguments().getString(ARG_BEVERAGE_ID);
        //use the id to get the beverage from the singleton
        mBeverage = BeverageCollection.get(getActivity()).getBeverage(beverageId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Use the inflator to get the view from the layout
        View view = inflater.inflate(R.layout.fragment_beverage, container, false);

        //Get handles to the widget controls in the view
        mId = (EditText) view.findViewById(R.id.beverage_id);
        mName = (EditText) view.findViewById(R.id.beverage_name);
        mPack = (EditText) view.findViewById(R.id.beverage_pack);
        mPrice = (EditText) view.findViewById(R.id.beverage_price);
        mActive = (CheckBox) view.findViewById(R.id.beverage_active);

        //Set the widgets to the properties of the beverage
        mId.setText(mBeverage.getId());
        mId.setEnabled(false);
        mName.setText(mBeverage.getName());
        mPack.setText(mBeverage.getPack());
        mPrice.setText(Double.toString(mBeverage.getPrice()));
        mActive.setChecked(mBeverage.isActive());

        //Text changed listenter for the id. It will not be used since the id will be always be disabled.
        //It can be used later if we want to be able to edit the id.
        mId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setId(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the name. Updates the model as the name is changed
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Text listener for the Pack. Updates the model as the text is changed
        mPack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBeverage.setPack(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Text listener for the price. Updates the model as the text is typed.
        mPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //If the count of characters is greater than 0, we will update the model with the
                //parsed number that is input.
                if (count > 0) {
                    mBeverage.setPrice(Double.parseDouble(s.toString()));
                //else there is no text in the box and therefore can't be parsed. Just set the price to zero.
                } else {
                    mBeverage.setPrice(0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Set a checked changed listener on the checkbox
        mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeverage.setActive(isChecked);
            }
        });

            // Intent to pick a contact:
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

            // Wire up the contact button:
        mContactButton = (Button)view.findViewById(R.id.contact_button);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // Start the activity, sending the request code:
                 startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

            // Boolean for whether there is a default contacts app:
        boolean contactsBool;

            // Create a package manager:
        PackageManager packageManager = getActivity().getPackageManager();
            // If there is no default contacts app, disable the button and set the bool to false:
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactButton.setEnabled(false);
            contactsBool = false;
        } else {
                // Otherwise set the bool to true so email can still be sent:
            contactsBool = true;
        }

            // Wire up the email button:
        mEmailButton = (Button) view.findViewById(R.id.email_button);
        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("text/plain");

                emailIntent.putExtra(Intent.EXTRA_TEXT, getBeverageReport());
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_email_subject));

                emailIntent = Intent.createChooser(emailIntent, getString(R.string.send_report));

                startActivity(emailIntent);
            }
        });

            // If there is no email for the selected contact but they can still
            // choose a contact (have default app), the send email button is disabled:
        if (contactEmail == "" && !contactsBool) {
            mEmailButton.setEnabled(false);
        }

        //Lastly return the view with all of this stuff attached and set on it.
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // If the result is not RESULT_OK, just return:
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
            // Set the query fields in a string array:
        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Email.DATA
        };

            // If the request code is for the contact button...
        if (requestCode == REQUEST_CONTACT && data != null) {
                // Save the data:
            Uri contactUri = data.getData();
                // Set a cursor using the data:
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
                // Enter a try/catch in case of errors:
            try {
                    // If there is nothing in the cursor, just return:
                if (c.getCount() == 0) {
                    return;
                }
                    // Move the cursor to the first:
                c.moveToFirst();
                    // Save the contact name and email:
                contactName = c.getString(0);
                contactEmail = c.getString(1);
            } finally {
                    // Close the cursor:
                c.close();
            }
        }
    }

        // Private method to get the beverage report for the email:
    private String getBeverageReport() {
            // Create a string to hold the phrasing for whether the beverage is active:
        String activeString = "";
            // Set the string according to whether the beverage is active:
        if (mBeverage.isActive()) {
            activeString = getString(R.string.beverage_active_yes);
        } else {
            activeString = getString(R.string.beverage_active_no);
        }
            // Send in the proper arguments to fill the formatted report string:
        String report = getString(R.string.beverage_report,
                contactName,
                mBeverage.getId(),
                mBeverage.getName(),
                mBeverage.getPack(),
                mBeverage.getPrice(),
                activeString);
            // Return the report string:
        return report;
    }
}









