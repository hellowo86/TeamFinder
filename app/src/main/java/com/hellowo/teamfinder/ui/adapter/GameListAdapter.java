package com.hellowo.teamfinder.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellowo.teamfinder.R;
import com.hellowo.teamfinder.data.GameData;
import com.hellowo.teamfinder.model.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private Context context;
    private List<Game> mContentsList;
    private AdapterInterface adapterInterface;

    public GameListAdapter(Context context, AdapterInterface adapterInterface) {
        this.context = context;
        this.adapterInterface = adapterInterface;
        mContentsList = new ArrayList<>();
        mContentsList.addAll(GameData.INSTANCE.getGames());
        Collections.sort(mContentsList, (l, r) ->
                l.getTitle().charAt(0) < r.getTitle().charAt(0) ? -1 :
                l.getTitle().charAt(0) > r.getTitle().charAt(0) ? 1 : 0);
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
                .inflate(R.layout.list_item_basic, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Game game = mContentsList.get(position);
        holder.titleText.setText(game.getTitle());
        holder.imageView.setImageResource(game.getIconId());
        holder.container.setOnClickListener(v ->
            adapterInterface.onItemClicked(game));
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
        void onItemClicked(Game game);
    }
}