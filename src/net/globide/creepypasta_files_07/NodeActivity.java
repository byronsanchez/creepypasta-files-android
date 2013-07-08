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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

// TODO: remove or keep
import android.graphics.Color;
import android.util.Log;

/**
 * Outputs a single node for a user to read. This includes the title and the
 * body. This activity also allows a user to access a modal that lets a user
 * change node display settings. These settings are also configurable from the
 * Settings Activity.
 */
public class NodeActivity extends Activity implements OnClickListener {

    // Define the array of possible text sizes for the node body.
    private int[] mTextSizeArray = {
            12, // extra small
            14, // small
            16, // normal
            18, // large
            22, // extra large
    };

    // Define the SharedPreferences persistent preference storage properties.
    private SharedPreferences mSharedPreferences;

    // Define a string containing the filename for our preferences.
    private static String sFilename = "creepypasta_files_settings";

    // Define our small db API object for database interaction.
    private NodeDatabase mDbNodeHelper = null;
    // Define a long number that we will receive from Browse Activity when a
    // user selects a menu item to read.
    private long mId;
    // Define a node object that will store the node data from the database.
    private Node mNodeData;
    // Define a variable object that will store the variable data from the
    // database.
    private Variable[] mVariableData;

    // Define necessary view properties.
    private TextView mTvNodeTitle;
    private PinchToZoomWebView mWvNodeBody;
    private ToggleButton mTbNodeBookmark;
    private ImageButton mIbNodeSettings;
    private AdView mAdView;

    // Tablet vs. phone boolean. Defaults to phone.
    private boolean isTablet = false;

    /**
     * Implements onCreate(). Loads preferences if they exist and sets up the
     * activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to full screen mode.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_node);

        // Determine whether or not the current device is a tablet.
        isTablet = getResources().getBoolean(R.bool.isTablet);

        // If the current device is a tablet, increase the font sizes
        if (isTablet) {
            for (int i = 0; i < mTextSizeArray.length; i++) {
                mTextSizeArray[i] += 4;
            }
        }

        /*
         * Application.
         */

        // Get stored preferences, if any.
        mSharedPreferences = getSharedPreferences(sFilename, 0);

        // Create our database object so we can communicate with the db.
        mDbNodeHelper = new NodeDatabase(this);

        // Call the createDatabase() function...just in case for some weird
        // reason the database does not yet exist. Otherwise, it will load our
        // database for use.
        mDbNodeHelper.createDatabase();

        // Setup a bundle and store any data passed from the activity
        // (BrowseActivity) that invoked this NodeActivity. The data passed
        // should be the id of the story the user wants to read.
        Bundle extras = getIntent().getExtras();
        mId = (long) extras.getInt("id");

        // Attach the views to their corresponding resource ids.
        mTbNodeBookmark = (ToggleButton) findViewById(R.id.tbNodeBookmark);
        mTvNodeTitle = (TextView) findViewById(R.id.tvNodeTitle);
        mIbNodeSettings = (ImageButton) findViewById(R.id.ibNodeSettings);
        mWvNodeBody = (PinchToZoomWebView) findViewById(R.id.wvNodeBody);

        // Set any necessary event listeners.
        mIbNodeSettings.setOnClickListener(this);
        mTbNodeBookmark.setOnClickListener(this);

        // Query the database for a node containing the id which was passed
        // from BrowseActivity.
        mNodeData = mDbNodeHelper.getNodeData(mId);
        // Get variable data in case ads are off.
        mVariableData = mDbNodeHelper.getVariableListData();
        // Define whether or not a bookmark is currently set for the node.
        long result = 0;
        result = mDbNodeHelper.getNodeIsBookmarked(mId);

        // Set the initial bookmark button state based on the result
        if (result == 0) {
          mTbNodeBookmark.setChecked(false);
        }
        else {
          mTbNodeBookmark.setChecked(true);
        }

        // Close the database.
        mDbNodeHelper.close();

        // Populate the text fields with corresponding data from the SQLite
        // database.
        mTvNodeTitle.setText(mNodeData.title);
        String encoding = "utf-8";
        String mime = "text/html";
        mWvNodeBody.getSettings().setJavaScriptEnabled(true);
        mWvNodeBody.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        mWvNodeBody.getSettings().setPluginsEnabled(true);
        mWvNodeBody.setBackgroundColor( Color.parseColor("#000000") );
        mWvNodeBody.setHorizontalScrollBarEnabled(true); 
        mWvNodeBody.setVerticalScrollBarEnabled(true); 
        mWvNodeBody.setScrollBarStyle(PinchToZoomWebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // For 2.0 and 2.1 - Double tap won't work without this.
        mWvNodeBody.getSettings().setUseWideViewPort(true);
        String nodeURI="file:///android_asset/raw/";

        // TODO: Consider moving the screen dimension generation code
        // elsewhere if images become common place in the node. ALSO add
        // zoom functionality.

        // Get the screen width and height.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;

        mWvNodeBody.loadDataWithBaseURL(nodeURI, "<html><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=10.0, user-scalable=yes\" /><style type=\"text/css\"> img {width: " + width + ";}</style><body bgcolor=\"#000000\" text=\"#C4C4C4\">" + mNodeData.body + "</body></html>", mime, encoding, "");

        /*
         * AdMob.
         */
        for (Variable variable : mVariableData) {
            if (variable.sku.equals("001_no_ads")) {
                if (variable.value.equals("0")) {
                    AdRequest adRequest = new AdRequest();
                    adRequest.addTestDevice(AdRequest.TEST_EMULATOR);

                    mAdView = (AdView) findViewById(R.id.avNodeBottom);
                    mAdView.loadAd(adRequest);
                }
            }
        }
    }

