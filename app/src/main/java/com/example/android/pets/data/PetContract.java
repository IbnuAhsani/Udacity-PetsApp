package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Created by test-pc on 27-Feb-18.
 */

public class PetContract {

    private PetContract(){};

    public static abstract class PetsEntry implements BaseColumns
        {
             /* Defining the name of the Column Heading of the Table */
            public static final String _ID = BaseColumns._ID;
            public static final String TABLE_PET_NAME = "pets";
            public static final String COLUMN_PET_NAME = "name";
            public static final String COLUMN_PET_BREED = "breed";
            public static final String COLUMN_PET_GENDER = "gender";
            public static final String COLUMN_PET_WEIGHT = "weight";

            /* Defining the keywords that are going to be used for Querying */
            public static final String TEXT_TYPE = "TEXT";
            public static final String INTEGER_TYPE = "INTEGER";
            public static final String PRIMARY_KEY = "PRIMARY KEY";
            public static final String AUTOINCREMENT = "AUTOINCREMENT";
            public static final String NOT_NULL = "NOT NULL";
            public static final String DEFAULT = "DEFAULT";

            /* Possible values for Gender of Pets */
            public static final int GENDER_MALE = 1;
            public static final int GENDER_FEMALE = 2;
            public static final int GENDER_UNKNOWN = 0;
        }
}
