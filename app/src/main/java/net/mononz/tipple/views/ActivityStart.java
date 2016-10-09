package net.mononz.tipple.views;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.mononz.tipple.R;
import net.mononz.tipple.Tipple;
import net.mononz.tipple.sync.database.order_products;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityStart extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

    }

    @Override
    public void onResume() {
        super.onResume();
        // update database if greater than sync threshold
        if (((Tipple) getApplicationContext()).timeForSync() && Tipple.isNetworkConnected(this)) {
            //SyncAdapter.initializeSyncAdapter(this);
            //SyncAdapter.syncImmediately(this);
        }
    }

    @OnClick(R.id.next)
    public void next() {
        ContentValues values = new ContentValues();
        values.put(order_products.added, 0);
        getContentResolver().update(order_products.CONTENT_URI, values, null, null);
        Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
        startActivity(intent);
    }

    @OnClick(R.id.recent)
    public void recent() {
        Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
        startActivity(intent);
    }

}