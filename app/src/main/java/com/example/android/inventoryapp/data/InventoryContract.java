package com.example.android.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    public static class InventoryEntry implements BaseColumns {
        //Authority
        public static final String AUTHORITY = "com.example.android.inventoryapp";
        //Content URI
        public static final Uri CONTENT_URI = Uri.parse("content://com.example.android.inventoryapp/inventory");
        //The name of the table
        public static final String TABLE_NAME = "inventory";
        //Column containing ids
        public static final String COLUMN_ID = BaseColumns._ID;
        //Column containing quantities
        public static final String COLUMN_QUANTITY = "quantity";
        //Column containing prices
        public static final String COLUMN_PRICE = "price";
        //Column containing names of items
        public static final String COLUMN_NAME = "name";
        //Column containing names of suppliers
        public static final String COLUMN_SUPPLIER = "supplier";
        //Column containing mobile numbers of suppliers
        public static final String COLUMN_CONTACT = "contact";
        //Column containing image items
        public static final String COLUMN_IMAGE = "image";
        //Return all columns
        public static final String[] PROJECTION_ALL = new String[]{COLUMN_ID, COLUMN_QUANTITY, COLUMN_PRICE, COLUMN_NAME, COLUMN_SUPPLIER, COLUMN_CONTACT, COLUMN_IMAGE};
    }
}
