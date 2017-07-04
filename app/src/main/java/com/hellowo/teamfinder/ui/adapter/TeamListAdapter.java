package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.data.TeamsLiveData;
import com.hellowo.teamfinder.databinding.ViewTeamListItemBinding;
import com.hellowo.teamfinder.model.Game;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.ui.adapter.decoration.HorizontalDotDecoration;
import com.hellowo.teamfinder.utils.ViewUtil;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {
    private Context context;
    private List<Team> mContentsList;
    private AdapterInterface adapterInterface;
    private int hashTagColor;

    public TeamListAdapter(Context context, AdapterInterface adapterInterface) {
        this.context = context;
        this.adapterInterface = adapterInterface;
        mContentsList = TeamsLiveData.get().getValue();
        hashTagColor = context.getResources().getColor(R.color.primaryText);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewTeamListItemBinding binding;
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
                .inflate(R.layout.view_team_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Team team = mContentsList.get(position);
        final Game game = GameData.get().getGame(team.getGameId());
        final Member organizer = team.getMembers().get(0);
        final ViewTeamListItemBinding binding = holder.binding;

        binding.titleText.setText(team.getTitle());
        binding.nameText.setText(organizer.getName());
        binding.memberCountText.setText(team.makeMemberText());
        binding.activeTimeText.setText(team.makeActiveTimeText());

        HashTagHelper tagHelper = HashTagHelper.Creator.create(hashTagColor, null);
        tagHelper.handle(binding.subText);

        if(!TextUtils.isEmpty(team.getDescription())) {
            binding.subText.setVisibility(View.VISIBLE);
            binding.subText.setText(team.getDescription());
        }else {
            binding.subText.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(game.getIconId())
                .bitmapTransform(new GrayscaleTransformation(context))
                .into(binding.gameIconImg);
        binding.gameText.setText(game.getTitle());

        Glide.with(context)
                .load(!TextUtils.isEmpty(organizer.getUserId()) ? organizer.getPhotoUrl() : R.drawable.default_profile)
                .bitmapTransform(new CropCircleTransformation(context))
                .thumbnail(0.1f)
                .into(binding.profileImage);

        holder.container.setOnClickListener(v ->{
            adapterInterface.onItemClicked(team);
        });
    }

    private void joinTeam(Team team) {
        FirebaseDatabase.getInstance().getReference()
                .child("pushToken")
                .child(team.getMembers().get(0).getUserId())
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                new Thread(() -> {
                                    try {
                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                            String pushToken = (String) postSnapshot.getValue();
                                            JSONArray registration_ids = new JSONArray();
                                            registration_ids.put(pushToken);

                                            JSONObject data = new JSONObject();
                                            data.put("subject", "haha");
                                            data.put("message", "22222");

                                            FormBody.Builder bodyBuilder = new FormBody.Builder();
                                            bodyBuilder.add("to", pushToken);
                                            bodyBuilder.add("data", data.toString());
                                            Request request = new Request.Builder()
                                                    .url("https://fcm.googleapis.com/fcm/send")
                                                    .addHeader("Authorization", "key=AAAAf48YPzE:APA91bHTTAW9NmbxcijSrey7FCMYt20PvO-hKl23ge5ZnPrGCTUlLiArHxu3_g_P20Vi91eT7ym_1UAYBnBbkLOMoM8gm-eRkBPrSxmRy-bv47usBC_MliCjjSCzkqnpj7sSSWIWHWAB")
                                                    .post(bodyBuilder.build())
                                                    .build();
                                            new OkHttpClient().newCall(request).execute();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
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
        void onItemClicked(Team team);
    }
}