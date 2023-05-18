package com.androiddeveloperyogesh.videoplayerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androiddeveloperyogesh.videoplayerapp.BottomSheet.VideoThreeDot;
import com.androiddeveloperyogesh.videoplayerapp.Models.VideoRelatedDetails;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity.VideoPlayer;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    Context context;
    Intent intent;
    List<VideoRelatedDetails> videoRelatedDetailsList;

    public VideosAdapter(Context context, List<VideoRelatedDetails> videoRelatedDetailsList) {
        this.context = context;
        this.videoRelatedDetailsList = videoRelatedDetailsList;

    }

    @NonNull
    @Override
    public VideosAdapter.VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_videos_layout, parent, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapter.VideosViewHolder holder, int position) {
        VideoRelatedDetails singleUnit = videoRelatedDetailsList.get(position);
        holder.title.setText(singleUnit.getDisplayName());
        String strSize = singleUnit.getSize();

        holder.size.setText(android.text.format.Formatter.formatFileSize(context, Long.parseLong(strSize)));
        double milliSeconds = Double.parseDouble(singleUnit.getDuration());
        holder.duration.setText(timeConversion((long) milliSeconds));
        Glide.with(context).load(new File(singleUnit.getPath())).placeholder(R.drawable.img_thumbnail).into(holder.thumbnail);

        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VideoThreeDot bottomSheet = new VideoThreeDot(context);
                FragmentManager fm = bottomSheet.getParentFragmentManager();
                Bundle bundle = new Bundle();
//                bundle.putString("latitude", String.valueOf(location.getLatitude()));
//                bundle.putString("longitude", String.valueOf(location.getLongitude()));
                bottomSheet.setCancelable(false);
                bundle.putString("name", singleUnit.getDisplayName());
                bundle.putString("thumbnail", singleUnit.getPath());
                bottomSheet.setArguments(bundle);
                bottomSheet.show(fm, bottomSheet.getTag());

//                Toast.makeText(context, "three dots clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, VideoPlayer.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable("video", singleUnit);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mins = (duration / 60000) % 60000;
        int secs = (duration % 60000) / 1000;
        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mins, secs);
        } else {
            videoTime = String.format("%02d:%02d", mins, secs);
        }
        return videoTime;
    }

    @Override
    public int getItemCount() {
        return videoRelatedDetailsList.size();
    }

    public static class VideosViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, threeDots;
        TextView duration, title, size;
        CardView card;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.rv_video_card);
            thumbnail = itemView.findViewById(R.id.rvThumbnailImageview);
            threeDots = itemView.findViewById(R.id.threeDots);
            duration = itemView.findViewById(R.id.durationTv);
            title = itemView.findViewById(R.id.rv_videoNameTv);
            size = itemView.findViewById(R.id.rv_videoSizeTv);
        }
    }
}
