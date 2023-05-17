package com.androiddeveloperyogesh.videoplayerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.Adapters.VideoFolderAdapter;
import com.androiddeveloperyogesh.videoplayerapp.Models.VideoRelatedDetails;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Intent intent;
    List<VideoRelatedDetails> videoRelatedDetailsList;
    List<String> foldersJismeVideosHeList;
    VideoFolderAdapter videoFolderAdapter;
    public static final int STORAGE = 11;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
//        getSupportActionBar().setTitle("All Folders");
        getSupportActionBar().setTitle("");

        videoRelatedDetailsList = new ArrayList<>();
        foldersJismeVideosHeList = new ArrayList<>();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void filter(String text) {
        List<String> filteredlist = new ArrayList<String>();

        for (String item : foldersJismeVideosHeList) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            videoFolderAdapter.filterList(filteredlist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!Environment.isExternalStorageManager()) {
                requestRuntimePermissionFunc("manageStorage");
            } else {
                showFolders();
            }
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestRuntimePermissionFunc("storage");
            } else {
                showFolders();
            }
        }
        binding.swipeRefreshFoldersLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showFolders();
                binding.swipeRefreshFoldersLayout.setRefreshing(false);
            }
        });
    }

    private void showFolders() {
        videoRelatedDetailsList = getVideoRelatedDetailsListFunc();
        if (foldersJismeVideosHeList.size() > 0) {
            //  recyclerview me apn folders ki list dikhanege
            //  extra me apn ne videos related details bhi le k pass kr di he qki apn usko wha click pr use krenge usko
            //  and phir videos k recyclerview ko show krenge is list k data se
            videoFolderAdapter = new VideoFolderAdapter(this, videoRelatedDetailsList, foldersJismeVideosHeList);
            binding.foldersRv.setAdapter(videoFolderAdapter);
            binding.foldersRv.setLayoutManager(new LinearLayoutManager(this));
            binding.foldersRv.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        } else {
            binding.foldersRv.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        }
    }

    private List<VideoRelatedDetails> getVideoRelatedDetailsListFunc() {
        List<VideoRelatedDetails> videoRelatedDetailsList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        //  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI = isse saari audios ka uri mil jaega
        //  MediaStore.Video.Media.EXTERNAL_CONTENT_URI = isse saari videos ka uri mil jaega
        //  MediaStore.Images.Media.EXTERNAL_CONTENT_URI = isse saari images ka uri mil jaega
        //  MediaStore.Downloads.Media.EXTERNAL_CONTENT_URI = isse saari downloaded files ka uri mil jaega
        //  MediaStore.Files.Media.EXTERNAL_CONTENT_URI = isse saare docs ka uri mil jaega

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        //  ek cursor ki help se hr ek uri ko dekho
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

                //  jese apne paas current video ka path he
                //  Android/data/videos/whatsapp/whatsappvideos/private/myvideo.mp4
                //  to isme se apn last index utha rhe he and wha tk ki ek substring bna rhe he like this
                //  Android/data/videos/whatsapp/whatsappvideos/private

                int isVideoKaIndex = path.lastIndexOf("/");
                String subString = path.substring(0, isVideoKaIndex);
                if (!foldersJismeVideosHeList.contains(subString)) {
                    foldersJismeVideosHeList.add(subString);
                }
                videoRelatedDetailsList.add(videoRelatedDetails);
            } while (cursor.moveToNext());
        }
        return videoRelatedDetailsList;
    }

    private void requestRuntimePermissionFunc(String permissionName) {
        if (permissionName.equals("manageStorage")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission is granted
                    Log.d("manageStorage", "yes yes yes yes ");
                } else {
                    Log.d("manageStorage", "no no no no ");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }

        } else if (permissionName.equals("storage")) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("storage", "yes yes yes yes ");

            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("this permission is required for this and this")
                        .setTitle("storage required")
                        .setCancelable(false)
                        .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE);
                            }
                        })
                        .setNegativeButton("reject", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("storage", "yes yes yes yes ");

            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("this feature is unavailable , now open settings ")
                        .setTitle("storage to chaiye")
                        .setCancelable(false)
                        .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("reject", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                requestRuntimePermissionFunc("storage");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        id = item.getItemId();

        if (id == R.id.rateUs) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(rateIntent);
        } else if (id == R.id.refreshActivity) {
            finish();
            startActivity(getIntent());

        } else if (id == R.id.shareApp) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "share app via..."));
        }

        return super.onOptionsItemSelected(item);

    }

}