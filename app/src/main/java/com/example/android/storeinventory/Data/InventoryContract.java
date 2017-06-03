package com.example.android.storeinventory.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by BaBa_RanChO on 31-05-2017.
 */

public final class InventoryContract {
    //private constructor to prevent accidental instantiation of Contract class
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.storeinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEMS = "inventory";

    public static final class ItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;

        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_IMAGE = "image";

    }

}
