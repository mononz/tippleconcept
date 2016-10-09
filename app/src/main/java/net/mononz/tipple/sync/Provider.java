package net.mononz.tipple.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import net.mononz.tipple.sync.database.order_details;
import net.mononz.tipple.sync.database.order_products;
import net.mononz.tipple.sync.database.product;
import net.mononz.tipple.sync.database.product_image;

public class Provider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "net.mononz.tipple";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private Database mOpenHelper;

    private static final int ORDER = 100;
    private static final int ORDER_PRODUCTS = 200;
    private static final int PRODUCT = 400;
    private static final int PRODUCT_IMAGES = 500;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, order_details.TABLE_NAME, ORDER);
        matcher.addURI(authority, order_products.TABLE_NAME, ORDER_PRODUCTS);
        matcher.addURI(authority, product.TABLE_NAME, PRODUCT);
        matcher.addURI(authority, product_image.TABLE_NAME, PRODUCT_IMAGES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = Database.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDER:
                return order_details.CONTENT_DIR_TYPE;
            case ORDER_PRODUCTS:
                return order_products.CONTENT_DIR_TYPE;
            case PRODUCT:
                return product.CONTENT_DIR_TYPE;
            case PRODUCT_IMAGES:
                return product_image.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ORDER:
                SQLiteQueryBuilder ORDER = new SQLiteQueryBuilder();
                ORDER.setTables(order_details.TABLE_NAME);
                retCursor = ORDER.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ORDER_PRODUCTS:
                SQLiteQueryBuilder ORDER_PRODUCTS = new SQLiteQueryBuilder();
                ORDER_PRODUCTS.setTables(order_products.TABLE_NAME +
                        " LEFT JOIN " + product.TABLE_NAME + " ON " + order_products.FK_PRODUCT_ID + "=" + product.FULL_ID +
                        " LEFT JOIN " + product_image.TABLE_NAME + " ON " + product.main_image + " = " + product_image.FULL_ID);
                retCursor = ORDER_PRODUCTS.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT:
                SQLiteQueryBuilder PRODUCT = new SQLiteQueryBuilder();
                PRODUCT.setTables(product.TABLE_NAME);
                retCursor = PRODUCT.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;
        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }
        switch (sUriMatcher.match(uri)) {
            case ORDER_PRODUCTS:
                numUpdated = db.update(order_products.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d("Update", "notify changed (" + numUpdated + ")");
        }
        return numUpdated;
    }

}