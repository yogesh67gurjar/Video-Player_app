package com.androiddeveloperyogesh.videoplayerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
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

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFilesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();

        getShared();
        // initialize list of videos
        videos = new ArrayList<>();

        // get from intent
        // folder jiska naam apn ko intent se mila he uski saari videos apn ko available ho jaegi in recyclerview
        folderName = getIntent().getStringExtra("folderName");
        folderPath = getIntent().getStringExtra("folderPath");

        // toolbar / action bar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        // by default bhi showVideos
        showVideos(folderPath);

        // swipe down krne pe bhi showVideos
        binding.swipeRefreshFoldersLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showVideos(folderPath);
                binding.swipeRefreshFoldersLayout.setRefreshing(false);
            }
        });

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
    }

    private void getShared() {
        sharedPreferences = getSharedPreferences("xm", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // right side me 3 dots wala menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.videos_option_menu, menu);
        return true;
    }

    // ye upr wale menu ka listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id;
        id = item.getItemId();

        if (id == R.id.sortBy) {
            Toast.makeText(this, "sort by", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoFilesList.this);
            alertDialog.setTitle("SortBy");
            alertDialog.setPositiveButton("Ok", (dialog, which) -> {
                dialog.dismiss();
                editor.apply();
                finish();
                startActivity(getIntent());
            });
            String[] sorts = new String[]{"Name (A-Z)", "Size (big-small)", "Date (new-old)", "Length (long-short)"};
            alertDialog.setSingleChoiceItems(sorts, -1, (dialog, which) -> {
                switch (which) {
                    case 0:
                        editor.putString("sort", "name");
                        break;
                    case 1:
                        editor.putString("sort", "size");
                        break;
                    case 2:
                        editor.putString("sort", "date");
                        break;
                    case 3:
                        editor.putString("sort", "length");
                        break;

                }
            });
            alertDialog.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showVideos(String folderPath) {
        videos = getAllVideos(folderPath);
        videosAdapter = new VideosAdapter(this, fragmentManager, videos, folderPath, folderName);
        binding.rvVideos.setAdapter(videosAdapter);
        binding.rvVideos.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<Video> getAllVideos(String folderPath) {
        List<Video> videos = new ArrayList<>();

        editor.putString("sort", "name");
        editor.putString("sort", "size");
        editor.putString("sort", "date");
        editor.putString("sort", "length");

        String sortValue = sharedPreferences.getString("sort", "abcd");


        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        if (sortValue.equals("name")) {
            sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
        } else if (sortValue.equals("size")) {
            sortOrder = MediaStore.MediaColumns.SIZE + " DESC";

        } else if (sortValue.equals("date")) {
            sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";

        } else if (sortValue.equals("length")) {
            sortOrder = MediaStore.MediaColumns.DURATION + " DESC";

        } else {
            sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
        }

        String selection = MediaStore.Video.Media.DATA + " LIKE ? AND " + MediaStore.Video.Media.DATA + " NOT LIKE ?";
        String[] selectionArgs = new String[]{"%" + folderPath + "/%", "%" + folderPath + "/%/%"};
        Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, sortOrder);
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
        } else {
            videosAdapter.filterList(filteredlist);
        }
    }
}