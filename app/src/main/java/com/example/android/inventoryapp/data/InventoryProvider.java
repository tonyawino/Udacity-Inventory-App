package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class InventoryProvider extends ContentProvider {
    private InventoryHelper helper;
    private UriMatcher matcher;

    @Override
    public boolean onCreate() {
        helper = new InventoryHelper(getContext());
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Add the possible types of URIs to the matcher
        matcher.addURI(InventoryContract.InventoryEntry.AUTHORITY, "/inventory", 0);
        matcher.addURI(InventoryContract.InventoryEntry.AUTHORITY, "/inventory/#", 1);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;
        //Query the database depending on the uri provided
        switch (matcher.match(uri)) {
            case 0:
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case 1:
                selection = InventoryContract.InventoryEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    //Return the type depending on uri
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case 0:
                return "vnd.android.cursor.dir/inventory";
            case 1:
                return "vnd.android.cursor.item/inventory";
            default:
                throw new IllegalArgumentException("Unknown Uri");
        }
    }

    //Insert data to the database
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //Delete data from the database depending on the uri provided
        switch (matcher.match(uri)) {
            case 0:
                if (db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs) != 0)
                    Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case 1:
                selectionArgs = new String[]{uri.getLastPathSegment()};
                selection = InventoryContract.InventoryEntry.COLUMN_ID + "=?";
                if (db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs) != 0)
                    Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri");
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //Update data in the database depending on the uri provided
        switch (matcher.match(uri)) {
            case 0:
                db.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case 1:
                selectionArgs = new String[]{uri.getLastPathSegment()};
                selection = InventoryContract.InventoryEntry.COLUMN_ID + "=?";
                db.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }
}
