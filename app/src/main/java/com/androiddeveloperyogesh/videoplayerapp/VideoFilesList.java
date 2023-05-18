package com.androiddeveloperyogesh.videoplayerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.androiddeveloperyogesh.videoplayerapp.Adapters.VideosAdapter;
import com.androiddeveloperyogesh.videoplayerapp.Models.VideoRelatedDetails;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityVideoFilesListBinding;

import java.util.ArrayList;
import java.util.List;

public class VideoFilesList extends AppCompatActivity {
    ActivityVideoFilesListBinding binding;
    VideosAdapter videosAdapter;
    List<VideoRelatedDetails> videoRelatedDetailsList;

    String folderName;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFilesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();
        videoRelatedDetailsList = new ArrayList<>();
        folderName = getIntent().getStringExtra("folderName");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(folderName);
        showVideos(folderName);
    }

    private void showVideos(String folderName) {
        videoRelatedDetailsList = getAllVideos(folderName);
        videosAdapter = new VideosAdapter(this,fragmentManager, videoRelatedDetailsList);
        binding.rvVideos.setAdapter(videosAdapter);
        binding.rvVideos.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<VideoRelatedDetails> getAllVideos(String folderName) {

        List<VideoRelatedDetails> videos = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null, selection, selectionArg, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

                VideoRelatedDetails videoRelatedDetails = new VideoRelatedDetails(id, title, displayName, size, duration, path, dateAdded);

                videos.add(videoRelatedDetails);
            } while (cursor.moveToNext());
        }
        return videos;
    }
}