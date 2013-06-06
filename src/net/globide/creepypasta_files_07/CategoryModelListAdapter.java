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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Defines the adapter used in generating item lists in Browse Activity. Also
 * used in database query result generation.
 */

public class CategoryModelListAdapter extends BaseAdapter {

    /**
     * ViewHolder Design Pattern - contains the views each list item will
     * display.
     */
    private class ViewHolder {
        public TextView textView;
    }

    // Define an array containing all the data necessary in generating each list
    // item.
    private CategoryModel[] mCategories;
    // LayoutInflater defined in class scope for access.
    private LayoutInflater mInflater;

    /**
     * Constructs the adapter.
     */
    public CategoryModelListAdapter(Context context, CategoryModel[] categories) {
        // Inflate the layout.
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Set the list data array.
        mCategories = categories;
    }

    /**
     * Implements getCount().
     */
    @Override
    public int getCount() {
        // If data exists in the list array...
        if (mCategories != null) {
            // Return the array's length.
            return mCategories.length;
        }
        // Else return 0 for no length.
        return 0;
    }

    /**
     * Implements getItem().
     */
    @Override
    public Object getItem(int position) {
        // If list items exist, and the position requested is within bounds of
        // the
        // list item data array...
        if (mCategories != null && position >= 0 && position < getCount()) {
            // Return the object.
            return mCategories[position];
        }
        // Else return null.
        return null;
    }

    /**
     * Implements getItemId().
     */
    @Override
    public long getItemId(int position) {
        // If list items exist, and the position requested is within bounds of
        // the
        // list item data array...
        if (mCategories != null && position >= 0 && position <= getCount()) {
            // Return the object's id.
            return mCategories[position].id;
        }
        // Else return 0 for no id.
        return 0;
    }

    /**
     * Implements getView().
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Set up a view to contain the view being passed.
        View view = convertView;
        // Define a new ViewHolder object.
        ViewHolder viewHolder;

        // If a view does not exist...
        if (view == null) {
            // Inflate the fragment for this list.
            view = mInflater.inflate(R.layout.fragment_browse_list, parent, false);

            // Create a ViewHolder instance containing our list item views.
            viewHolder = new ViewHolder();
            // Attach the views to their corresponding resource ids.
            viewHolder.textView = (TextView) view.findViewById(R.id.tvBrowseList);

            // Set a view tag for our view.
            view.setTag(viewHolder);
        } else {
            // If the view was already created, just get the tag previously set.
            viewHolder = (ViewHolder) view.getTag();
        }

        // Create a list data object and set it to the current list item
        // position.
        CategoryModel categoryModel = mCategories[position];

        // Set the list item textview to the category label.
        viewHolder.textView.setText(categoryModel.category);

        // Return the view.
        return view;
    }
}
