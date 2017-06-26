package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.model.Game;

import java.util.ArrayList;
import java.util.List;


public class RoleListAdapter extends RecyclerView.Adapter<RoleListAdapter.ViewHolder> {
    private Context context;
    private List<String> mContentsList;
    private RoleListAdapterInterface roleListAdapterInterface;

    public RoleListAdapter(Context context, Game game, RoleListAdapterInterface roleListAdapterInterface) {
        this.context = context;
        this.roleListAdapterInterface = roleListAdapterInterface;
        mContentsList = new ArrayList<>();
        mContentsList.add(context.getString(R.string.free_role));
        mContentsList.addAll(game.getRoles());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView titleText;
        TextView subText;
        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            container = v;
            titleText = (TextView) v.findViewById(R.id.titleText);
            subText = (TextView) v.findViewById(R.id.subText);
            imageView = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.view_normal_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String role = mContentsList.get(position);
        holder.titleText.setText(role);
        holder.imageView.setVisibility(View.GONE);
        holder.container.setOnClickListener(v1 ->
                roleListAdapterInterface.onItemClicked(role));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mContentsList.size();
    }

    public interface RoleListAdapterInterface{
        void onItemClicked(String role);
    }
}