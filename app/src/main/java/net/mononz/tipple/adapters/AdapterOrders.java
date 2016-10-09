package net.mononz.tipple.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mononz.tipple.library.CursorRecAdapter;
import net.mononz.tipple.R;
import net.mononz.tipple.sync.database.order_products;
import net.mononz.tipple.sync.database.product;
import net.mononz.tipple.sync.database.product_image;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterOrders extends CursorRecAdapter<AdapterOrders.ShowViewHolder> {

    private Context mContext;
    private Callback mCallback;
    private int order_id = -1;

    public AdapterOrders(Context mContext, Cursor cursor, int order_id, Callback callback) {
        super(cursor);
        this.order_id = order_id;
        this.mContext = mContext;
        this.mCallback = callback;
    }

    @Override
    public void onBindViewHolder(final ShowViewHolder viewHolder, final Cursor cursor) {

        int idx_id = cursor.getColumnIndex(product._id);
        int idx_name = cursor.getColumnIndex(product.name);
        int idx_thumb = cursor.getColumnIndex(product_image.path);
        int idx_quantity = cursor.getColumnIndex(order_products.quantity);
        int idx_volume = cursor.getColumnIndex(product.volume);
        int idx_added = cursor.getColumnIndex(order_products.added);

        final String name = cursor.getString(idx_name);
        viewHolder.vName.setText(name);
        viewHolder.vPortrait.setContentDescription(name);

        final String quantity = cursor.getString(idx_quantity) + " x " + cursor.getString(idx_volume) + "mL";
        viewHolder.vQuantity.setText(quantity);

        final String thumb = cursor.getString(idx_thumb);
        Glide.with(mContext)
                .load(thumb)
                .crossFade()
                .centerCrop()
                .into(viewHolder.vPortrait);

        final int added = cursor.getInt(idx_added);
        viewHolder.vAdded.setVisibility(View.GONE);
        if (added == 1) {
            viewHolder.vAdded.setVisibility(View.VISIBLE);
        }

        final long _id = cursor.getLong(idx_id);
        viewHolder.vView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.tickProduct(order_id, _id, (added == 1) ? 0 : 1);
            }
        });
    }

    @Override
    public ShowViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_product, viewGroup, false);
        return new ShowViewHolder(itemView);
    }

    class ShowViewHolder extends RecyclerView.ViewHolder {

        View vView;
        @BindView(R.id.portrait) ImageView vPortrait;
        @BindView(R.id.added) ImageView vAdded;
        @BindView(R.id.quantity) TextView vQuantity;
        @BindView(R.id.name) TextView vName;

        ShowViewHolder(View v) {
            super(v);
            vView = v;
            ButterKnife.bind(this, v);
        }
    }

    public interface Callback {
        void tickProduct(int order_id, long product_id, int tick);
    }

}