package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.button_new)
    FloatingActionButton button;
    @BindView(R.id.text_main_empty)
    TextView emptyText;
    private Uri mUri;
    private String[] mProjection;
    private InventoryAdapter inventoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        inventoryAdapter = new InventoryAdapter(this, null);
        listView.setEmptyView(emptyText);
        listView.setAdapter(inventoryAdapter);
        mUri = InventoryContract.InventoryEntry.CONTENT_URI;
        mProjection = new String[]{InventoryContract.InventoryEntry.COLUMN_ID, InventoryContract.InventoryEntry.COLUMN_NAME, InventoryContract.InventoryEntry.COLUMN_QUANTITY, InventoryContract.InventoryEntry.COLUMN_PRICE};
        LoaderManager.getInstance(this).initLoader(0, null, this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //When the item is clicked, open the detail view
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("data", ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open the detail view for creating a new item
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, mUri, mProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //If the cursor is not null, update the adapter
        if (cursor != null)
            inventoryAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        inventoryAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_data:
                addDummyData();
        }
        return super.onOptionsItemSelected(item);
    }

    //Add dummy data with dummy values
    public void addDummyData() {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_CONTACT, "0754555");
        values.put(InventoryContract.InventoryEntry.COLUMN_NAME, "Tony");
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, 145);
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, 10);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER, "pedi");
        getContentResolver().insert(mUri, values);
    }
}
