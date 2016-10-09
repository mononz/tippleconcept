package net.mononz.tipple.views;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Toast;

import net.mononz.tipple.R;
import net.mononz.tipple.sync.database.order_details;
import net.mononz.tipple.sync.database.order_products;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new Fragment_Orders())
                    .commit();
        }
    }

    @OnClick(R.id.fab)
    public void fab() {
        Cursor c = getContentResolver().query(order_products.CONTENT_URI, null, null, null, null);
        if (c != null) {
            int total = c.getCount();
            int count = 0;
            if (c.moveToFirst()) {
                do {
                    int idx_added = c.getColumnIndex(order_products.added);
                    if (c.getInt(idx_added) == 1)
                        count++;
                } while (c.moveToNext());
            }
            c.close();
            if (count != total) {
                Toast.makeText(ActivityMain.this, "Some products are not ticked!", Toast.LENGTH_SHORT).show();
                return;
            }
            alertWhereTo();
        } else {
            Toast.makeText(ActivityMain.this, "Error :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void alertWhereTo() {
        Cursor c = getContentResolver().query(order_details.CONTENT_URI,
                new String[]{ order_details.TABLE_NAME + ".*"},
                null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                ArrayList<String> addresses = new ArrayList<>();
                do {
                    int idx_id = c.getColumnIndex(order_details._id);
                    int idx_firstname = c.getColumnIndex(order_details.firstname);
                    int idx_lastname = c.getColumnIndex(order_details.lastname);
                    int idx_address = c.getColumnIndex(order_details.address);
                    int idx_suburb = c.getColumnIndex(order_details.suburb);
                    int idx_postcode = c.getColumnIndex(order_details.postcode);

                    addresses.add("#" + c.getString(idx_id) + "\n" +
                            c.getString(idx_firstname) + " " + c.getString(idx_lastname) + "\n" +
                            c.getString(idx_address) + "\n" +
                            c.getString(idx_suburb) + ", " + c.getString(idx_postcode));
                } while (c.moveToNext());

                new AlertDialog.Builder(ActivityMain.this)
                        .setTitle(getString(R.string.alert_title))
                        .setMessage(TextUtils.join("\n\n", addresses))
                        .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
            c.close();
        } else {
            Toast.makeText(ActivityMain.this, "Error :(", Toast.LENGTH_SHORT).show();
        }
    }

}