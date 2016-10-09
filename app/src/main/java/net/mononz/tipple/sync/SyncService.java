package net.mononz.tipple.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sFishSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sFishSyncAdapter == null) {
                sFishSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFishSyncAdapter.getSyncAdapterBinder();
    }

}