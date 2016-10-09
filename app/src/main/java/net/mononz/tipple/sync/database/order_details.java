package net.mononz.tipple.sync.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import net.mononz.tipple.sync.Provider;

public final class order_details implements BaseColumns {

    public static final String TABLE_NAME = "order_details";

    public static final String _id = "_id";
    public static final String firstname = "firstname";
    public static final String lastname = "lastname";
    public static final String address = "address";
    public static final String suburb = "suburb";
    public static final String postcode = "postcode";
    public static final String state = "state";
    public static final String country = "country";
    public static final String created_at = "created_at";
    public static final String completed_at = "completed_at";

    public static final String FULL_ID = TABLE_NAME + "." + _id;

    public static final Uri CONTENT_URI = Provider.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Provider.CONTENT_AUTHORITY + "/" + TABLE_NAME;

}