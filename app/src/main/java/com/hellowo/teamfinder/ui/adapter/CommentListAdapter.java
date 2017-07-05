package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.databinding.ListItemCommentBinding;
import com.hellowo.teamfinder.model.Comment;
import com.hellowo.teamfinder.model.User;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private Context context;
    private List<Comment> mContentsList;
    private AdapterInterface adapterInterface;

    public CommentListAdapter(Context context, List<Comment> commentList,
                              AdapterInterface adapterInterface) {
        this.context = context;
        this.adapterInterface = adapterInterface;
        mContentsList = commentList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListItemCommentBinding binding;
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
                .inflate(R.layout.list_item_comment, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Comment comment = mContentsList.get(position);
        ListItemCommentBinding binding = holder.binding;

        SpannableString ss = new SpannableString(comment.getUserName() + " : " + comment.getText());
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.primaryText)),
                0, comment.getUserName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan( new StyleSpan(android.graphics.Typeface.BOLD),
                0, comment.getUserName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.meesageText.setText(ss);
        binding.activeTimeText.setText(comment.makeActiveTimeText());

        Glide.with(context)
                .load(User.makePublicPhotoUrl(comment.getUserId()))
                .bitmapTransform(new CropCircleTransformation(context))
                .thumbnail(0.1f)
                .placeholder(R.drawable.default_profile)
                .into(binding.profileImage);

        binding.profileImage.setOnClickListener(v ->{
            adapterInterface.onUserClicked(comment.getUserId());
        });
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
        void onUserClicked(String userId);
    }
}