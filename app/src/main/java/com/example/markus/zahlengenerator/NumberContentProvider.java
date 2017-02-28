package com.example.markus.zahlengenerator;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;


import static com.example.markus.zahlengenerator.NumberContract.NumberEntry.*;

/**
 * Created by markus on 26.02.2017.
 */

public class NumberContentProvider extends ContentProvider {


    public static final int NUMBERS = 100;
    public static final int NUMBER_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(NumberContract.AUTHORITY, NumberContract.PATH_NUMBERS, NUMBERS);
        uriMatcher.addURI(NumberContract.AUTHORITY, NumberContract.PATH_NUMBERS + "/#", NUMBER_WITH_ID);

        return uriMatcher;
    }


    private NumberDbHelper numberDbHelper;

    @Override
    public boolean onCreate() {

        this.numberDbHelper = new NumberDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = numberDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match) {
            case NUMBERS:

                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NUMBER_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor = db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Nicht bekannt: " + uri);
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = numberDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case NUMBERS:
                long id = db.insert(TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else
                    throw new android.database.SQLException("Insert fehlgeschlagen! " + uri);

                break;

            default:
                throw new UnsupportedOperationException("URI nicht bekannt: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = numberDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int numberDeleted; // starts as 0
        String id;
        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case NUMBER_WITH_ID:
                // Get the task ID from the URI path
                 id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                numberDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unbekannte  delete URI: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (numberDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return numberDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
