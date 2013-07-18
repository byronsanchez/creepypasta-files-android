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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdSize;

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

    // Cached calculated values so we don't have to regenerate them if they are
    // needed again
    private float mScreenWidth;
    private float mScreenHeight;
    private float mScreenWidthPadded;
    private float mScreenHeightPadded;
    private float mScreenMin;

    // Define necessary view properties.
    private TextView mTvNodeTitle;
    private PinchToZoomWebView mWvNodeBody;
    private ToggleButton mTbNodeBookmark;
    private ImageButton mIbNodeSettings;
    private AdView mAdView;

    // Tablet vs. phone boolean. Defaults to phone.
    public static boolean sIsTablet = false;
    public static boolean sIsSmall = false;
    public static boolean sIsNormal = false;
    public static boolean sIsLarge = false;
    public static boolean sIsExtraLarge = false;

    private boolean mAreAdsEnabled = false;
    private boolean mIsJavaScriptLoaded = false;

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
        NodeActivity.sIsTablet = getResources().getBoolean(R.bool.isTablet);
        NodeActivity.sIsSmall = getResources().getBoolean(R.bool.isSmall);
        NodeActivity.sIsNormal = getResources().getBoolean(R.bool.isNormal);
        NodeActivity.sIsLarge = getResources().getBoolean(R.bool.isLarge);
        NodeActivity.sIsExtraLarge = getResources().getBoolean(R.bool.isExtraLarge);

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

        float padding = 0;
        if (NodeActivity.sIsTablet) {
            // If the current device is a tablet, increase the font sizes
            for (int i = 0; i < mTextSizeArray.length; i++) {
                mTextSizeArray[i] += 4;
            }

            if (NodeActivity.sIsSmall) {
                padding = 6;
            }
            else if (NodeActivity.sIsNormal) {
                padding = 8;
            }
            else if (NodeActivity.sIsLarge) {
                padding = 9;
            }
            else if (NodeActivity.sIsExtraLarge) {
                padding = 10;
            }
        } else {
            padding = 4;
        }

        // Calculate screen metrics
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mScreenWidthPadded = mScreenWidth - (padding * 2);
        mScreenHeightPadded = mScreenHeight - (padding * 2);
        mScreenMin = Math.min(mScreenWidthPadded, mScreenHeightPadded);

        // Render any custom tags if necessary.
        mNodeData.body = renderCustomTags(mNodeData.body);
        // Populate the text fields with corresponding data from the SQLite
        // database.
        mTvNodeTitle.setText(mNodeData.title);
        String encoding = "utf-8";
        String mime = "text/html";
        //mWvNodeBody.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        mWvNodeBody.setWebViewClient(new CustomWebViewClient(this));
        mWvNodeBody.getSettings().setSupportZoom(false);
        mWvNodeBody.getSettings().setBuiltInZoomControls(false);
        mWvNodeBody.getSettings().setJavaScriptEnabled(true);
        mWvNodeBody.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        if (Build.VERSION.SDK_INT < 8) {
            mWvNodeBody.getSettings().setPluginsEnabled(true);
        }
        else {
            mWvNodeBody.getSettings().setPluginState(PluginState.ON);
        }
        mWvNodeBody.setBackgroundColor( Color.parseColor("#000000") );
        mWvNodeBody.setHorizontalScrollBarEnabled(true); 
        mWvNodeBody.setVerticalScrollBarEnabled(true); 
        mWvNodeBody.setScrollBarStyle(PinchToZoomWebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // For 2.0 and 2.1 - Double tap won't work without this.
        mWvNodeBody.getSettings().setUseWideViewPort(true);
        String nodeURI = "file:///android_asset/raw/";
        String data = "<!DOCTYPE html>" +
                "    <head>" +
                "        <meta charset=\"utf-8\">" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=10.0, user-scalable=yes\">" +
                "        <link rel=\"stylesheet\"  href=\"file:///android_asset/css/nodeactivity.css\">" +
                "        <script src=\"file:///android_asset/js/nodeactivity.js\"></script>" +
                "        <style type=\"text/css\">" +
                "            body {" +
                "                font-family: \"%@\";" +
                "                margin: 0;" +
                "                padding: " + padding + "px;" +
                "            }" +
                "            img {" +
                "                width: " + mScreenMin + "px;" +
                "            }" +
                "        </style>" +
                "    </head>" +
                "    <body bgcolor=\"#000000\" text=\"#C4C4C4\">" +
                mNodeData.body +
                "    </body>" +
                "</html>";
        mWvNodeBody.loadDataWithBaseURL(nodeURI, data, mime, encoding, "");

        /*
         * AdMob.
         */
        for (Variable variable : mVariableData) {
            if (variable.sku.equals("001_no_ads")) {
                if (variable.value.equals("0")) {
                    mAreAdsEnabled = true;
                }
            }
        }
    }

    /**
     * Implements onConfigurationChanged.
     *
     * Handles configuration changes manually instead of restarting and
     * recreating the activity. In this case, this will help maintain
     * the webview scroll position. Consequently, we must dynamically
     * resize certain elements.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateView();
    }

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

        /**
         * Text size.
         */

        // Get shared preferences, if they exist. Else, use the default value.
        mSharedPreferences = getSharedPreferences(sFilename, 0);

        // Get the textSize preference and use it as a key to the textSize
        // array.
        int textSizeKey = mSharedPreferences.getInt("textSize", 2);
        int textSize = mTextSizeArray[textSizeKey];

        // Update the node body's text size.
        mWvNodeBody.getSettings().setDefaultFontSize(textSize);


        /**
         * Ads.
         */

        // Request a new ad to update the ad orientation if ads are enabled.

        if (mAreAdsEnabled) {
            mAdView = (AdView)findViewById(R.id.avNodeBottom);
            RelativeLayout parent = (RelativeLayout) mAdView.getParent();
            RelativeLayout.LayoutParams adViewParams = (RelativeLayout.LayoutParams) mAdView.getLayoutParams();

            parent.removeView(mAdView);
            mAdView = new AdView(this, AdSize.SMART_BANNER, getString(R.string.admob_pub_id));
            mAdView.setId(R.id.avNodeBottom);
            parent.addView(mAdView, adViewParams);

            AdRequest adRequest = new AdRequest();
            adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
            mAdView.loadAd(adRequest);
        }

        /**
         * JavaScript.
         */

        // Update the node body's javascript positioning.
        if (mIsJavaScriptLoaded) {
            updateJavaScript();
        }
    }

    /**
     * Updates the webview's javascript.
     */
    public void updateJavaScript() {
        /**
         * Video Embeds.
         */

        // Image updates only since video embeds get converted to image links.
        String javascript = "javascript:updateImages(" + mScreenMin + ");";
        mWvNodeBody.loadUrl(javascript);
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
                // style preferences.
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

    /**
     * Renders tags placed in the node and returns the entired body of the node, whether
     * it has been rendered or not.
     */
    public String renderCustomTags(String nodeString) {
        // Video tags
        String syntax = "\\[video\\s+?(.*?)(?:\\s+?\\|\\s+?(.*?))?\\]";
        Pattern pattern = Pattern.compile(syntax);
        Matcher matcher = pattern.matcher(nodeString);
        boolean isNameSet = false;

        while (matcher.find()) {
            String matchString = matcher.group(0);
            String source = matcher.group(1);
            String name = matcher.group(2);

            if (source != null && !(source.length() == 0)) {
                if (name != null && !(name.length() == 0)) {
                   isNameSet = true;
                }
                // Youtube regex.
                String syntaxYoutube = "(?:https?:\\/\\/)?(?:www\\.)?youtu(?:\\.be|be\\.com)\\/(?:watch\\?v=)?([\\w\\-]{10,})";
                Pattern patternYoutube = Pattern.compile(syntaxYoutube);
                Matcher matcherYoutube = patternYoutube.matcher(source);

                if (matcherYoutube.find()) {
                    String id = matcherYoutube.group(1);
                    // Match only the particular tag we are working on. This is
                    // because there may be multiple video tags per page.
                    String label = "[play video]";
                    // NAME and LABEL are two seperate placeholders.
                    String replacementString = "<div class=\"mobile-youtube\"><a class=\"image-link\" href=\"http://youtube.com/embed/VIDEOID\"><img src=\"http://i.ytimg.com/vi/VIDEOID/0.jpg\" ></a><br /><a href=\"http://youtube.com/embed/VIDEOID\" class=\"text-link\">NAMELABEL</a></div>";
                    replacementString = replacementString.replace("VIDEOID", id);
                    replacementString = replacementString.replace("LABEL", label);
                    if (isNameSet) {
                        replacementString = replacementString.replace("NAME", name + "<br />");
                    }
                    else {
                        replacementString = replacementString.replace("NAME", "");
                    }
                    nodeString = nodeString.replace(matchString, replacementString);
                }
            }
        }

        return nodeString;
    }

    /**
     * Whenever a youtube link is clicked, this class will launch the native
     * youtube app to view the video.
     */

    public class CustomWebViewClient extends WebViewClient {

        public Context mContext;

        /**
         * Constructor.
         *
         * Sets the calling activity as the context.
         */
        public CustomWebViewClient(Activity activity) {
            super();
            mContext = activity;
        }

        /**
         * Implements shouldOverrideUrlLoading().
         *
         * Overrides WebView browser loads. If the content being loaded comes from
         * a particular URL, we can change what happens. For this case, we are
         * going to launch the native youtube application for any youtube embeds.
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getHost().contains("youtube.com")) {
                String packageName = "com.google.android.youtube";
                try {
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (isApplicationInstalled(mContext, packageName)) {
                        viewIntent.setPackage(packageName);
                    }
                    mContext.startActivity(viewIntent);
                } catch (Exception e) {
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(viewIntent);
                }
                return true;
            }

            return false;
        }

        /**
         * Checks if the device has a specified application installed and returns
         * a boolean for the check.
         */
        public boolean isApplicationInstalled(Context context, String packageName) {
            PackageManager packageManager = context.getPackageManager();
            try {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                return true;
            } catch (NameNotFoundException e) {
            }
            return false;
        }

        /**
         * Implements onPageFinished().
         *
         * When the page is finished loading, the youtube iframe embed will show up, but it
         * will still act like an embedded object, since it is not an actual link. This
         * function creates the link to the youtube video and positions it over the video
         * so that the embed essentially becomes clickable. This will launch the native
         * youtube app.
         */
        @Override
        public void onPageFinished(final WebView view, String url) {
            NodeActivity contextActivity = (NodeActivity) mContext;
            contextActivity.updateJavaScript();
            mIsJavaScriptLoaded = true;
            super.onPageFinished(view, url);
        }
    }
}
