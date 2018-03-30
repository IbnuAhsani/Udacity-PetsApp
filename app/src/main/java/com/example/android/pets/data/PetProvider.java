package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by test-pc on 03-Mar-18.
 */

public class PetProvider extends ContentProvider {

    /* DB Helper Object */
    private PetDbHelper mDbHelper;

    /* Tag for the Log Message */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PETS_ID = 101;

    /* The variable that's used when matching predefined URIs to Querying URIs */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static
        {
            // The calls to addURI() go here, for all of the content URI patterns that the provider
            // should recognize. All paths added to the UriMatcher have a corresponding code to return
            // when a match is found.

            // The content URI of the form "content://com.example.android.pets/pets" will map to the
            // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
            // of the pets table.
            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

            // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
            // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
            // of the pets table.
            //
            // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
            // For example, "content://com.example.android.pets/pets/3" matches, but
            // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
            sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
        }

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     * <p>
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     * <p>
     * <p>If you use SQLite, {@link SQLiteOpenHelper}
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * {@link SQLiteOpenHelper#getReadableDatabase} or
     * {@link SQLiteOpenHelper#getWritableDatabase}
     * from this method.  (Instead, override
     * {@link SQLiteOpenHelper#onOpen} to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Implement this to handle query requests from clients.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     *
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     *
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     *
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Get readable DB
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the Query
        Cursor cursor;

        // Figure out if the URI Matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match)
            {
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                case PETS:
                        cursor = db.query(
                            PetContract.PetsEntry.TABLE_PET_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                        break;
                case PETS_ID:
                    // For the PET_ID code, extract out the ID from the URI.
                    // For an example URI such as "content://com.example.android.pets/pets/3",
                    // the selection will be "_id=?" and the selection argument will be a
                    // String array containing the actual ID of 3 in this case.
                    //
                    // For every "?" in the selection, we need to have an element in the selection
                    // arguments that will fill in the "?". Since we have 1 question mark in the
                    // selection, we have 1 String in the selection arguments' String array.
                    selection = PetContract.PetsEntry._ID + "=?";
                    selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                    // This will perform a query on the pets table where the _id equals 3 to return a
                    // Cursor containing that row of the table.
                    cursor = db.query(
                        PetContract.PetsEntry.TABLE_PET_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                    break;
                default:
                    throw new IllegalArgumentException("Can't Query unknown URUI " + uri);
            }

        // Se Notification URI on the Cursor, so we know what tehe content URI the
        // Cursor was created for. If the data at this URI changes, then we know
        // we need to update the URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the Cursor
        return cursor;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match)
            {
                case PETS:
                    return PetContract.PetsEntry.CONTENT_LIST_TYPE;
                case PETS_ID:
                    return PetContract.PetsEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues contentValues)
        {
            // Getting the values from the contentValues passed as the input argument
            // to make sure that none of them are of the incorrect values
            String name = contentValues.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);
            Integer gender = contentValues.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);
            Integer weight = contentValues.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);

            // Checking one by one if the input values are incorrect
            if(name == null)
                {
                    throw new IllegalArgumentException("Pet requires a name");
                }
            if (gender == null || !PetContract.PetsEntry.isValidGender(gender))
                {
                    throw new IllegalArgumentException("Pet requires valid gender");
                }
            if (weight != null && weight <= 0)
                {
                    throw new IllegalArgumentException("Please input the breed of the pet");
                }

            // No need to check the breed, any value is valid (including null).

            // Get writable DB
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Insert the new pet with the given values
            long newURI = db.insert(PetContract.PetsEntry.TABLE_PET_NAME, null, contentValues);

            // If the ID is -1, then the insertion failed. Log an error and return null.
            if (newURI == -1)
                {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

            // Notify all listeners that the data has changed for the pet content URI
            // uri: content://com.example.android.pets/pets
            getContext().getContentResolver().notifyChange(uri, null);

            // Return the new URI with the ID (of the newly inserted row) appended at the end
            return ContentUris.withAppendedId(uri, newURI);
        }

    /**
     * Implement this to handle requests to insert a new row.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match)
            {
                case PETS:
                    return insertPet(uri, values);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for : " + uri);
            }
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs
     * @return The number of rows affected.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        // Get writable DB
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        int match = sUriMatcher.match(uri);

        switch (match)
            {
                case PETS:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = db.delete(PetContract.PetsEntry.TABLE_PET_NAME,selection, selectionArgs);
                    if(rowsDeleted != 0 )
                        {
                            getContext().getContentResolver().notifyChange(uri, null);
                        }
                    return rowsDeleted;
                case PETS_ID:
                    // Delete a single row given by the ID in the URI
                    selection = PetContract.PetsEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = db.delete(PetContract.PetsEntry.TABLE_PET_NAME, selection, selectionArgs);
                    if(rowsDeleted != 0 )
                        {
                            getContext().getContentResolver().notifyChange(uri, null);
                        }
                    return rowsDeleted;
                default:
                    throw new IllegalArgumentException("Delete is not supported for " + uri);
            }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs)
        {
            // If there are no values to update, then don't try to update the database
            if (values.size() == 0)
                {
                    return 0;
                }

            // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
            // check that the name value is not null.
            if (values.containsKey(PetContract.PetsEntry.COLUMN_PET_NAME))
                {
                    String name = values.getAsString(PetContract.PetsEntry.COLUMN_PET_NAME);

                    // Checking one by one if the input values are incorrect
                    if(name == null)
                        {
                            throw new IllegalArgumentException("Pet requires a name");
                        }
                }

            // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
            // check that the gender value is valid.
            if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_GENDER))
                {
                    Integer gender = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_GENDER);

                    if (gender == null || !PetContract.PetsEntry.isValidGender(gender))
                        {
                            throw new IllegalArgumentException("Pet requires valid gender");
                        }
                }

            // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
            // check that the weight value is valid.
            if(values.containsKey(PetContract.PetsEntry.COLUMN_PET_WEIGHT))
                {
                    Integer weight = values.getAsInteger(PetContract.PetsEntry.COLUMN_PET_WEIGHT);

                    if (weight != null && weight <= 0)
                        {
                            throw new IllegalArgumentException("Please input the breed of the pet");
                        }
                }

            // Otherwise, get writeable database to update the data
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Perform the update on the DB and get the number of rows affected
            int rowsUpdated = db.update(PetContract.PetsEntry.TABLE_PET_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if(rowsUpdated != 0)
                {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

            // Return the number of rows updated
            return rowsUpdated;
        }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match)
            {
                case PETS:
                    return update(uri, values, selection, selectionArgs);
                case PETS_ID:
                    // For the PET_ID code, extract out the ID from the URI,
                    // so we know which row to update. Selection will be "_id=?" and selection
                    // arguments will be a String array containing the actual ID
                    selection = PetContract.PetsEntry._ID + "=?";
                    selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                    return updatePet(uri, values, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
    }
}
