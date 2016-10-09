package net.mononz.tipple.views;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.mononz.tipple.R;
import net.mononz.tipple.sync.database.order_details;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_Orders extends Fragment {

    @BindView(R.id.sliding_tabs) TabLayout slidingTabLayout;
    @BindView(R.id.viewpager) ViewPager pager;

    private final static String STORE_POS = "position";

    private int mPosition = 0;
    private ArrayList<Integer> ids;
    private ArrayList<String> titles;

    public Fragment_Orders() { }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, rootView);

        if (ids == null) {
            ids = new ArrayList<>();
            titles = new ArrayList<>();

            Cursor cursor = getActivity().getContentResolver().query(order_details.CONTENT_URI,
                    new String[]{order_details.FULL_ID, order_details.postcode, order_details.suburb},
                    null, null, order_details.FULL_ID + " ASC");  // order_details by id as most likely will be due first

            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    int idx_id = cursor.getColumnIndex(order_details._id);
                    int postcode = cursor.getColumnIndex(order_details.postcode);
                    int suburb = cursor.getColumnIndex(order_details.suburb);
                    ids.add(cursor.getInt(idx_id));
                    titles.add(cursor.getString(suburb) + ", " + cursor.getString(postcode));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STORE_POS)) {
            mPosition = savedInstanceState.getInt(STORE_POS);
        }
        displayList(titles, ids);
        return rootView;
    }

    private void displayList(final ArrayList<String> titles, final ArrayList<Integer> ids) {
        pager.setId(View.generateViewId());
        pager.setOffscreenPageLimit(titles.size());
        pager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            public int getCount() {
                return titles.size();
            }

            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }

            public Fragment getItem(int position) {
                return Fragment_Orders_Pager.newInstance(ids.get(position));
            }
        });

        slidingTabLayout.setupWithViewPager(pager);
        slidingTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        pager.setCurrentItem(mPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPosition = pager.getCurrentItem();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt(STORE_POS, mPosition);
    }

}