package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ListItemRoleBinding;

import java.util.List;


public class RolesAdapter extends RecyclerView.Adapter<RolesAdapter.ViewHolder> {
    private Context context;
    private ArrayMap<String, Integer> mContentsList;
    private AdapterInterface adapterInterface;

    public RolesAdapter(Context context, ArrayMap<String, Integer> items, AdapterInterface adapterInterface) {
        this.context = context;
        this.adapterInterface = adapterInterface;
        mContentsList = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListItemRoleBinding binding;
        View container;

        public ViewHolder(View v) {
            super(v);
            container = v;
            binding = DataBindingUtil.bind(v);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.list_item_role, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String role = mContentsList.keyAt(position);
        final int count = mContentsList.get(role);
        final ListItemRoleBinding binding = holder.binding;

        binding.roloText.setText(role);
        binding.countText.setText(String.valueOf(count));
        holder.container.setOnClickListener(v ->
                adapterInterface.onItemClicked(role, count));
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
        void onItemClicked(String item, int count);
    }
}