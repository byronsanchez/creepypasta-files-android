/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Displays a list of bookmarks that the user previously set for easy access to
 * stories the user was interested in. This acts sort of like a favorites
 * system.
 */

public class BrowseActivity extends FragmentActivity implements
        OnBrowseListTouchListener {

    // Stores FragInfo (fragment-wrapper) objects keyed by their corresponding
    // tags.
    private HashMap<String, FragInfo> mHmFragInfo = new HashMap<String, BrowseActivity.FragInfo>();
    private FragInfo mFiNewFrag = null;

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

        // Attach views to their corresponding resource ids.
        flBookmarksEmpty = (FrameLayout) findViewById(R.id.flListEmpty);
        tvListLabel = (TextView) findViewById(R.id.tvListLabel);
        
        // Set the text label
        tvListLabel.setText(R.string.tv_browse_label);

        // Setup the fragment.
        initFragment(savedInstanceState);
    }

    /**
     * Initializes the fragments used in this activity and adds them to the
     * hashmap.
     */
    private void initFragment(Bundle args) {
        // Set our current frag to null
        mFiNewFrag = null;

        // Add the frag to the hashmap with all it's corresponding data.
        BrowseActivity.addFrag(this, (mFiNewFrag = new FragInfo("Frag1",
                BrowseListFragment.class, args)), "Frag1");
        this.mHmFragInfo.put(mFiNewFrag.tag, mFiNewFrag);

        // Set the frag to automatically display.
        attachFrag("Frag1");
    }

    /**
     * Detaches the fragments if necessary as they are being added to the
     * hashmap.
     */
    private static void addFrag(BrowseActivity activity, FragInfo fiNewFrag,
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
