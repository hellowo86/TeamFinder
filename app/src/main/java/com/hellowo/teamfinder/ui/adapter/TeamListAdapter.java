package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.data.TeamsLiveData;
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.model.Team;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {
    private Context context;
    private List<Team> mContentsList;
    private AdapterInterface adapterInterface;
    private int hashTagColor;

    public TeamListAdapter(Context context, AdapterInterface adapterInterface) {
        this.context = context;
        this.adapterInterface = adapterInterface;
        mContentsList = TeamsLiveData.get().getValue();
        hashTagColor = context.getResources().getColor(R.color.colorAccent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView nameText;
        TextView titleText;
        TextView subText;
        ImageView profileImage;
        ImageView gameIconImg;
        TextView memberCountText;
        TextView activeTimeText;

        public ViewHolder(View v) {
            super(v);
            container = v;
            nameText = (TextView) v.findViewById(R.id.nameText);
            titleText = (TextView) v.findViewById(R.id.titleText);
            subText = (TextView) v.findViewById(R.id.subText);
            profileImage = (ImageView) v.findViewById(R.id.profileImage);
            gameIconImg = (ImageView) v.findViewById(R.id.gameIconImg);
            memberCountText = (TextView) v.findViewById(R.id.memberCountText);
            activeTimeText = (TextView) v.findViewById(R.id.activeTimeText);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.view_team_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Team team = mContentsList.get(position);
        final Member organizer = team.getMembers().get(0);

        holder.titleText.setText(team.getTitle());
        holder.nameText.setText(organizer.getName());
        holder.gameIconImg.setImageResource(GameData.get().getGame(team.getGameId()).getIconId());
        holder.memberCountText.setText(team.makeMemberText());
        holder.activeTimeText.setText(team.makeActiveTimeText());

        HashTagHelper tagHelper = HashTagHelper.Creator.create(hashTagColor,
                hashTag -> {/*click tag*/});
        tagHelper.handle(holder.subText);

        if(!TextUtils.isEmpty(team.getDescription())) {
            holder.subText.setVisibility(View.VISIBLE);
            holder.subText.setText(team.getDescription());
        }else {
            holder.subText.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(!TextUtils.isEmpty(organizer.getUserId()) ? organizer.getPhotoUrl() : R.drawable.default_profile)
                .bitmapTransform(new CropCircleTransformation(context))
                .thumbnail(0.1f)
                .into(holder.profileImage);

        holder.container.setOnClickListener(v ->
                adapterInterface.onItemClicked(team));
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
        void onItemClicked(Team team);
    }
}