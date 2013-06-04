/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

/**
 * Displays a list of bookmarks that the user previously set for easy access to
 * stories the user was interested in. This acts sort of like a favorites
 * system.
 */

public class BookmarksActivity extends FragmentActivity implements
        OnBrowseListTouchListener {

    // Stores FragInfo (fragment-wrapper) objects keyed by their corresponding
    // tags.
    private HashMap<String, FragInfo> mHmFragInfo = new HashMap<String, BookmarksActivity.FragInfo>();
    private FragInfo mFiNewFrag = null;
    
    // Tablet vs. phone boolean. Defaults to phone.
    public static boolean sIsTablet = false;
    public static boolean sIsSmall = false;
    public static boolean sIsNormal = false;
    public static boolean sIsLarge = false;
    public static boolean sIsExtraLarge = false;

    /**
     * Stores fragment data used in the outputting the listview.
     */
    
    private class FragInfo {

        // Stores the fragment tag.
        private String tag;
        // Stores the class associated with the fragment.
        private Class<?> clss;
        // Contains a Bundle of this Activity's args to pass to the fragments.
        private Bundle args;
        // Contains the actual default tab fragment.
        private Fragment fragment;

        /**
         * Sets the necessary properties for FragInfo objects.
         */
        FragInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }
    }

    // Define any views.
    FrameLayout flBookmarksEmpty;
    TextView tvListLabel;

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

        setContentView(R.layout.activity_list);
        
        // Determine whether or not the current device is a tablet.
        BookmarksActivity.sIsTablet = getResources().getBoolean(R.bool.isTablet);
        BookmarksActivity.sIsSmall = getResources().getBoolean(R.bool.isSmall);
        BookmarksActivity.sIsNormal = getResources().getBoolean(R.bool.isNormal);
        BookmarksActivity.sIsLarge = getResources().getBoolean(R.bool.isLarge);
        BookmarksActivity.sIsExtraLarge = getResources().getBoolean(R.bool.isExtraLarge);

        // Attach views to their corresponding resource ids.
        flBookmarksEmpty = (FrameLayout) findViewById(R.id.flListEmpty);
        tvListLabel = (TextView) findViewById(R.id.tvListLabel);
        
        // Set the text label
        tvListLabel.setText(R.string.tv_bookmarks_label);

        // Setup the fragment.
        initFragment(savedInstanceState);
    }

    /**
     * Implements onResume().
     */
    @Override
    protected void onResume() {
        super.onResume();
        ((BrowseListFragment) this.mFiNewFrag.fragment).updateList();

        // If the list is empty...display a message...
        if (((BrowseListFragment) this.mFiNewFrag.fragment).nodes.length == 0) {
            TextView tvBookmarksEmpty = new TextView(this);
            tvBookmarksEmpty.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            tvBookmarksEmpty
                    .setText(R.string.bookmarks_help);
            
            // Set the text size based on the size of the current device.
            if (BookmarksActivity.sIsTablet) {
                if (BookmarksActivity.sIsSmall) {
                    tvBookmarksEmpty.setTextSize(24);
                }
                else if (BookmarksActivity.sIsNormal) {
                    tvBookmarksEmpty.setTextSize(30);
                }
                else if (BookmarksActivity.sIsLarge) {
                    tvBookmarksEmpty.setTextSize(36);
                }
                else if (BookmarksActivity.sIsExtraLarge) {
                    tvBookmarksEmpty.setTextSize(40);
                }
            } else {
                tvBookmarksEmpty.setTextSize(16);
            }
            
            
            tvBookmarksEmpty.setTextColor(Color.WHITE);
            // Add the textview message to a view.
            flBookmarksEmpty.addView(tvBookmarksEmpty);
        }
    }

    /**
     * Initializes the fragments used in this activity and adds them to the
     * hashmap.
     */
    private void initFragment(Bundle args) {
        // Set our current frag to null
        mFiNewFrag = null;

        // Add the frag to the hashmap with all it's corresponding data.
        BookmarksActivity.addFrag(this, (mFiNewFrag = new FragInfo("Frag1",
                BrowseListFragment.class, args)), "Frag1");
        this.mHmFragInfo.put(mFiNewFrag.tag, mFiNewFrag);

        // Set the frag to automatically display.
        attachFrag("Frag1");
    }

    /**
     * Detaches the fragments if necessary as they are being added to the
     * hashmap.
     */
    private static void addFrag(BookmarksActivity activity, FragInfo fiNewFrag,
            String tag) {
        // Check to see if we already have a fragment created, probably
        // from a previously saved state. If so, deactivate it, because our
        // initial state is that a frag isn't being shown.
        fiNewFrag.fragment = activity.getSupportFragmentManager()
                .findFragmentByTag(tag);

        if (fiNewFrag.fragment != null && !fiNewFrag.fragment.isDetached()) {
            // If a fragment was previously created and it is currently
            // attached,
            // begin a fragment transaction.
            FragmentTransaction ft = activity.getSupportFragmentManager()
                    .beginTransaction();
            // Detach the fragment.
            ft.detach(fiNewFrag.fragment);

            // Commit transaction changes.
            ft.commit();
            // Execute the transaction.
            activity.getSupportFragmentManager().executePendingTransactions();
        }
    }

    /**
     * Attaches fragments as a user navigates through the listview.
     */
    public void attachFrag(String tag) {
        // Set the TabInfo object to the tag being selected.
        mFiNewFrag = null;
        mFiNewFrag = (FragInfo) this.mHmFragInfo.get(tag);

        // Begin a fragment transaction.
        FragmentTransaction ft = this.getSupportFragmentManager()
                .beginTransaction();

        if (mFiNewFrag != null) {
            // Case: the fragment was previously created...
            if (mFiNewFrag.fragment != null) {
                // Detach the fragment.
                ft.detach(mFiNewFrag.fragment);
            }
        }

        if (mFiNewFrag != null) {
            // Case: The fragment has not yet been created.
            if (mFiNewFrag.fragment == null) {
                // Instantiate the fragment and store the returned fragment
                // object in
                // the FragInfo's fragment property.
                mFiNewFrag.fragment = Fragment.instantiate(this,
                        mFiNewFrag.clss.getName(), mFiNewFrag.args);
                // Add the fragment to the view through the frame layout.
                ft.add(R.id.flList, mFiNewFrag.fragment, mFiNewFrag.tag);

            } else {
                // If the fragment has been previously created, simply attach
                // it.
                ft.attach(mFiNewFrag.fragment);
            }
        }

        // Commit the transaction.
        ft.commit();
        // Execute the transaction.
        this.getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Implements onBrowseListTouchListener().
     * 
     * This is a custom interface
     * created for the purposes of allowing fragment-to-host-activity
     * communication for Menu List Item click events. The host activity must
     * implement this interface if using the fragments that need to communicate
     * with host. Whenever a fragment fires this event, the fragment will pass
     * the host it's fragment name and an id of content to query. The type of
     * content is determined by the fragment name.
     */
    @Override
    public void onBrowseListTouchListener(String fragment, int dbId) {
        // If the fragment firing the event is Tab2's menu list fragment...
        if (fragment.equals("BrowseListFragment")) {
            // Create a new intent setting the target action to be the
            // NodeActivity
            // activity.
            Intent nodeActivityIntent = new Intent(
                    "net.globide.creepypasta_files_07.NODEACTIVITY");
            nodeActivityIntent.putExtra("id", dbId);

            // Start the activity via intent.
            startActivity(nodeActivityIntent);
        }
    }
}
