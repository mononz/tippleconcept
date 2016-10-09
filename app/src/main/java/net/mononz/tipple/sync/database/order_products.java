package net.mononz.tipple.sync.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import net.mononz.tipple.sync.Provider;

public final class order_products implements BaseColumns {

    public static final String TABLE_NAME = "order_products";

    public static final String _id = "_id";
    public static final String fk_order_id = "fk_order_id";
    public static final String fk_product_id = "fk_product_id";
    public static final String quantity = "quantity";
    public static final String added = "added";

    public static final String FULL_ID = TABLE_NAME + "." + _id;
    public static final String FK_PRODUCT_ID = TABLE_NAME + "." + fk_product_id;

    public static final Uri CONTENT_URI = Provider.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Provider.CONTENT_AUTHORITY + "/" + TABLE_NAME;

}