    // TODO: See if there is a way to center the image.

    /**
     * Draws images to the screen when html is parsed.
     */
    private ImageGetter imgGetter = new ImageGetter() {

        public Drawable getDrawable(String source) {
            Log.d("NodeActivity", "IMAGEGETTER INVOKED");
            Drawable drawable = null;

            // Get the resource by string instead of the usual integer id.
            // This is because we are given a string by the override; this
            // string is the img src, which we are exclusively using as a
            // resource identifier.
            drawable = getResources().getDrawable(
                    getResources().getIdentifier(source, "raw", getPackageName()));

            // TODO: Consider moving the screen dimension generation code
            // elsewhere if images become common place in the node. ALSO add
            // zoom functionality.

            // Get the screen width and height.
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            float screenWidth = dm.widthPixels;
            // Get the image width and height.
            float imageWidth = drawable.getIntrinsicWidth();
            float imageHeight = drawable.getIntrinsicHeight();
            // Define variables to store the image bounds.
            int width = 0;
            int height = 0;

            // Resize the image if the image width exceeds the screen width.
            if (imageWidth >= screenWidth) {
                // If we need to resize the image because the width is too big,
                // get the resize ratio when reducing size to the screen width.
                float resizeRatio = screenWidth / imageWidth;
                // Calculate the new width and height values according to this
                // ratio.
                width = (int) (imageWidth * resizeRatio);
                height = (int) (imageHeight * resizeRatio);
            } else {
                // Else if the image width is NOT bigger than the screen size
                // then the image size remains the same.
                width = (int) imageWidth;
                height = (int) imageHeight;
            }

            // Set the image bounds.
            drawable.setBounds(0, 0, width, height);

            // Return the image to be drawn to the screen.
            return drawable;
        }
    };

    /**
     * Implements onResume(). This will be triggered when the activity first
     * starts and any time the settings activity is called and returned from
     * within this NodeActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Update output based on stored preferences, if any.
        updateView();
    }

    /**
     * Updates the node display based on text configuration preferences.
     */
    private void updateView() {
        // Get shared preferences, if they exist. Else, use the default value.
        mSharedPreferences = getSharedPreferences(sFilename, 0);

        // Get the textSize preference and use it as a key to the textSize
        // array.
        int textSizeKey = mSharedPreferences.getInt("textSize", 2);
        int textSize = mTextSizeArray[textSizeKey];

        // Update the node body's text size.
        mWvNodeBody.getSettings().setDefaultFontSize(textSize);
    }

    /**
     * Implements onClick().
     */
    @Override
    public void onClick(View view) {
        // Setup an intent variable for potential new activity intents.
        Intent i;
        // Setup the string to contain the action name for an activity, should
        // the user trigger a new activity event.
        String intentClass = "";
        // Create a Class variable which will contain the Activity class we
        // intend to trigger.
        Class<?> selectedClass = null;

        switch (view.getId()) {
        // Bookmark Button
            case R.id.tbNodeBookmark:

                // Define a boolean integer to contain whether or not we should
                // bookmark the current node.
                int isBookmarked = 0;

                String resultText = "";

                // The toggle button does the switch for us before invoking
                // onClick, so we just need to react to the toggle event.
                if ( mTbNodeBookmark.isChecked() ) {
                    isBookmarked = 1;
                    mTbNodeBookmark.setChecked(true);
                    resultText = "Bookmark has been added.";
                } else {
                    // Else remove it.
                    isBookmarked = 0;
                    mTbNodeBookmark.setChecked(false);
                    resultText = "Bookmark has been removed.";
                }

                // Create our database object so we can communicate with the db.
                mDbNodeHelper = new NodeDatabase(this);

                // Call the createDatabase() function...just in case for some
                // weird reason the database does not yet exist. Otherwise, it
                // will load our database for use.
                mDbNodeHelper.createDatabase();

                // Query the database to set/remove a bookmark for the current
                // node.
                mDbNodeHelper.updateNode(mId, "is_bookmarked", isBookmarked);

                // Close the database.
                mDbNodeHelper.close();

                // Display a toast confirming what just happened.
                Toast.makeText(this, resultText, Toast.LENGTH_SHORT).show();

                break;

            // Settings Button
            case R.id.ibNodeSettings:
                // Display a quick settings dialog displaying only critical
                // style
                // preferences.
                intentClass = "SettingsActivity";

                // If intentClass is NOT empty due to an activity button being
                // clicked...
                if (intentClass != "") {
                    try {
                        // Create a Class variable containing the Activity class
                        // we intend to
                        // trigger.
                        selectedClass = Class
                                .forName("net.globide.creepypasta_files_07." + intentClass);
                    } catch (ClassNotFoundException e) {
                        // If, for some STRANGE reason, the class is one that
                        // does not exist, try relaunching the SettingsActivity
                        intentClass = "SettingsActivity";
                        // Create a Class variable containing the Activity class
                        // we intend to trigger.
                        try {
                            selectedClass = Class
                                    .forName("net.globide.creepypasta_files_07." + intentClass);
                        } catch (ClassNotFoundException e2) {
                            // If the default class does not exist, something is
                            // wrong with the installation of the program.
                            // Crash.
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

                    break;

                }
        }
    }

    /**
     * Implements onBackPressed().
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kill the activity.
        finish();
    }

    /**
     * Implements onDestroy().
     */
    @Override
    public void onDestroy() {
        // When this activity is killed, make sure to implement proper garbage
        // collection.

        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
