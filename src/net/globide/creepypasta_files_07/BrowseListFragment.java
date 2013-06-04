/*
 * Copyright (c) 2013 Byron Sanchez
 * www.chompix.com
 *
 * Licensed under the MIT license.
 */

package net.globide.creepypasta_files_07;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Creates the views to display whenever this fragment is invoked.
 */

public class BrowseListFragment extends ListFragment implements
        OnBrowseListTouchListener {

    // Define a class listener property so we can fire back events to the host
    // activity.
    private OnBrowseListTouchListener mListener;

    // Define the database access property.
    private NodeDatabase mDbNodeHelper = null;

    // Define an custom object array to hold our database query results.
    private CategoryModel[] mCm;
    public Node[] nodes;

    // Define the activity and the list properties with class scope.
    private Activity mActivity;
    private ListAdapter mListAdapter;

    /**
     * Implements onActivityCreated().
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the host activity as an object.
        mActivity = getActivity();

        // If the host activity exists...
        if (mActivity != null) {

            // Create our database access object.
            mDbNodeHelper = new NodeDatabase(mActivity);

            // Call the create right after initializing the helper, just in case
            // the user has never run the app before.
            mDbNodeHelper.createDatabase();

            // Get the name of the host activity invoking this fragment and load
            // different data depending on that host.
            if (getActivity().getClass().getName().contains("BookmarksActivity")) {
                // Get a list of bookmarked node titles.
                loadBookmarks();
            }
            else {
                // Get a list of node titles.
                loadList();
            }

            // Close the database
            mDbNodeHelper.close();

            // Store data from the category obect to the listadapter model
            // object.
            generateModel();

            // Create an instance of the custom adapter for the ListView.
            mListAdapter = new CategoryModelListAdapter(mActivity, mCm);
            // Set the listAdapter.
            setListAdapter(mListAdapter);
        }
    }

    /**
     * Queries the database for a list of nodes and returns an array of results.
     */
    public void loadList() {
        // Run the query.
        nodes = mDbNodeHelper.getNodeListData();
    }

    /**
     * Create a listadapter model based on the table results.
     */
    private void generateModel() {
        // Initialize a new model object
        mCm = new CategoryModel[nodes.length];

        for (int i = 0; i < nodes.length; i++) {
            mCm[i] = new CategoryModel(nodes[i].id, nodes[i].title);
        }
    }

    /**
     * Queries the database for a list of bookmarked nodes and returns an array
     * of results.
     */
    public void loadBookmarks() {
        // Set the WHERE clause
        mDbNodeHelper.setConditions("is_bookmarked", "1");
        // Run the query.
        nodes = mDbNodeHelper.getNodeListData();
        // Flush the query builder properties.
        mDbNodeHelper.flushQuery();
    }

    /**
     * Used by BookmarksActivity to update the listview if bookmarks are
     * removed.
     */
    public void updateList() {
        if (getActivity().getClass().getName().contains("BookmarksActivity")) {

            // Call the create right after initializing the helper, just in case
            // the user has never run the app before.
            mDbNodeHelper.createDatabase();

            // Get a list of bookmarked node titles.
            loadBookmarks();

            // Close the database
            mDbNodeHelper.close();

            // Clear the old ListView.
            setListAdapter(null);

            // Initialize a new model object
            CategoryModel[] bookmarksModel = new CategoryModel[nodes.length];

            for (int i = 0; i < nodes.length; i++) {
                bookmarksModel[i] = new CategoryModel(nodes[i].id, nodes[i].title);
            }

            // Create a new list adapter based on our new updated array.
            ListAdapter bookmarksAdapter = new CategoryModelListAdapter(mActivity, bookmarksModel);

            // set the new list adapter, thus updating the list display.
            setListAdapter(bookmarksAdapter);
        }
        else if (getActivity().getClass().getName().contains("BrowseActivity")) {

            // Call the create right after initializing the helper, just in case
            // the user has never run the app before.
            mDbNodeHelper.createDatabase();

            // Get a list of bookmarked node titles.
            loadList();

            // Close the database
            mDbNodeHelper.close();

            // Clear the old ListView.
            setListAdapter(null);

            // Initialize a new model object
            CategoryModel[] bookmarksModel = new CategoryModel[nodes.length];

            for (int i = 0; i < nodes.length; i++) {
                bookmarksModel[i] = new CategoryModel(nodes[i].id, nodes[i].title);
            }

            // Create a new list adapter based on our new updated array.
            ListAdapter bookmarksAdapter = new CategoryModelListAdapter(mActivity, bookmarksModel);

            // set the new list adapter, thus updating the list display.
            setListAdapter(bookmarksAdapter);
        }
    }

    /**
     * Implements onAttach().
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // When the fragment is added to the display screen, have this
            // class's
            // mListener property reference the host activity's
            // OnBrowseTouchListener
            // implementation. This allows this fragment to both fire events to
            // the
            // host activity AND forces activities using this fragment to
            // implement
            // OnBrowseTouchListener.
            mListener = (OnBrowseListTouchListener) activity;
        } catch (ClassCastException e) {
            // If the class using this fragment is NOT implementing
            // OnBrowseTouchListener, throw an exception.
            throw new ClassCastException(activity.toString()
                    + " must implement OnBrowseListTouchListener");
        }
    }

    /**
     * Implements onListItemClick().
     */
    @Override
    public void onListItemClick(ListView list, View view, int position, long id) {
        // When a user selects an item from this fragment's list, key the
        // classes
        // array with the position of the selected list item to get the node's
        // id in the database. (the list is displayed in the same order as the
        // classes array).
        int dbId = (int) id;
        // Store the name of this fragment in a string.
        String fragment = "BrowseListFragment";
        // Fire an onBrowseListTouch event, passing the fragment name and the
        // dbId
        // to the host activity.
        mListener.onBrowseListTouchListener(fragment, dbId);
    }

    /**
     * Implements onBrowseListTouchListener().
     */
    @Override
    public void onBrowseListTouchListener(String fragment, int dbId) {
        // do nothing...

        // Typically, we'd fire events to the host activity from here, but
        // instead
        // we are choosing to fire the events from onListItemClick.
    }
}
