/*
 * Copyright (c) 2013 Byron Sanchez (hackbytes.com)
 * www.chompix.com
 *
 * Licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.globide.creepypasta_files_07;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Displays the default menu screen.
 */

public class MainActivity extends Activity implements OnClickListener {

    // Define the database access object.
    private NodeDatabase mDbNodeHelper = null;
    private Context mContext;

    /**
     * Implements onCreate(). If a database does not yet exist, this function
     * will automatically copy a pre-populated database located in this
     * application's assets folder.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;
        // Initialize a LoadViewTask object and call the execute method.
        // new LoadViewTask().execute();

        setContentView(R.layout.activity_main);

        // Database check!

        // Probe the database in case an upgrade or installation is necessary.
        mDbNodeHelper = new NodeDatabase((MainActivity) mContext);
        mDbNodeHelper.createDatabase();
        mDbNodeHelper.close();

        // Attach the views to their corresponding resource ids.
        Button bMainBrowse = (Button) findViewById(R.id.bMainBrowse);
        Button bMainBookmarks = (Button) findViewById(R.id.bMainBookmarks);
        ImageButton ibMainSettings = (ImageButton) findViewById(R.id.ibMainSettings);
        ImageButton ibMainHelp = (ImageButton) findViewById(R.id.ibMainHelp);

        bMainBrowse.setOnClickListener((MainActivity) mContext);
        bMainBookmarks.setOnClickListener((MainActivity) mContext);
        ibMainSettings.setOnClickListener((MainActivity) mContext);
        ibMainHelp.setOnClickListener((MainActivity) mContext);
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
        // Store the activity name in the intentClass string, corresponding to
        // the button pressed.
            case R.id.bMainBrowse:
                intentClass = "BrowseActivity";
                break;

            case R.id.bMainBookmarks:
                intentClass = "BookmarksActivity";
                break;

            case R.id.ibMainSettings:
                intentClass = "SettingsActivity";
                break;

            case R.id.ibMainHelp:
                intentClass = "HelpActivity";
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
                // exist, launch the BrowseActivity. This will be considered the
                // default case.
                intentClass = "BrowseActivity";
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
     * Implements onBackPressed().
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
