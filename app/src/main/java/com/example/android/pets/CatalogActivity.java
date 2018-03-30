/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetsEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Database Helper that will provide us access to the DB */
    PetDbHelper mDbHelper;

    /* SQLiteDatabase variable to be able to read or write to the DB */
    SQLiteDatabase db;

    /* Identifies a particular Loader being used in this component */
    private static final int PET_LOADER = 0;

    /* Setup an Adapter to create a list item for each row of pet data in the Cursor */
    PetCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(CatalogActivity.this);

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.pet_list_view);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader is finishied) so pass in null for the Cursor
        mCursorAdapter = new PetCursorAdapter(CatalogActivity.this, null);
        petListView.setAdapter(mCursorAdapter);

        /**
         * Initialize the Cursor Loader. The URL_LOADER value is eventually passed
         * to onCreateLoader()
         */
        getLoaderManager().initLoader(PET_LOADER, null, CatalogActivity.this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

     /**
    * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
    */
    private void insertPet()
        {
            // Gets the database in write mode
            db = mDbHelper.getWritableDatabase();

            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            ContentValues values = new ContentValues();
            values.put(PetsEntry.COLUMN_PET_NAME, "Toto");
            values.put(PetsEntry.COLUMN_PET_BREED, "Terrier");
            values.put(PetsEntry.COLUMN_PET_GENDER, PetsEntry.GENDER_MALE);
            values.put(PetsEntry.COLUMN_PET_WEIGHT, 7);

            // Insert a new row for Toto into the provider using the ContentResolver.
            // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
            // into the pets database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            Uri newUri = getContentResolver().insert(PetContract.PetsEntry.CONTENT_URI, values);

            // Log message to show the ID of the newly inserted row
            Log.v("CatalogActivity", "New Row URI: " + newUri);
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection =
                {
                        PetsEntry._ID,
                        PetsEntry.COLUMN_PET_NAME,
                        PetsEntry.COLUMN_PET_BREED,
                };

        // This Loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                      CatalogActivity.this,
                            PetsEntry.CONTENT_URI,
                            projection,
                            null,
                            null,
                            null
                    );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.changeCursor(data);

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // Callback called when the data needs to be deleted
        mCursorAdapter.changeCursor(null);

    }
}
