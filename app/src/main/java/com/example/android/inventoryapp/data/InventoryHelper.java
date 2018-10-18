package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryHelper extends SQLiteOpenHelper {
    public static final int VERSION_NUMBER = 1;

    public InventoryHelper(Context context) {
        super(context, InventoryContract.InventoryEntry.TABLE_NAME + ".db", null, VERSION_NUMBER);
    }

    //Create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " (" + InventoryContract.InventoryEntry.COLUMN_ID +
                " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + InventoryContract.InventoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL, " + InventoryContract.InventoryEntry.COLUMN_QUANTITY +
                " INTEGER NOT NULL DEFAULT 0, " + InventoryContract.InventoryEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.COLUMN_CONTACT + " TEXT NOT NULL, " + InventoryContract.InventoryEntry.COLUMN_IMAGE +
                " BLOB);";
        db.execSQL(create);

    }

    //Upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop = "DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME + ";";
        db.execSQL(drop);
        onCreate(db);
    }
}
