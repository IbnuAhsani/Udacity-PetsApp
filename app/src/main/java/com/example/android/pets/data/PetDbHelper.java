package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.PetContract.PetsEntry;

/**
 * Created by test-pc on 28-Feb-18.
 */

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class PetDbHelper extends SQLiteOpenHelper {

    /* Defining the name of the DB */
    public static final String DATABASE_NAME = "shelter.db";

    /* Defining the version of the DB */
    public static final int DATABASE_VERSION = 1;

    /* The Query statement to CREATE the pets Table */
    public static final String SQL_CREATE_PETS_TABLE=
            "CREATE TABLE " + PetsEntry.TABLE_PET_NAME + " (" +
            PetsEntry._ID + " " + PetsEntry.INTEGER_TYPE + " " + PetsEntry.PRIMARY_KEY + " " + PetsEntry.NOT_NULL + ", " +
            PetsEntry.COLUMN_PET_NAME + " " + PetsEntry.TEXT_TYPE + " " + PetsEntry.NOT_NULL + ", " +
            PetsEntry.COLUMN_PET_BREED + " " + PetsEntry.TEXT_TYPE + ", " +
            PetsEntry.COLUMN_PET_GENDER + " " + PetsEntry.INTEGER_TYPE + " " + PetsEntry.INTEGER_TYPE + " " + PetsEntry.NOT_NULL + ", " +
            PetsEntry.COLUMN_PET_WEIGHT + " " + PetsEntry.INTEGER_TYPE + " " + PetsEntry.NOT_NULL + " DEFAULT 0);";

    /* The Query statemnt to DELETE the pets Table if it already exists */
    public static final String SQL_DELETE_PETS_TABLE = "DROP TABLE IF EXISTS " + PetsEntry.TABLE_PET_NAME;

    /* The constructor for the PetDbHelper class */
    public PetDbHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the pets Table if it doesn't already exists
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
