package com.androiddeveloperyogesh.videoplayerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.Adapters.FoldersAdapter;
import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    List<Video> videos;
    List<String> folders;
    // isme hr ek string folder ka path hoga
    FoldersAdapter foldersAdapter;
    public static final int STORAGE = 11;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        loadLocale();

        // action bar / toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");

        // initialization
        videos = new ArrayList<>();
        folders = new ArrayList<>();

        // drawer kholne k liye
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // navigation drawer k items ka listener
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int Id = item.getItemId();
                if (Id == R.id.appLanguage) {
                    Dialog appLanguageDialog = new Dialog(MainActivity.this);
                    appLanguageDialog.setContentView(R.layout.dialog_language);

                    AppCompatButton hindi = appLanguageDialog.findViewById(R.id.hindiLanguage);
                    AppCompatButton english = appLanguageDialog.findViewById(R.id.englishLanguage);

                    hindi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "भाषा सफलतापूर्वक बदल गई", Toast.LENGTH_SHORT).show();
                            changeLanguage("hindi");
                            appLanguageDialog.dismiss();
                        }
                    });
                    english.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "Language Changed Successfully", Toast.LENGTH_SHORT).show();
                            changeLanguage("english");
                            appLanguageDialog.dismiss();
                        }
                    });
                    appLanguageDialog.show();
                } else if (Id == R.id.appTheme) {
                    Toast.makeText(MainActivity.this, "app theme", Toast.LENGTH_SHORT).show();
                } else if (Id == R.id.rateUs) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(rateIntent);
                } else if (Id == R.id.shareApp) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, "share app via..."));
                }
                binding.drawerLayout.close();
                return false;
            }
        });

        // for searching any folder by name
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
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

    private void showFolders() {
        fragmentManager = getSupportFragmentManager();
        videos = getFoldersFunc();
        if (folders.size() > 0) {
            //  recyclerview me apn folders ki list dikhanege
            //  extra me apn ne videos related details bhi le k pass kr di he qki apn usko wha click pr use krenge usko
            //  and phir videos k recyclerview ko show krenge is list k data se
            foldersAdapter = new FoldersAdapter(this, fragmentManager, videos, folders);
            binding.foldersRv.setAdapter(foldersAdapter);
            binding.foldersRv.setLayoutManager(new LinearLayoutManager(this));
            binding.foldersRv.setVisibility(View.VISIBLE);
            binding.noData.setVisibility(View.GONE);
        } else {
            binding.foldersRv.setVisibility(View.GONE);
            binding.noData.setVisibility(View.VISIBLE);
        }
    }

    private List<Video> getFoldersFunc() {
        List<Video> videoList = new ArrayList<>();

        //  uri ek trh se kisi resource (jisme kuch bhi data , file ya kuch bhi ho) k path ki trh he
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        //  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI = isse saari audios ka uri mil jaega
        //  MediaStore.Video.Media.EXTERNAL_CONTENT_URI = isse saari videos ka uri mil jaega
        //  MediaStore.Images.Media.EXTERNAL_CONTENT_URI = isse saari images ka uri mil jaega
        //  MediaStore.Downloads.Media.EXTERNAL_CONTENT_URI = isse saari downloaded files ka uri mil jaega
        //  MediaStore.Files.Media.EXTERNAL_CONTENT_URI = isse saare docs ka uri mil jaega

        // get content resolver apn se uri leta he and isme filtering kr skte he conditions de skte he k kya lena he kya nhi lena
        // jese abhi apn saare hi videos le rhe he kuch filter nhi lga rhe
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        //  ek cursor ki help se hr ek uri ko dekho
        if (cursor != null && cursor.moveToNext()) {
            do {
                // id ka column index nikaal lo and phir us index pe jo rkha he usko get kr lo cursor.getstring se
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

                Video video = new Video(id, title, displayName, size, duration, path, dateAdded);

                //  jese apne paas current video ka path he
                //  Android/data/videos/whatsapp/whatsappvideos/private/myvideo.mp4
                //  to isme se apn last index utha rhe he and wha tk ki ek substring bna rhe he like this
                //  Android/data/videos/whatsapp/whatsappvideos/private

                int isVideoKaIndex = path.lastIndexOf("/");
                String subString = path.substring(0, isVideoKaIndex);
                if (!folders.contains(subString)) {
                    folders.add(subString);
                }
                videoList.add(video);
            } while (cursor.moveToNext());
        }
        return videoList;
    }


    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("xm", MODE_PRIVATE);
        if (sharedPreferences.contains("language")) {
            String lang = sharedPreferences.getString("language", "");
            if (lang.equalsIgnoreCase("hi")) {
                changeLanguage("hindi");
            } else if (lang.equalsIgnoreCase("")) {
                changeLanguage("english");
            }
        }
    }

    private void filter(String text) {
        List<String> filteredlist = new ArrayList<String>();

        for (String item : folders) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            foldersAdapter.filterList(filteredlist);
        }
    }

    // right side me 3 dots wala menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return true;
    }

    // ye usi menu ka listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id;
        id = item.getItemId();

        if (id == R.id.sortBy) {
            Toast.makeText(this, "sort by", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFolders();
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


    private void changeLanguage(String language) {
        String str = "";
        if (language.equalsIgnoreCase("hindi")) {
            str = "hi";
        } else if (language.equalsIgnoreCase("english")) {
            str = "";
        }

        // sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("xm", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", str);
        editor.commit();

        Locale locale = new Locale(str);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());


        recreate();
    }

}