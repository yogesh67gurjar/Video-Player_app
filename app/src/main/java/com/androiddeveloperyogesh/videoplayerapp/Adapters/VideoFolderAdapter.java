package com.androiddeveloperyogesh.videoplayerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androiddeveloperyogesh.videoplayerapp.Models.VideoFiles;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoFilesList;

import java.util.List;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.VideoFolderViewHolder> {
    Context context;
    Intent intent;
    List<VideoFiles> videoFilesList;
    List<String> folderPathList;

    public VideoFolderAdapter(Context context, List<VideoFiles> videoFilesList, List<String> folderPathList) {
        this.context = context;
        this.videoFilesList = videoFilesList;
        this.folderPathList = folderPathList;
    }

    @NonNull
    @Override
    public VideoFolderAdapter.VideoFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_video_folders_layout, parent, false);
        return new VideoFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFolderAdapter.VideoFolderViewHolder holder, int position) {

        int indexPath = folderPathList.get(position).lastIndexOf("/");
        String nameOfFolder = folderPathList.get(position).substring(indexPath + 1);
        holder.folderNameTv.setText(nameOfFolder);
        holder.folderPathTv.setText(folderPathList.get(position));
        holder.noOfFilesTv.setText("5 videos");

        holder.rv_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, VideoFilesList.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderPathList.size();
    }

    public class VideoFolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderNameTv, folderPathTv, noOfFilesTv;
        CardView rv_card;

        public VideoFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_card = itemView.findViewById(R.id.rv_card);
            folderNameTv = itemView.findViewById(R.id.rv_folderNameTv);
            folderPathTv = itemView.findViewById(R.id.rv_folderPathTv);
            noOfFilesTv = itemView.findViewById(R.id.rv_noOfFilesTv);
        }
    }
}
