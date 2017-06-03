package com.example.android.storeinventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.storeinventory.Data.InventoryContract;

import java.io.ByteArrayOutputStream;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;
    private byte[] itemImage;
    private Button imageButton;
    private int GET_IMAGE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_IMAGE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView testImage = (ImageView) findViewById(R.id.image_view);
            testImage.setImageBitmap(imageBitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Button imageButton = (Button) findViewById(R.id.add_image_button);
            imageButton.setVisibility(View.GONE);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            itemImage = stream.toByteArray();
        }
    }

    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        imageButton = (Button) findViewById(R.id.add_image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, GET_IMAGE);
                }
            }
        });
        itemName = (EditText) findViewById(R.id.new_item_name);
        itemQuantity = (EditText) findViewById(R.id.new_item_quantity);
        itemPrice = (EditText) findViewById(R.id.new_item_price);
        itemName.setOnTouchListener(mTouchListener);
        itemQuantity.setOnTouchListener(mTouchListener);
        itemPrice.setOnTouchListener(mTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    public void saveItem() {
        String nameString = itemName.getText().toString().trim();
        String priceString = itemPrice.getText().toString().trim();
        String quantityString = itemQuantity.getText().toString().trim();
        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, R.string.enter_all_values, Toast.LENGTH_SHORT).show();
            return;
        }
        if (itemImage == null) {
            Toast.makeText(this, "Please set an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryContract.ItemEntry.COLUMN_PRICE, priceString);
        values.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, quantityString);
        values.put(InventoryContract.ItemEntry.COLUMN_IMAGE, itemImage);
        Uri newUri = getContentResolver().insert(InventoryContract.ItemEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.insert_item_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.insert_success),
                    Toast.LENGTH_SHORT).show();
            saved = true;
        }
    }

    Boolean saved = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                if (saved)
                    finish();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddItemActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddItemActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
