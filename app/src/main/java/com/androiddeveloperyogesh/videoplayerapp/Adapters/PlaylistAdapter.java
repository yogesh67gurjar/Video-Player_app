package com.androiddeveloperyogesh.videoplayerapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity.VideoPlayer;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    Context context;
    List<Video> videos;
    int i;

    public PlaylistAdapter(Context context, List<Video> videos, int i) {
        this.context = context;
        this.videos = videos;
        this.i = i;
    }

    @NonNull
    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.PlaylistViewHolder holder, int position) {
        Video singleUnit = videos.get(position);

//        Toast.makeText(context, pos + "  " + position, Toast.LENGTH_SHORT).show();
//        if (videos.get(i).getDisplayName().equals(singleUnit.getDisplayName())) {
//            holder.rv_playlist_title.setTextColor(Color.parseColor("#006400"));
//            holder.rv_playlist_title.setTypeface(Typeface.DEFAULT_BOLD);
//            Log.d("positionpositiojs", singleUnit.getDisplayName() + " ,, $ ,, " + videos.get(i).getDisplayName());
//        }

        holder.rv_playlist_title.setText(singleUnit.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView rv_playlist_title;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_playlist_title = itemView.findViewById(R.id.rv_playlist_title);
        }
    }
}
