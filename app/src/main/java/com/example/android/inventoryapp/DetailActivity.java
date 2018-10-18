package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.text_detail_contact)
    TextInputEditText edit_contact;
    @BindView(R.id.text_detail_name)
    TextInputEditText edit_name;
    @BindView(R.id.text_detail_price)
    TextInputEditText edit_price;
    @BindView(R.id.text_detail_quantity)
    TextView text_quantity;
    @BindView(R.id.text_detail_supplier)
    TextInputEditText edit_supplier;
    @BindView(R.id.button_detail_add)
    Button btn_add;
    @BindView(R.id.button_detail_subtract)
    Button btn_subtract;
    @BindView(R.id.button_detail_image)
    FloatingActionButton btn_image;
    @BindView(R.id.image_detail_item)
    ImageView image_item;
    private int loadChange;
    private Uri mUri;
    private String[] mProjection;
    private boolean hasChanged = false;
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //Increase loadChange by one every time text changes
            loadChange++;
            if (mUri != null) {
                //If updating item, wait until all items are loaded to change status
                if (loadChange > 5)
                    hasChanged = true;
            } else {
                //If creating item, wait until 'quantity' is loaded to change status
                if (loadChange > 1)
                    hasChanged = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mUri = getIntent().getParcelableExtra("data");
        mProjection = InventoryContract.InventoryEntry.PROJECTION_ALL;
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increase quantity by one when clicked
                text_quantity.setText(String.valueOf(Integer.valueOf(text_quantity.getText().toString()) + 1));
            }
        });
        btn_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If quantity is greater than zero, decrease by one when clicked
                int qty = Integer.valueOf(text_quantity.getText().toString());
                if (qty > 0)
                    text_quantity.setText(String.valueOf(qty - 1));
            }
        });
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Select an image when to be stored
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
        //Listen for change in text
        edit_name.addTextChangedListener(watcher);
        edit_price.addTextChangedListener(watcher);
        edit_supplier.addTextChangedListener(watcher);
        edit_contact.addTextChangedListener(watcher);
        text_quantity.addTextChangedListener(watcher);
        if (mUri != null) {
            //Do not pop the keyboard up when entering screen
            image_item.requestFocus();
            setTitle(getString(R.string.title_edit));
            //Load data from the database
            LoaderManager.getInstance(this).initLoader(0, null, this);
        } else {
            setTitle(getString(R.string.title_new));
            //Set the quantity to zero when creating new items
            text_quantity.setText("0");
        }

    }

    //Get image from storage and load it into ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                Uri image = data.getData();
                Picasso.get().load(image).resize(image_item.getWidth(), image_item.getHeight()).into(image_item);
                //Count this as an edit
                hasChanged = true;
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(getApplicationContext(), mUri, mProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //Select an item from the database and populate the fields with the details
        if (cursor != null) {
            cursor.moveToFirst();
            edit_contact.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_CONTACT)));
            edit_name.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME)));
            edit_price.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE))));
            text_quantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY))));
            edit_supplier.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER)));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE));
            //If an image was saved, set it, otherwise set a predefined image
            if (image != null && image.length > 0) {
                image_item.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            } else {
                image_item.setImageResource(R.drawable.ic_image);
            }
        }
        //Load data only once
        LoaderManager.getInstance(this).destroyLoader(0);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        MenuItem itemDelete = menu.findItem(R.id.action_detail_delete);
        MenuItem itemContact = menu.findItem(R.id.action_detail_contact);
        //If creating a new item, make delete and contact options unavailable
        if (mUri == null) {
            itemDelete.setVisible(false);
            itemContact.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail_save:
                //Save the item
                saveItem();
                break;
            case R.id.action_detail_delete:
                //Delete the item
                deleteItem();
                break;
            case android.R.id.home:
                //Up button pressed
                onBackPressed();
                return true;
            case R.id.action_detail_contact:
                //Contact the supplier
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + edit_contact.getText().toString()));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItem() {
        //Ensure all fields are filled
        if (TextUtils.isEmpty(edit_name.getText().toString().trim()) || TextUtils.isEmpty(edit_price.getText().toString().trim()) || TextUtils.isEmpty(edit_supplier.getText().toString().trim()) || TextUtils.isEmpty(edit_contact.getText().toString().trim())) {
            Toast.makeText(this, R.string.detail_fill_all, Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, text_quantity.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER, edit_supplier.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_PRICE, edit_price.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_NAME, edit_name.getText().toString());
        values.put(InventoryContract.InventoryEntry.COLUMN_CONTACT, edit_contact.getText().toString());
        //If image is selected, save it
        if (image_item.getDrawable() != null && !image_item.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_image).getConstantState())) {
            BitmapDrawable drawable = (BitmapDrawable) image_item.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] image = outputStream.toByteArray();
            values.put(InventoryContract.InventoryEntry.COLUMN_IMAGE, image);
        }
        //If editing, update, if creating new, insert
        if (mUri != null) {
            getContentResolver().update(mUri, values, null, null);
            Toast.makeText(this, R.string.detail_successful_update, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            Toast.makeText(this, R.string.detail_successful_add, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    private void deleteItem() {
        //Prompt the user to confirm delete
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.detail_delete_prompt) + edit_name.getText().toString() + "?");
        builder.setNegativeButton(R.string.detail_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getContentResolver().delete(mUri, null, null);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        builder.setPositiveButton(R.string.detail_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exit() {
        //If content has been edited, prompt user for confirmation on trying to exit, otherwise move to parent activity
        if (hasChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.detail_exit_prompt);
            final Activity source = this;
            builder.setNegativeButton(R.string.detail_exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NavUtils.navigateUpFromSameTask(source);
                }
            });
            builder.setPositiveButton(getString(R.string.detail_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else
            NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

}
