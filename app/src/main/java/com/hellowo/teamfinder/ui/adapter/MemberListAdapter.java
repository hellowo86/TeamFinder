package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.MeLiveData;
import com.hellowo.teamfinder.model.Member;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class MemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Member> mContentsList;
    private boolean isEditable;
    private int layoutId;
    private AdapterInterface adapterInterface;

    public MemberListAdapter(Context context, boolean isEditable, List<Member> mContentsList,
                             int layoutId, AdapterInterface adapterInterface) {
        this.context = context;
        this.mContentsList = mContentsList;
        this.isEditable = isEditable;
        this.layoutId = layoutId;
        this.adapterInterface = adapterInterface;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView titleText;
        TextView subText;
        ImageView imageView;
        ImageButton deleteBtn;

        public ItemViewHolder(View v) {
            super(v);
            container = v;
            titleText = v.findViewById(R.id.titleText);
            subText = v.findViewById(R.id.subText);
            imageView = v.findViewById(R.id.imageView);
            deleteBtn = v.findViewById(R.id.deleteBtn);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context).inflate(layoutId, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Member member = mContentsList.get(position);
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.subText.setText(member.getRole());
        holder.titleText.setText(member.getName());

        if(!TextUtils.isEmpty(member.getPhotoUrl())) {
            Glide.with(context).load(member.getPhotoUrl())
                    .bitmapTransform(new CropCircleTransformation(context))
                    .placeholder(R.drawable.default_profile)
                    .into(holder.imageView);
        }else {
            Glide.with(context).load(R.drawable.default_profile)
                    .into(holder.imageView);
        }

        if(isEditable && !MeLiveData.INSTANCE.isMe(member)) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setOnClickListener(v -> adapterInterface.onDeleteClicked(member));
        }else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        holder.container.setOnClickListener(v -> adapterInterface.onItemClicked(member));
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
        void onDeleteClicked(Member member);
        void onItemClicked(Member member);
    }
}