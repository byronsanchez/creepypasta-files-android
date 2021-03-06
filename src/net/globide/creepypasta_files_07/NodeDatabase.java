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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

/**
 * Handles all database requests made by the application.
 */

public class NodeDatabase extends SQLiteOpenHelper {

    // Define the SQLite database location.
    private static final String DATABASE_NAME = "creepypasta_files.db";

    // Define the tables used in the application.
    private static final String DATABASE_TABLE_NODE = "node";
    private static final String DATABASE_TABLE_CATEGORIES = "categories";
    private static final String DATABASE_TABLE_VARIABLES = "variables";
    private static final String DATABASE_TABLE_SCHEMA = "schema";

    // Define the "node" table SQLite columns
    private static final String KEY_NODE_ROWID = "_id";
    private static final String KEY_NODE_CATEGORYID = "cid";
    private static final String KEY_NODE_TITLE = "title";
    private static final String KEY_NODE_BODY = "body";
    private static final String KEY_NODE_IS_BOOKMARKED = "is_bookmarked";

    // Define the "categories" table SQLite columns
    public static final String KEY_CATEGORIES_ROWID = "_id";
    public static final String KEY_CATEGORIES_CATEGORY = "category";
    public static final String KEY_CATEGORIES_DESCRIPTION = "description";
    
    // Defie the "variables" table SQLite columns
    public static final String KEY_VARIABLES_ROWID = "_id";
    public static final String KEY_VARIABLES_KEY = "key";
    public static final String KEY_VARIABLES_VALUE = "value";
    public static final String KEY_VARIABLES_SKU = "sku";

    // Defie the "schema" table SQLite columns
    private static final String KEY_SCHEMA_ROWID = "_id";
    private static final String KEY_SCHEMA_MAJOR_RELEASE_NUMBER = "major_release_number";
    private static final String KEY_SCHEMA_MINOR_RELEASE_NUMBER = "minor_release_number";
    private static final String KEY_SCHEMA_POINT_RELEASE_NUMBER = "point_release_number";
    private static final String KEY_SCHEMA_SCRIPT_NAME = "script_name";
    private static final String KEY_SCHEMA_DATE_APPLIED = "date_applied";

    // Define the current schema version.
    private static final int SCHEMA_VERSION = 2;

    // Define the context and the actual database property.
    private final Context mOurContext;
    private SQLiteDatabase mOurDatabase;

    // Define query builder properties.
    private String mLeftOperand;
    private String mRightOperand;
    private String mOperator;

    // A boolean signalling whether or not an upgrade task is in progress.
    public boolean mIsUpgradeTaskInProgress = false;

    /**
     * Pass the database information and set the context.
     */
    public NodeDatabase(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
        mOurContext = context;
    }

    /**
     * Wrapper method for creating the database. Intended for external access.
     */
    public void createDatabase() {
        createDB();
    }

    /**
     * Creates the database or establishes the database connection.
     */
    private void createDB() {
        // Check to see if the database exists. (Typically, on first run, it
        // should not exist yet).
        boolean dbExist = DBExists();

        // If a database does not exist, create one.
        if (!dbExist) {
            // Create an empty database in the default system location.
            mOurDatabase = getWritableDatabase();

            // Run all available changescripts.
            new DBUpgradeTask(mOurDatabase).execute("0000");
        } else {
            // If the database exists, just call it for use.
            mOurDatabase = getWritableDatabase();
        }
    }

    /**
     * Checks to see if a database file exists in the default system location.
     */
    private boolean DBExists() {
        File dbFile = mOurContext.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Extracts the specified portion of the script file name.
     */
    private String extractStringFromScript(String scriptFileName, String scriptMeta) {
        // Split accepts regular expressions, so if it is "acting weird"
        // that's probably it.
        String[] parts = scriptFileName.split("\\.");

        if (scriptMeta.equals("major_release_number")) {
          return parts[1];
        }
        else if (scriptMeta.equals("minor_release_number")) {
          return parts[2];
        }
        else if (scriptMeta.equals("point_release_number")) {
          return parts[3];
        }
        else {
          return "";
        }
    }

    /**
     * Returns whether or not a table exists in the database.
     */
    public boolean doesTableExist(String tableName) {

        Cursor cursor = mOurDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }

        return false;
    }

