package com.androiddeveloperyogesh.videoplayerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.Adapters.VideosAdapter;
import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityVideoFilesListBinding;

import java.util.ArrayList;
import java.util.List;

public class VideoFilesList extends AppCompatActivity {
    ActivityVideoFilesListBinding binding;
    VideosAdapter videosAdapter;
    List<Video> videos;

    String folderName, folderPath;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFilesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();
        videos = new ArrayList<>();
        folderName = getIntent().getStringExtra("folderName");
        folderPath = getIntent().getStringExtra("folderPath");
        setSupportActionBar(binding.toolbar);
//        getSupportActionBar().setTitle(folderName);
        getSupportActionBar().setTitle("");
        showVideos(folderPath);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        binding.videoSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        binding.swipeRefreshFoldersLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showVideos(folderName);
                binding.swipeRefreshFoldersLayout.setRefreshing(false);
            }
        });
    }



    private void showVideos(String folderPath) {
        videos = getAllVideos(folderPath);
        videosAdapter = new VideosAdapter(this, fragmentManager, videos);
        binding.rvVideos.setAdapter(videosAdapter);
        binding.rvVideos.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Video> getAllVideos(String folderPath) {

        List<Video> videos = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " LIKE ? AND " + MediaStore.Video.Media.DATA + " NOT LIKE ?";
        String[] selectionArgs = new String[]{"%" + folderPath + "/%", "%" + folderPath + "/%/%"};
        Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));


                Video video = new Video(id, title, displayName, size, duration, path, dateAdded);

                videos.add(video);
            } while (cursor.moveToNext());
        }
        return videos;
    }


    private void filter(String text) {
        List<Video> filteredlist = new ArrayList<Video>();

        for (Video item : videos) {
            if (item.getDisplayName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            videosAdapter.filterList(filteredlist);
        }
    }
}