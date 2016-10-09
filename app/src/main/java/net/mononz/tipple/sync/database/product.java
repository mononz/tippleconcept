package net.mononz.tipple.sync.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import net.mononz.tipple.sync.Provider;

public final class product implements BaseColumns {

    public static final String TABLE_NAME = "product";

    public static final String _id = "_id";
    public static final String name = "name";
    public static final String barcode = "barcode";
    public static final String volume = "volume";
    public static final String main_image = "main_image";
    public static final String created_at = "created_at";
    public static final String updated_at = "updated_at";

    public static final String FULL_ID = TABLE_NAME + "." + _id;

    public static final Uri CONTENT_URI = Provider.BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Provider.CONTENT_AUTHORITY + "/" + TABLE_NAME;

}