    /**
     * Returns a full list of categories from the categories table.
     */
    public Category[] getCategoryListData() {

        // TODO: consider also loading descriptions in the future

        // Define an array of columns to SELECT.
        String[] columns = new String[] {
                KEY_CATEGORIES_ROWID,
                KEY_CATEGORIES_CATEGORY
        };
        // Define the cursor to contain our query results, and execute the
        // query.
        Cursor c = mOurDatabase.query(DATABASE_TABLE_CATEGORIES, columns, null,
                null, null, null, "UPPER(" + KEY_CATEGORIES_CATEGORY + ")");

        // Define an array per column, for dynamic results.
        ArrayList<Integer> resultId = new ArrayList<Integer>();
        ArrayList<String> resultCategory = new ArrayList<String>();

        // Define indices for each column, for iteration.
        int id = c.getColumnIndex(KEY_CATEGORIES_ROWID);
        int category = c.getColumnIndex(KEY_CATEGORIES_CATEGORY);

        // Loop through the results in the Cursor.
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            // Add each id to the id ArrayList.
            resultId.add(c.getInt(id));
            // Add each category to the category ArrayList.
            resultCategory.add(c.getString(category));
        }

        // Close the cursor.
        c.close();

        // Define an array in which to store the results.
        Category[] resultArray = new Category[resultId.size()];

        // Convert the ArrayList to an array.
        for (int i = 0; i < resultId.size(); i++) {
            resultArray[i] = new Category(resultId.get(i), resultCategory.get(i));
        }

