package net.mononz.tipple.sync;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import net.mononz.tipple.BuildConfig;

public class Database extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "tipple.sqlite3";

    private static Database mInstance;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, BuildConfig.DB_VERSION);
        setForcedUpgrade();
    }

    public static Database getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new Database(ctx.getApplicationContext());
        }
        return mInstance;
    }

    static Uri buildUri(Uri contentUri, long _id) {
        return ContentUris.withAppendedId(contentUri, _id);
    }

}