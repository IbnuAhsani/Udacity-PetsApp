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

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetsEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /* Database Helper that will provide us access to the DB */
    PetDbHelper mDbHelper;

    /* SQLiteDatabase variable to be able to read or write to the DB */
    SQLiteDatabase db;

    /*  */
    String selection;

    /*  */
    String selctionArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(CatalogActivity.this);

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

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Display the info from the DB after transitioning
        // from the EditorActivity
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    public void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        db = mDbHelper.getReadableDatabase();

        String[] projection =
            {
                PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
                PetsEntry.COLUMN_PET_GENDER,
                PetsEntry.COLUMN_PET_WEIGHT
            };
        Cursor cursor = db.query(
            PetsEntry.TABLE_PET_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        );

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");

            // Create a header in the Text View that looks like this:
            // The pets table contains <number of rows in Cursor> pets.
            //
            //              _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.append(
                PetsEntry._ID + " - " + PetsEntry.COLUMN_PET_NAME +
                " - " + PetsEntry.COLUMN_PET_BREED + " - " + PetsEntry.COLUMN_PET_GENDER +
                " - " + PetsEntry.COLUMN_PET_WEIGHT + "\n\n"
            );

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(PetsEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);
            int currentId;
            String currentName;
            String currentBreed;
            int currentGender;
            int currentWeight;

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext())
                {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    currentId = cursor.getInt(idColumnIndex);
                    currentName = cursor.getString(nameColumnIndex);
                    currentBreed = cursor.getString(breedColumnIndex);
                    currentGender = cursor.getInt(genderColumnIndex);
                    currentWeight = cursor.getInt(weightColumnIndex);

                    // Display the values from each column of the current row in the cursor in the TextView
                    displayView.append((
                        "\n" + currentId + " - " +
                        currentName + " - " + currentBreed + " - " +
                        currentGender + " - " + currentWeight
                    ));
                }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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

            // Insert a new row for Toto in the database, returning the ID of that new row.
            // The first argument for db.insert() is the pets table name.
            // The second argument provides the name of a column in which the framework
            // can insert NULL in the event that the ContentValues is empty (if
            // this is set to "null", then the framework will not insert a row when
            // there are no values).
            // The third argument is the ContentValues object containing the info for Toto.
            long newRowId = db.insert(PetsEntry.TABLE_PET_NAME, null, values);

            // Log message to show the ID of the newly inserted row
            Log.v("CatalogActivity", "New Row Id: " + newRowId);
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