        // Return the array.
        return resultArray;
    }

    /**
     * Returns a list of node titles filtered by a category id.
     */
    public Node[] getNodeListData(int cid) {

        // Define an array of columns to SELECT.
        String[] columns = new String[] {
                KEY_NODE_ROWID, KEY_NODE_TITLE
        };
        // Define the cursor to contain our query results, and execute the
        // query.
        Cursor c = mOurDatabase.query(DATABASE_TABLE_NODE, columns,
                KEY_NODE_CATEGORYID + "=" + cid, null, null, null, "UPPER(" + KEY_NODE_TITLE + ")");

        // Define an array per column, for dynamic results.
        ArrayList<Integer> resultId = new ArrayList<Integer>();
        ArrayList<String> resultTitle = new ArrayList<String>();

        // Define indices for each column, for iteration.
        int id = c.getColumnIndex(KEY_NODE_ROWID);
        int title = c.getColumnIndex(KEY_NODE_TITLE);

        // Loop through the results in the Cursor.
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            // Add each id to the id ArrayList.
            resultId.add(c.getInt(id));
            // Add each node title to the node title ArrayList.
            resultTitle.add(c.getString(title));
        }

        // Close the cursor.
        c.close();

        // Define an array in which to store the results.
        Node[] resultArray = new Node[resultId.size()];

        // Convert the ArrayList to an array.
        for (int i = 0; i < resultId.size(); i++) {
            resultArray[i] = new Node(resultId.get(i), resultTitle.get(i));
        }

        // Return the array.
        return resultArray;
    }

    /**
     * Returns a full list of node titles.
     */
    public Node[] getNodeListData() {

        // Initialize a where string to contain a potential WHERE clause.
        String where = "";

        // If the WHERE clause properties were set...
        if (this.mLeftOperand != null && this.mRightOperand != null
                && this.mOperator != null) {
            // Define the WHERE clause.
            where = this.mLeftOperand + this.mOperator + this.mRightOperand;
        }

        // Define an array of columns to SELECT.
        String[] columns = new String[] {
                KEY_NODE_ROWID, KEY_NODE_TITLE
        };
        // Create a cursor to contain our query results;
        Cursor c;

        // If a WHERE clause was defined.
        if (where != "") {
            // Execute the query with the WHERE clause.
            c = mOurDatabase.query(DATABASE_TABLE_NODE, columns, where, null, null,
                    null, "UPPER(" + KEY_NODE_TITLE + ")");
        } else {
            // Execute the query.
            c = mOurDatabase.query(DATABASE_TABLE_NODE, columns, null, null, null,
                    null, "UPPER(" + KEY_NODE_TITLE + ")");
        }

        // Define an array per column, for dynamic results.
        ArrayList<Integer> resultId = new ArrayList<Integer>();
        ArrayList<String> resultTitle = new ArrayList<String>();

        // Define indices for each column, for iteration.
        int id = c.getColumnIndex(KEY_NODE_ROWID);
        int title = c.getColumnIndex(KEY_NODE_TITLE);

        // Loop through the results in the Cursor.
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            // Add each id to the id ArrayList.
            resultId.add(c.getInt(id));
            // Add each node title to the node title ArrayList.
            resultTitle.add(c.getString(title));
        }

        // Close the cursor.
        c.close();

        // Define an array in which to store the results.
        Node[] resultArray = new Node[resultId.size()];

        // Convert the ArrayList to an array.
        for (int i = 0; i < resultId.size(); i++) {
            resultArray[i] = new Node(resultId.get(i), resultTitle.get(i));
        }

        // Return the array.
        return resultArray;
    }

    /**
     * Returns a single node row containing all column data.
     */
    public Node getNodeData(long l) throws SQLException {
        // Define an array of columns to SELECT.
        String[] columns = new String[] {
                KEY_NODE_ROWID, KEY_NODE_TITLE,
                KEY_NODE_BODY
        };
        // Define the cursor to contain our query results, and execute the
        // query.
        Cursor c = mOurDatabase.query(DATABASE_TABLE_NODE, columns, KEY_NODE_ROWID
                + "=" + l, null, null, null, null);

        // If there is a result...
        if (c != null) {
            // Position the iterator to the start of the result set.
            c.moveToFirst();

            // Define a node to store each column result.
            // Store the results in local variables.
            Node result = new Node();
            result.id = Integer.parseInt(c.getString(0));
            result.title = c.getString(1);
            result.body = c.getString(2);

            // Close the cursor.
            c.close();

            // Return the array.
            return result;
        }

        // No result? Return null.
        return null;
    }
    
    /**
     * Returns a full list of variables from the variables table.
     */
    public Variable[] getVariableListData() {

        // Define an array of columns to SELECT.
        String[] columns = new String[] {
                KEY_VARIABLES_ROWID,
                KEY_VARIABLES_KEY,
                KEY_VARIABLES_VALUE,
                KEY_VARIABLES_SKU
        };
        // Define the cursor to contain our query results, and execute the
        // query.
        Cursor c = mOurDatabase.query(DATABASE_TABLE_VARIABLES, columns, null,
                null, null, null, "UPPER(" + KEY_VARIABLES_KEY + ")");

        // Define an array per column, for dynamic results.
        ArrayList<Integer> resultId = new ArrayList<Integer>();
        ArrayList<String> resultKey = new ArrayList<String>();
        ArrayList<String> resultValue = new ArrayList<String>();
        ArrayList<String> resultSku = new ArrayList<String>();

        // Define indices for each column, for iteration.
        int id = c.getColumnIndex(KEY_VARIABLES_ROWID);
        int key = c.getColumnIndex(KEY_VARIABLES_KEY);
        int value = c.getColumnIndex(KEY_VARIABLES_VALUE);
        int sku = c.getColumnIndex(KEY_VARIABLES_SKU);

        // Loop through the results in the Cursor.
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            // Add each id to the id ArrayList.
            resultId.add(c.getInt(id));
            // Add each key to the key ArrayList.
            resultKey.add(c.getString(key));
            // Add each value to the value ArrayList.
            resultValue.add(c.getString(value));
            // Add each sku id to the sku ArrayList.
            resultSku.add(c.getString(sku));
        }

        // Close the cursor.
        c.close();

        // Define an array in which to store the results.
        Variable[] resultArray = new Variable[resultId.size()];

        // Convert the ArrayList to an array.
        for (int i = 0; i < resultId.size(); i++) {
            resultArray[i] = new Variable(resultId.get(i), resultKey.get(i), resultValue.get(i), resultSku.get(i));
        }

        // Return the array.
        return resultArray;
    }

    /**
     * Queries the node table to detemine whether or not a row is bookmarked
     */
    public int getNodeIsBookmarked(long l) {
        // Define an array of columns to SELECT.
        String[] columns = new String[] {
                KEY_NODE_IS_BOOKMARKED
        };
        // Define the cursor to contain our query results, and execute the
        // query.
        Cursor c = mOurDatabase.query(DATABASE_TABLE_NODE, columns, KEY_NODE_ROWID
                + "=" + l, null, null, null, null);

        // If there is a result...
        if (c != null) {
            // Position the iterator to the start of the result set.
            c.moveToFirst();

            // Store the results in a local variable.
            int isBookmarked = c.getInt(0);

            // Close the cursor.
            c.close();

            // Return the boolean result.
            return isBookmarked;
        }

        // No result? Return 0.
        return 0;
    }

    /**
     * Updates a node column.
     */
    public void updateNode(long nid, String column, int value) {

        // Create the new values
        ContentValues cv;

        // Store the arguments passed as the column and value in ContentValues.
        cv = new ContentValues();
        cv.put(column, value);

        // Execute a simple update query using the nid as the WHERE clause
        mOurDatabase.update(DATABASE_TABLE_NODE, cv, "_id=" + nid, null);
    }
    
    /**
     * Updates a variable column.
     */
    public void updateVariable(long vid, String column, String value) {

        // Create the new values
        ContentValues cv;

        // Store the arguments passed as the column and value in ContentValues.
        cv = new ContentValues();
        cv.put(column, value);

        // Execute a simple update query using the nid as the WHERE clause
        mOurDatabase.update(DATABASE_TABLE_VARIABLES, cv, "_id=" + vid, null);
    }

    /**
     * Sets conditions for a potential WHERE clause.
     */
    public void setConditions(String leftOperand, String rightOperand) {
        this.mLeftOperand = leftOperand;
        this.mRightOperand = rightOperand;
        this.mOperator = "=";
    }

    /**
     * Sets condition for a potential WHERE clause.
     */
    public void setConditions(String leftOperand, String rightOperand,
            String operator) {
        this.mLeftOperand = leftOperand;
        this.mRightOperand = rightOperand;
        this.mOperator = operator;
    }

    /**
     * Flushes any query builder properties.
     */
    public void flushQuery() {
        this.mLeftOperand = null;
        this.mRightOperand = null;
        this.mOperator = null;
    }

    /**
     * Implements onCreate().
     * 
     * Typically used to create the database
     * table. Not necessary in this application.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * Implements onUpgrade().
     * 
     * Typically used to upgrade the database
     * table. Not necessary in this application.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We are starting transactions directly from the DDL files, so the
        // default transaction is unnecessary here. This way, all DDL files
        // are consistent, even outside the scope of Java code.
        db.endTransaction();
        mOurDatabase = db;
        boolean tableExists = doesTableExist("schema");
        if (!tableExists) {
            // most recent script == sc.01.00.0002
            new DBUpgradeTask(mOurDatabase).execute("0002");
        }
        else {
            // Define an array of columns to SELECT.
            String[] columns = new String[] {
                    KEY_SCHEMA_ROWID, KEY_SCHEMA_MAJOR_RELEASE_NUMBER,
                    KEY_SCHEMA_MINOR_RELEASE_NUMBER, KEY_SCHEMA_POINT_RELEASE_NUMBER,
                    KEY_SCHEMA_SCRIPT_NAME, KEY_SCHEMA_DATE_APPLIED
            };

            // Define the cursor to contain our query results, and execute the
            // query.
            Cursor c = mOurDatabase.query(DATABASE_TABLE_SCHEMA, columns, null, null, null, null, KEY_SCHEMA_SCRIPT_NAME + " DESC", "1");

            // If there is a result...
            if (c != null) {
                // Position the iterator to the start of the result set.
                c.moveToFirst();
                String recentScriptID = extractStringFromScript( c.getString(4), "point_release_number" );
                // Close the cursor.
                c.close();

                new DBUpgradeTask(mOurDatabase).execute( recentScriptID );
            }
        }
        // The API is going to try and close a transaction, so let's start one
        // to prevent errors. I know this is hacky, but at least we now get
        // consistent DDL files.
        db.beginTransaction();
        mOurDatabase = null;
    }

    /**
     * Checks to see whether the database is being accessed asynchronously and
     * if so, does NOT close the database.
     *
     * This will then allow the asynchronous task to close the database itself.
     * If the database is not being accessed asynchronously, closes the
     * database.
     */
    @Override
    public synchronized void close() {
        if (!mIsUpgradeTaskInProgress) {
            super.close();
        }
    }

    /**
     * An asynchronous database updater.
     *
     * It closes the database when updates are done and displays a
     * ProgressDialog to prevent interaction and attempts at using
     * the database while simultaneously not blocking the main thread.
     */

    private class DBUpgradeTask extends AsyncTask<String, Void, Void> {
        
        private ProgressDialog progressDialog;
        private SQLiteDatabase mOurDatabase;

        /**
         * Constructor which sets the database-helper object reference.
         */
        public DBUpgradeTask(SQLiteDatabase db) {
            this.mOurDatabase = db;
        }

        /**
         * Displays the ProgressDialog and signals that an upgrade task is in
         * progress.
         */
        @Override
        protected void onPreExecute() {
            mIsUpgradeTaskInProgress = true;
            progressDialog = ProgressDialog.show(mOurContext, "[processing]", "Acquiring new files...", true, false);
        }

        /**
         * Runs the updates starting from the script AFTER the one passed
         * via the params argument.
         */
        @Override
        protected Void doInBackground(String... params) {
            // Run the database update.
            runUpdates( params[0] );
            return null;
        }

        /**
         * Signals that the upgrade task is completed and dismisses the
         * ProgressDialog.
         */
        @Override
        protected void onPostExecute(Void result) {
            // Hide the dialog.
            mIsUpgradeTaskInProgress = false;
            progressDialog.dismiss();
        }

        /**
         * Runs updates from the specified script ID an all subsequent available
         * updates.
         */
        private void runUpdates( String recentScriptID ) {
           
            // Signal a fresh install if the database is being created from scratch.
            boolean isFreshInstall = false;
            if (recentScriptID.equals("0000")) {
                isFreshInstall = true;
            }

            // Get a list of all database scripts from the assets directory.
            String[] fileList = null;
            try {
                fileList = mOurContext.getAssets().list("database");
            } catch ( IOException ioe ) {
                fileList = new String[] {};
            }

            for (int i = 0; i < fileList.length; i++) {
                String fileString = fileList[i];
                String scriptIDString = extractStringFromScript( fileString, "point_release_number" );

                // If the current iteration scriptID is less than the most recently
                // applied update, skip to the next iteration.
                // Ignore this check for fresh installs, as all scripts will run in
                // in that case.
                if (!isFreshInstall && scriptIDString.compareTo(recentScriptID) <= 0) {
                  continue;
                }
                applyScript( fileString );

                // This is what happens when you don't think ahead. Now we have to
                // skip logging directly to the database for the first 3
                // changescripts, because the schema table is first introduced in
                // the third changescript. The third changescript also
                // retroactively logs all previous change scripts.
                if (scriptIDString.compareTo("0003") <= 0) {
                  continue;
                }

                // Update the Schema Change Log.

                // Prepare the data to insert.
                String major_release_number = extractStringFromScript( fileString, "major_release_number" );
                String minor_release_number = extractStringFromScript( fileString, "minor_release_number" );
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String date_applied = sdf.format(now);
                
                // Create the new values
                ContentValues cv;
                cv = new ContentValues();
                cv.put(KEY_SCHEMA_MAJOR_RELEASE_NUMBER, major_release_number);
                cv.put(KEY_SCHEMA_MINOR_RELEASE_NUMBER, minor_release_number);
                cv.put(KEY_SCHEMA_POINT_RELEASE_NUMBER, scriptIDString);
                cv.put(KEY_SCHEMA_SCRIPT_NAME, fileString);
                cv.put(KEY_SCHEMA_DATE_APPLIED, date_applied);

                // Execute a simple update query using the nid as the WHERE clause
                mOurDatabase.insert(DATABASE_TABLE_SCHEMA, null, cv);
            }

            // Close the database now that updates are done.
            mOurDatabase.close();
        }

        /**
         * Applies a change-script to the database.
         */
        private void applyScript( String script ) {
            String[] items = null;
            try {
                BufferedReader reader = new BufferedReader( new InputStreamReader( mOurContext.getAssets().open( "database/" + script, AssetManager.ACCESS_STREAMING ) ), 8192 );

                StringBuffer sql = new StringBuffer();
                String line = null;
                while ( ( line = reader.readLine() ) != null ) {
                    sql.append( line );
                    sql.append( "\n" );
                }
                // split the ddl file by semi-colons anchored to the end of line.
                Pattern myPattern = Pattern.compile(";$", Pattern.MULTILINE);
                items = myPattern.split(sql.toString());
            } catch ( IOException ioe ) {
                items = new String[] {};
            }

            for ( String item : items ) {
                if ( item.trim().length() != 0 ) {
                    mOurDatabase.execSQL( item + ";" );
                }
            }
        }
    }
}
