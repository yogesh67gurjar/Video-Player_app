package com.androiddeveloperyogesh.videoplayerapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity.VideoPlayer;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    Context context;
    List<Video> videos;
    int i;
    String videoTitle;

    public PlaylistAdapter(Context context, List<Video> videos, int i, String videoTitle) {
        this.context = context;
        this.videos = videos;
        this.i = i;
        this.videoTitle = videoTitle;
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

//        Toast.makeText(context,  "  " + position, Toast.LENGTH_SHORT).show();
//        if (singleUnit.getDisplayName().equals(videoTitle)) {
//            holder.rv_playlist_title.setTextColor(Color.parseColor("#006400"));
//            holder.rv_playlist_title.setTypeface(Typeface.DEFAULT_BOLD);
//            Log.d("positionpositiojs", singleUnit.getDisplayName() + " ,, $ ,, " + videos.get(i).getDisplayName());
//        }

        holder.rv_playlist_title.setText(singleUnit.getDisplayName());
        Glide.with(context).load(new File(singleUnit.getPath())).placeholder(R.drawable.img_thumbnail).into(holder.imageViewPlaylist);


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
                Bundle bundle = new Bundle();
                bundle.putSerializable("video", singleUnit);
                bundle.putSerializable("videos", (Serializable) videos);
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView rv_playlist_title;
        ConstraintLayout root;
        ImageView imageViewPlaylist;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_playlist_title = itemView.findViewById(R.id.rv_playlist_title);
            root = itemView.findViewById(R.id.videoPlaylistRoot);
            imageViewPlaylist = itemView.findViewById(R.id.imageViewPlaylist);
        }
    }
}
