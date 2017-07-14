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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class RolesAdapter extends RecyclerView.Adapter<RolesAdapter.ViewHolder> {
    private Context context;
    private ArrayMap<String, Integer> mContentsList;
    private AdapterInterface adapterInterface;
    private boolean isEditable;
    private boolean isFullMember;
    private boolean isOnlyOneMember;

    public RolesAdapter(Context context, boolean isEditable, Map<String, Integer> items,
                        AdapterInterface adapterInterface) {
        this.context = context;
        this.isEditable = isEditable;
        this.adapterInterface = adapterInterface;
        mContentsList = new ArrayMap<>();
        mContentsList.putAll(items);
    }

    public void refresh(Map<String, Integer> roles) {
        mContentsList.clear();
        mContentsList.putAll(roles);
        notifyDataSetChanged();
    }

    public void setIsFullMember(Boolean isFullMember) {
        this.isFullMember = isFullMember;
        notifyDataSetChanged();
    }

    public void setIsOnlyOneMember(Boolean isOnlyOneMember) {
        this.isOnlyOneMember = isOnlyOneMember;
        notifyDataSetChanged();
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

        if(isEditable) {
            binding.plusBtn.setVisibility(View.VISIBLE);
            binding.minusBtn.setVisibility(View.VISIBLE);
            binding.joinBtn.setVisibility(View.GONE);
            binding.countText.setText(String.format(context.getString(R.string.need_member_count), count));
            binding.minusBtn.setOnClickListener(v ->adapterInterface.onChangedRoleCount(role, -1));
            binding.plusBtn.setOnClickListener(v ->adapterInterface.onChangedRoleCount(role, 1));
            binding.roloText.setOnClickListener(v->adapterInterface.onClickedRole(role, count));
            binding.plusBtn.setEnabled(!isFullMember);
            binding.minusBtn.setEnabled(!isOnlyOneMember);

            if(isFullMember) {
                binding.plusBtn.setAlpha(0.2f);
            }else {
                binding.plusBtn.setAlpha(1f);
            }

            if(isOnlyOneMember) {
                binding.minusBtn.setAlpha(0.2f);
            }else {
                binding.minusBtn.setAlpha(1f);
            }
        }else {
            binding.plusBtn.setVisibility(View.GONE);
            binding.minusBtn.setVisibility(View.GONE);
            binding.joinBtn.setVisibility(View.VISIBLE);
            binding.countText.setText(String.format(context.getString(R.string.remain_member_count), count));
        }
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
        void onChangedRoleCount(String role, int count);
        void onClickedRole(String role, int count);
    }
}