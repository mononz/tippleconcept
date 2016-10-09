package net.mononz.tipple.views;
 
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager; 
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader; 
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager; 
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View; 
import android.view.ViewGroup;

import net.mononz.tipple.R;
import net.mononz.tipple.adapters.AdapterOrders;
import net.mononz.tipple.sync.database.order_products;
import net.mononz.tipple.sync.database.product;
import net.mononz.tipple.sync.database.product_image;

import butterknife.BindView; 
import butterknife.ButterKnife; 
 
public class Fragment_Orders_Pager extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
 
    public Fragment_Orders_Pager() { }
 
    @BindView(R.id.recycler) RecyclerView recycler;
 
    private AdapterOrders mAdapter;
    private int mOrderId = -1;
 
    private static final String ARG_ORDER_ID = "order";
 
    static Fragment_Orders_Pager newInstance(int order_id) {
        Fragment_Orders_Pager f = new Fragment_Orders_Pager();
        Bundle b = new Bundle();
        b.putInt(ARG_ORDER_ID, order_id);
        f.setArguments(b);
        return f; 
    } 
 
    @Override 
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        View rootView = inflater.inflate(R.layout.fragment_order_pager, container, false);
        ButterKnife.bind(this, rootView);
        mOrderId = getArguments().getInt(ARG_ORDER_ID);
        mAdapter = new AdapterOrders(getActivity(), null, mOrderId, new AdapterOrders.Callback() {
            @Override
            public void tickProduct(int order_id, long product_id, int tick) {
                //Tipple.sendAction(getString(R.string.analytics_action_hero), Long.toString(_id), name);
                ContentValues values = new ContentValues();
                values.put(order_products.added, tick);
                getActivity().getContentResolver().update(order_products.CONTENT_URI, values,
                        order_products.fk_order_id + "=? AND " + order_products.fk_product_id + "=?",
                        new String[]{Integer.toString(order_id), Long.toString(product_id)});
            }
        });

        GridLayoutManager llm = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.hero_grid_columns), LinearLayoutManager.VERTICAL, false);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(llm);
        recycler.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSupportLoaderManager().initLoader(mOrderId, getArguments(), Fragment_Orders_Pager.this);
    }
 
    @Override 
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), order_products.CONTENT_URI,
                new String[]{ product.FULL_ID, product.name, product.volume, order_products.quantity, product_image.path, order_products.added },
                order_products.fk_order_id + "=?", new String[]{ Integer.toString(mOrderId)},
                product.name + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}