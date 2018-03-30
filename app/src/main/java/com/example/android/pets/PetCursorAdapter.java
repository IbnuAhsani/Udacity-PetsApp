package com.example.android.pets;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

/**
 * Created by test-pc on 07-Mar-18.
 */

public class PetCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link PetCursorAdapter}.
     *
     * @param context The context
     * @param cursor       The cursor from which to get the data.
     */
    public PetCursorAdapter(Context context, Cursor cursor)
        {
            super(context, cursor, 0);
        }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.pet_list, parent, false);

    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView name = (TextView) view.findViewById(R.id.pet_name);
        TextView breed = (TextView) view.findViewById(R.id.pet_breed);

        // Read the pet attributes from the Cursor for the current pet
        String pet_name = cursor.getString(cursor.getColumnIndex(PetContract.PetsEntry.COLUMN_PET_NAME));
        String pet_breed = cursor.getString(cursor.getColumnIndex(PetContract.PetsEntry.COLUMN_PET_BREED));

        // Update the TextViews with the attributes for the current pet
        name.setText(pet_name);
        breed.setText(pet_breed);

    }
}
