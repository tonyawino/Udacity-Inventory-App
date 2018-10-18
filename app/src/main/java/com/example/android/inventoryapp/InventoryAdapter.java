package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryAdapter extends CursorAdapter {
    @BindView(R.id.text_item_cost)
    TextView textView_cost;
    @BindView(R.id.text_item_quantity)
    TextView textView_quantity;
    @BindView(R.id.text_item_title)
    TextView textView_title;
    @BindView(R.id.button_item_sell)
    Button sell;
    private Context mContext;

    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
    }

    //Get data from the database and show it in a listview
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);
        int cost = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY));
        String title = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME));
        textView_cost.setText(mContext.getString(R.string.text_main_cost, cost));
        textView_quantity.setText(String.valueOf(quantity));
        textView_title.setText(title);
        final int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ID));
        final int qty = Integer.valueOf(String.valueOf(textView_quantity.getText().toString())) - 1;
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When the button is clicked and quantity is not less than 0, update quantity in database by reducing by 1
                if (qty >= 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, qty);
                    mContext.getContentResolver().update(ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id), contentValues, null, null);
                }
            }
        });
    }

}
