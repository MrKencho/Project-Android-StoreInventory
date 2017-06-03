package com.example.android.storeinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventory.Data.InventoryContract;

public class DetailedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int ITEM_LOADER = 2;
    private Uri mCurrentItemUri;
    public static int currentQty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

        Button orderButton = (Button) findViewById(R.id.order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText order = (EditText) findViewById(R.id.orderQty);
                String orderQty = order.getText().toString();
                if (orderQty.isEmpty()) {
                    Toast.makeText(DetailedActivity.this, "Please enter the quantity first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int qty = Integer.parseInt(orderQty);
                if (qty <= 0) {
                    Toast.makeText(DetailedActivity.this, "Order amount cannot be less than 1", Toast.LENGTH_SHORT).show();
                } else {
                    String orderDetails = "Dear Seler please send " + String.valueOf(qty) + " Items.";
                    Intent email = new Intent(Intent.ACTION_SENDTO);
                    email.setAction(Intent.ACTION_SENDTO);
                    email.setData(Uri.parse("mailto:"));
                    email.putExtra(Intent.EXTRA_EMAIL, "baba@22fma.com");
                    email.putExtra(Intent.EXTRA_SUBJECT, "My order");
                    email.putExtra(Intent.EXTRA_TEXT, orderDetails);
                    if (email.resolveActivity(getPackageManager()) != null)
                        startActivity(email);
                }
            }
        });

        Button addQuantity = (Button) findViewById(R.id.addQty);
        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText add = (EditText) findViewById(R.id.add_qty);
                String addQty = add.getText().toString();
                if (addQty.isEmpty()) {
                    Toast.makeText(DetailedActivity.this, "Enter the Quantity First", Toast.LENGTH_SHORT).show();
                    return;
                }
                int qt = Integer.parseInt(addQty);
                if (qt > 0) {
                    qt += currentQty;
                    updateQty(qt);
                    Intent intent = new Intent(DetailedActivity.this, DetailedActivity.class);
                    Uri currentItemUri = mCurrentItemUri;
                    intent.setData(currentItemUri);
                    startActivity(intent);
                } else
                    Toast.makeText(DetailedActivity.this, "Enter valid qty to be added", Toast.LENGTH_SHORT).show();
            }
        });

        Button saleMade = (Button) findViewById(R.id.sell);
        saleMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQty > 0) {
                    EditText sell = (EditText) findViewById(R.id.sellItem);
                    String sellQty = sell.getText().toString();
                    if (sellQty.isEmpty()) {
                        Toast.makeText(DetailedActivity.this, "Enter the Quantity First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int qt = Integer.parseInt(sellQty);
                    if (qt <= currentQty) {
                        qt = currentQty - qt;
                        currentQty = qt;
                        updateQty(qt);
                        Intent intent = new Intent(DetailedActivity.this, DetailedActivity.class);
                        Uri currentItemUri = mCurrentItemUri;
                        intent.setData(currentItemUri);
                        startActivity(intent);
                    } else
                        Toast.makeText(DetailedActivity.this, "Items cannot be negative", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(DetailedActivity.this, "Item is out of Stock,order from supplier first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.ItemEntry._ID,
                InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ItemEntry.COLUMN_PRICE,
                InventoryContract.ItemEntry.COLUMN_QUANTITY,
                InventoryContract.ItemEntry.COLUMN_IMAGE
        };
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    public void updateQty(int qty) {
        ContentValues qt = new ContentValues();
        qt.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, String.valueOf(qty));
        getContentResolver().update(mCurrentItemUri, qt, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_IMAGE);

            String itemName = cursor.getString(nameColumnIndex);
            String itemQty = cursor.getString(quantityColumnIndex);
            currentQty = Integer.parseInt(itemQty);
            String itemPrice = cursor.getString(priceColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);

            TextView name = (TextView) findViewById(R.id.detailName);
            name.setText(itemName);
            TextView qty = (TextView) findViewById(R.id.detailQty);
            qty.setText(itemQty);
            TextView price = (TextView) findViewById(R.id.detailPrice);
            price.setText(itemPrice);
            ImageView img = (ImageView) findViewById(R.id.detail_image);
            img.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        TextView name = (TextView) findViewById(R.id.detailName);
        name.setText("");
        TextView qty = (TextView) findViewById(R.id.detailQty);
        qty.setText("");
        TextView price = (TextView) findViewById(R.id.detailPrice);
        price.setText("");
        ImageView img = (ImageView) findViewById(R.id.detail_image);
        img.setImageBitmap(null);
    }

    public void refresh() {
        Intent intent = new Intent(this, DetailedActivity.class);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_data:
                refresh();
                return true;
            case R.id.delete_it:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_success),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
