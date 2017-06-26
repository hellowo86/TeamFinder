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
import com.hellowo.teamfinder.model.Member;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class MemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Member> mContentsList;
    private boolean isEditable;
    private MemberListInterface memberListInterface;

    final private static int TYPE_NORMAL = 0;
    final private static int TYPE_FOOTER = 1;

    public MemberListAdapter(Context context, boolean isEditable, List<Member> mContentsList,
                             MemberListInterface memberListInterface) {
        this.context = context;
        this.mContentsList = mContentsList;
        this.isEditable = isEditable;
        this.memberListInterface = memberListInterface;
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
            titleText = (TextView) v.findViewById(R.id.titleText);
            subText = (TextView) v.findViewById(R.id.subText);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        View container;

        public FooterViewHolder(View v) {
            super(v);
            container = v;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        switch(viewType){
            case TYPE_FOOTER:
                v = LayoutInflater.from(context)
                        .inflate(R.layout.view_member_list_footer, viewGroup, false);
                return new FooterViewHolder(v);
            default:
                v = LayoutInflater.from(context)
                        .inflate(R.layout.view_member_list_item, viewGroup, false);
                return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(getItemViewType(position)){
            case TYPE_NORMAL:
                final Member member = mContentsList.get(position);
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                holder.titleText.setText(member.getName());
                holder.subText.setText(member.getRole());

                if(!TextUtils.isEmpty(member.getPhotoUrl())) {
                    Glide.with(context).load(member.getPhotoUrl())
                            .thumbnail(0.1f)
                            .into(holder.imageView);
                }else {
                    holder.imageView.setImageResource(R.drawable.default_profile_circle);
                }

                holder.container.setOnClickListener(v ->{});

                if(isEditable) {
                    holder.deleteBtn.setOnClickListener(v ->{});
                }
                break;
            case TYPE_FOOTER:
                FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
                footerViewHolder.container.setOnClickListener(v -> memberListInterface.onAddClicked());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mContentsList.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mContentsList.size() + 1;
    }

    public interface MemberListInterface{
        void onAddClicked();
    }
}