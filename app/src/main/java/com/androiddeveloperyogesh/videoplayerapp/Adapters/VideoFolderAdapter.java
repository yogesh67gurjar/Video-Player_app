package com.androiddeveloperyogesh.videoplayerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androiddeveloperyogesh.videoplayerapp.Models.VideoRelatedDetails;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoFilesList;

import java.util.ArrayList;
import java.util.List;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.VideoFolderViewHolder> {
    Context context;
    Intent intent;
    List<VideoRelatedDetails> videoRelatedDetailsList;
    List<String> foldersJismeVideosHeList;

    FragmentManager fragmentManager;

    public VideoFolderAdapter(Context context, FragmentManager fragmentManager, List<VideoRelatedDetails> videoRelatedDetailsList, List<String> foldersJismeVideosHeList) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.videoRelatedDetailsList = videoRelatedDetailsList;
        this.foldersJismeVideosHeList = foldersJismeVideosHeList;
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


        //  jese apne paas current folder ka path he
        //  Android/data/videos/whatsapp/whatsappvideos/private
        //  ab iski size he 52 to apn ne last index liya to size bni 45
        //  to isme se apn last index utha rhe he and wha se ek substring bna rhe he like this
        //  private
        int indexPath = foldersJismeVideosHeList.get(position).lastIndexOf("/");
        String nameOfFolder = foldersJismeVideosHeList.get(position).substring(indexPath + 1);

        holder.folderNameTv.setText(nameOfFolder);

        holder.folderPathTv.setText(foldersJismeVideosHeList.get(position));

        holder.noOfFilesTv.setText("5 videos");

        holder.rv_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, VideoFilesList.class);
                intent.putExtra("folderName", nameOfFolder);
                context.startActivity(intent);
            }
        });
    }

    public void filterList(List<String> filterlist) {
        foldersJismeVideosHeList = filterlist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return foldersJismeVideosHeList.size();
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
