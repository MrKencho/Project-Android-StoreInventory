package com.example.android.storeinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventory.Data.InventoryContract;

/**
 * Created by BaBa_RanChO on 31-05-2017.
 */

public class ItemCursorAdapter extends CursorAdapter {
    public Cursor c;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        c = cursor;
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry.COLUMN_QUANTITY);
        final int idColumnIndex = cursor.getColumnIndex(InventoryContract.ItemEntry._ID);

        String itemName = cursor.getString(nameColumnIndex);
        final String itemQty = cursor.getString(quantityColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        String itemSummmary = "Quantity = " + itemQty + "\nPrice = " + itemPrice;
        final int rowId = cursor.getInt(idColumnIndex);

        nameTextView.setText(itemName);
        summaryTextView.setText(itemSummmary);

        final Uri mCurrentItemUri = ContentUris.withAppendedId(InventoryContract.ItemEntry.CONTENT_URI, rowId);

        Button reduceQtyButton = (Button) view.findViewById(R.id.reduce_qty);
        reduceQtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(itemQty);
                if (qty >= 1)
                    qty--;
                else {
                    Toast.makeText(context, "Quantity can't be negative or zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues quant = new ContentValues();
                quant.put(InventoryContract.ItemEntry.COLUMN_QUANTITY, String.valueOf(qty));
                int rowsUpdated = context.getContentResolver().update(mCurrentItemUri, quant, null, null);
                if (rowsUpdated == 0)
                    Toast.makeText(context, "Some Problem occured while reducing quantity", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "SOLD!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
