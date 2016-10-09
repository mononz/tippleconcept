package net.mononz.tipple.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import net.mononz.tipple.Tipple;
import net.mononz.tipple.R;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();

    private static final int notification_id = 0;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mNotifyManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getContext());
        mBuilder.setContentTitle(getContext().getString(R.string.app_name))
                .setContentText(getContext().getString(R.string.sync_database))
                .setColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                .setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setProgress(0, 0, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        mNotifyManager.notify(notification_id, mBuilder.build());

        syncProducts();
        syncProductImages();

        Tipple.getSession(getContext()).setLastUpdated(System.currentTimeMillis());
        mNotifyManager.cancel(notification_id);
    }

    private void syncProducts() {
        // retrofit api call, sync product table
    }

    private void syncProductImages() {
        // retrofit api call, sync product_images table
    }

    private String lastUpdatedAt(String tableName) {
        String UPDATED_AT = "updated_at";
        String value = "0";
        Database db = Database.getInstance(getContext().getApplicationContext());
        Cursor c = db.getReadableDatabase().query(tableName, new String[]{ UPDATED_AT }, null, null, null, null, UPDATED_AT + " ASC", "1");
        if (c != null) {
            if (c.moveToFirst()) {
                int idx_updated_at = c.getColumnIndex(UPDATED_AT);
                value = c.getString(idx_updated_at);
            }
            c.close();
        }
        return value;
    }

    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;
            onAccountCreated(newAccount);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount) {
        Log.d(LOG_TAG, "Account created - " + newAccount.name);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void updateNotification(String text) {
        mBuilder.setContentText("Updating " + text);
        mNotifyManager.notify(notification_id, mBuilder.build());
    }

}