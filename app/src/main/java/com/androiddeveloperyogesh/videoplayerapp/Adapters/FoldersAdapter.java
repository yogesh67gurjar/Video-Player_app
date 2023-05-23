package com.androiddeveloperyogesh.videoplayerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoFilesList;

import java.util.ArrayList;
import java.util.List;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.VideoFolderViewHolder> {
    Context context;
    Intent intent;
    List<Video> videos;
    List<String> folders;
    // isme rh ek string folder ka path hoga

    FragmentManager fragmentManager;

    public FoldersAdapter(Context context, FragmentManager fragmentManager, List<Video> videos, List<String> folders) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.videos = videos;
        this.folders = folders;

        for (Video video : videos) {
            Log.d("videosMili   ", video.getId() + "  " + video.getTitle() + "  " + video.getDisplayName() + "  " + video.getSize() + "  " + video.getDuration() + "  " + video.getPath() + "  " + video.getDateAdded());
            //  is type se logs dikhenge

            //  1000002296 (id)
            //  VID_20220928_183058 (title)
            //  VID_20220928_183058.mp4 (displayName)
            //  40070587 (size)
            //  19050 (duration)
            //  /storage/emulated/0/DCIM/OpenCamera/VID_20220928_183058.mp4 (path)
            //  1664370058 (dateAdded)
        }
    }

    @NonNull
    @Override
    public FoldersAdapter.VideoFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_video_folders_layout, parent, false);
        return new VideoFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersAdapter.VideoFolderViewHolder holder, int position) {


        String folderPath = folders.get(position);
        holder.folderPathTv.setText(folderPath);
        //  jese apne paas current folder ka path he
        //  Android/data/videos/whatsapp/whatsappvideos/private
        //  ab iski size he 52 to apn ne last index liya to size bni 45
        //  to isme se apn last index utha rhe he and wha se ek substring bna rhe he like this
        //  private
        int indexPathIndex = folderPath.lastIndexOf("/");
        String folderName = folderPath.substring(indexPathIndex + 1);
        holder.folderNameTv.setText(folderName);

        holder.noOfFilesTv.setText(getVideosCount(folderPath) + " Videos");

        holder.rv_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, VideoFilesList.class);
                intent.putExtra("folderName", folderName);
                intent.putExtra("folderPath", folderPath);
                context.startActivity(intent);
            }
        });
    }


    private int getVideosCount(String folderPath) {
        Log.d("FOLDERPATHIS", folderPath);
        List<Video> videos = new ArrayList<>();

        // jese koi folder ka path apne paas he like
        // Android/emulated/0/data/videos/whatsapp/whatsappvideos/private/myvideo.mp4
        // to iski video to uth jaegi
        // lekin agr apn main directory me hi dekhe like
        // Android/emulated/0/ me to isme bahar rkhi videos uthaane k chakkar me apn wo bhi videos count kr lenge
        // jo iski subdirectory me rkhi he
        // isiliye apne ko ye selection lgana pdega k is folder k baad k / check mt kro

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " LIKE ? AND " + MediaStore.Video.Media.DATA + " NOT LIKE ?";
        String[] selectionArgs = new String[]{"%" + folderPath + "/%", "%" + folderPath + "/%/%"};
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

                Video video = new Video(id, title, displayName, size, duration, path, dateAdded);
                videos.add(video);
            }

            cursor.close();
        }

        return videos.size();
    }


    public void filterList(List<String> filterlist) {
        folders = filterlist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folders.size();
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