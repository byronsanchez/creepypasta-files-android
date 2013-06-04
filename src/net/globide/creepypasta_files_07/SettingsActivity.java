/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Outputs a list of user-configurable settings to affect the user experience of
 * the application. Also provides buttons to access other parts of the
 * application, such as the shop or to view production credits (this is placed
 * here to prevent negative UX due to placing shop and credits somewhere where
 * the user can always see them. The settings place might be a good candidate as
 * it is not-interfering and consequently, more of an opt-in if you want to
 * access the credits or the shop).
 */

public class SettingsActivity extends Activity implements OnClickListener,
        OnItemSelectedListener {

    // Define the possible text sizes
    private int[] mTextSizeArray = {
            12, // xsmall
            14, // small
            16, // normal
            18, // large
            22, // xlarge
    };

    // SharedPreferences persistent data storage properties
    private SharedPreferences mSharedPreferences;
    private Editor mEditor;

    // SharedPreferences storage properties
    private static String sFilename = "creepypasta_files_settings";

    // Define views used for event handling.
    private Spinner mSpSettingsTextSize;
    private ImageButton mIbSettingsShop;
    private ImageButton mIbSettingsCredits;

    // Tablet vs. phone boolean. Defaults to phone.
    private boolean isTablet = false;
    private boolean isSmall = false;
    private boolean isNormal = false;
    private boolean isLarge = false;
    private boolean isExtraLarge = false;

    /**
     * Implements onCreate().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_settings);

        // Determine whether or not the current device is a tablet.
        isTablet = getResources().getBoolean(R.bool.isTablet);
        isSmall = getResources().getBoolean(R.bool.isSmall);
        isNormal = getResources().getBoolean(R.bool.isNormal);
        isLarge = getResources().getBoolean(R.bool.isLarge);
        isExtraLarge = getResources().getBoolean(R.bool.isExtraLarge);

        // If the current device is a tablet, increase the font sizes
        if (isTablet) {
            for (int i = 0; i < mTextSizeArray.length; i++) {
                mTextSizeArray[i] += 4;
            }
        }

        // Get stored preferences, if any.
        mSharedPreferences = getSharedPreferences(sFilename, 0);
        // Setup the editor in this function, so it can be used anywhere else if
        // needed.
        mEditor = mSharedPreferences.edit();

        // Attach the defined views to their corresponding resource ids.
        mSpSettingsTextSize = (Spinner) findViewById(R.id.spSettingsTextSize);
        mIbSettingsShop = (ImageButton) findViewById(R.id.ibSettingsShop);
        mIbSettingsCredits = (ImageButton) findViewById(R.id.ibSettingsCredits);

        // Give the spinner display some padding.
        // spSettingsTextSize.setPadding(20, 20, 20, 20);

        ArrayList<String> spinnerArray = new ArrayList<String>();

        // Add the text size selection items.
        spinnerArray.add("Extra Small");
        spinnerArray.add("Small");
        spinnerArray.add("Normal");
        spinnerArray.add("Large");
        spinnerArray.add("Extra Large");

        // Setup an ArrayAdapter for our spinner view.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, spinnerArray) {
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    Context mContext = this.getContext();
                    LayoutInflater vi = (LayoutInflater) mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row, null);
                }

                TextView tv = (TextView) v.findViewById(R.id.spinnerTarget);
                tv.setTextColor(Color.WHITE);

                if (isTablet) {
                    if (isSmall) {
                        tv.setPadding(12, 12, 12, 12);
                    }
                    else if (isNormal) {
                        tv.setPadding(15, 15, 15, 15);
                    }
                    else if (isLarge) {
                        tv.setPadding(18, 18, 18, 18);
                    }
                    else if (isExtraLarge) {
                        tv.setPadding(20, 20, 20, 20);
                    }
                } else {
                    tv.setPadding(8, 8, 8, 8);
                }

                tv.setBackgroundColor(Color.BLACK);

                switch (position) {
                    case 0:

                        tv.setText("Extra Small");
                        tv.setTextSize(mTextSizeArray[0]);
                        break;

                    case 1:
                        tv.setText("Small");
                        tv.setTextSize(mTextSizeArray[1]);
                        break;

                    case 2:
                        tv.setText("Normal");
                        tv.setTextSize(mTextSizeArray[2]);
                        break;

                    case 3:
                        tv.setText("Large");
                        tv.setTextSize(mTextSizeArray[3]);
                        break;

                    case 4:
                        tv.setText("Extra Large");
                        tv.setTextSize(mTextSizeArray[4]);
                        break;
                }

                return v;
            }
        };

        // Add the adapter to the spinner.
        mSpSettingsTextSize.setAdapter(adapter);

        // Set listeners for our input views.
        mSpSettingsTextSize.setOnItemSelectedListener(this);
        mIbSettingsShop.setOnClickListener(this);
        mIbSettingsCredits.setOnClickListener(this);

        // Update output based on stored preferences, if any.
        updateView();
    }

    /**
     * Updates the text preview based on text configuration preferences.
     */
    private void updateView() {
        // Get the stored preferences.
        mSharedPreferences = getSharedPreferences(sFilename, 0);

        int spinnerDefaultItem = mSharedPreferences.getInt("spinnerDefaultItem", 2);

        // Update spinner default selection view. This is needed for the initial
        // loading
        // of this view. In manual selections, it's redundant.
        mSpSettingsTextSize.setSelection(spinnerDefaultItem);
    }

    /**
     * Implements onClick().
     */
    @Override
    public void onClick(View v) {
        // Setup an intent variable for potential new activity intents.
        Intent i;
        // Setup the string to contain the action name for an activity, should
        // the user trigger a new activity event.
        String intentClass = "";
        // Create a Class variable which will contain the Activity class we
        // intend to trigger.
        Class<?> selectedClass = null;

        switch (v.getId()) {
        // Shop Activity button.
            case R.id.ibSettingsShop:
                intentClass = "ShopActivity";
                break;
            // Credits Activity button.
            case R.id.ibSettingsCredits:
                intentClass = "CreditsActivity";
                break;
        }
        // If intentClass is NOT empty due to an activity button being
        // clicked...
        if (intentClass != "") {
            try {
                // Create a Class variable containing the Activity class we
                // intend to trigger.
                selectedClass = Class
                        .forName("net.globide.creepypasta_files_07." + intentClass);
            } catch (ClassNotFoundException e) {
                // If, for some STRANGE reason, the class is one that does not
                // exist, launch the ShopActivity. This will be considered the
                // default case.
                intentClass = "ShopActivity";
                // Create a Class variable containing the Activity class we
                // intend to trigger.
                try {
                    selectedClass = Class
                            .forName("net.globide.creepypasta_files_07." + intentClass);
                } catch (ClassNotFoundException e2) {
                    // If the default class does not exist, something is wrong
                    // with the installation of the program. Crash.
                    throw new RuntimeException(
                            "Application installation is corrupt. Please reinstall the application.");
                }
            } finally {
                if (selectedClass != null) {
                    // Create a new intent based on the selected class.
                    i = new Intent(this, selectedClass);
                    // Start the activity.
                    startActivity(i);
                }
            }
        }
    }

    /**
     * Implements onItemSelected().
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long is) {
        // Set the textSize preference based on selected spSettingsTextSize view
        // position.
        mEditor.putInt("textSize", position);
        mEditor.putInt("spinnerDefaultItem", position);
        mEditor.commit();

        // Update the preview based on the new preference.
        updateView();
    }

    /**
     * Implements onNothingSelected().
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}
