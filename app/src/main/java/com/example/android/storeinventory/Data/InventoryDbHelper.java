package com.example.android.storeinventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by BaBa_RanChO on 31-05-2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HABIT_TABLE = "CREATE TABLE " + InventoryContract.ItemEntry.TABLE_NAME + " (" +
                InventoryContract.ItemEntry._ID + " INTEGER PRIMARY KEY," +
                InventoryContract.ItemEntry.COLUMN_PRODUCT_NAME + " TEXT, " +
                InventoryContract.ItemEntry.COLUMN_QUANTITY + " INTEGER, " +
                InventoryContract.ItemEntry.COLUMN_PRICE + " INTEGER," +
                InventoryContract.ItemEntry.COLUMN_IMAGE + " BLOB)";
        db.execSQL(SQL_CREATE_HABIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
