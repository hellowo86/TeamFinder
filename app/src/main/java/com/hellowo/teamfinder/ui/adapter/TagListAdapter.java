package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.HashTag;

import java.util.List;


public class TagListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HashTag> mContentsList;
    private boolean isEditable;
    private AdapterInterface adapterInterface;

    public TagListAdapter(Context context, boolean isEditable, List<HashTag> mContentsList,
                          AdapterInterface adapterInterface) {
        this.context = context;
        this.mContentsList = mContentsList;
        this.isEditable = isEditable;
        this.adapterInterface = adapterInterface;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView titleText;
        ImageView imageView;

        public ItemViewHolder(View v) {
            super(v);
            container = v;
            titleText = (TextView) v.findViewById(R.id.titleText);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.list_item_basic, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final HashTag hashTag = mContentsList.get(position);
        ItemViewHolder holder = (ItemViewHolder)viewHolder;
        holder.titleText.setText(hashTag.getName());
        holder.imageView.setImageResource(hashTag.getIconId());
        holder.container.setOnClickListener(v -> adapterInterface.onItemClicked(hashTag));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mContentsList.size();
    }

    public interface AdapterInterface {
        void onItemClicked(HashTag hashTag);
    }
}