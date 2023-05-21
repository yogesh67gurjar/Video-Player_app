package com.androiddeveloperyogesh.videoplayerapp.Welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.MainActivity;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityAllowPermissionsBinding;

import java.io.IOException;

public class AllowPermissions extends AppCompatActivity {
    ActivityAllowPermissionsBinding binding;
    public static final int STORAGE = 11;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllowPermissionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click pe check kro permission di he ya nhi
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestRuntimePermissionFunc("manageStorage");
                } else {
                    requestRuntimePermissionFunc("storage");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Environment.isExternalStorageManager()) {
                // Permission is granted
                Log.d("manageStorage", "yes yes yes yes ");
                intent = new Intent(AllowPermissions.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            if (ContextCompat.checkSelfPermission(AllowPermissions.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("storage", "yes yes yes yes ");
                intent = new Intent(AllowPermissions.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void requestRuntimePermissionFunc(String permissionName) {
        if (permissionName.equals("manageStorage")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission is granted
                    Log.d("manageStorage", "yes yes yes yes ");
                    intent = new Intent(AllowPermissions.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    // start activity kro and main pe jao
                } else {
                    Log.d("manageStorage", "no no no no ");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }

        } else if (permissionName.equals("storage")) {
            if (ContextCompat.checkSelfPermission(AllowPermissions.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("storage", "yes yes yes yes ");
                intent = new Intent(AllowPermissions.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(AllowPermissions.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllowPermissions.this);
                builder.setMessage("this permission is required for this and this")
                        .setTitle("storage required")
                        .setCancelable(false)
                        .setPositiveButton("accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(AllowPermissions.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE);
                            }
                        })
                        .setNegativeButton("reject", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                ActivityCompat.requestPermissions(AllowPermissions.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("storage", "yes yes yes yes ");
                intent = new Intent(AllowPermissions.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(AllowPermissions.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllowPermissions.this);
                builder.setMessage("this feature is unavailable , now open settings ")
                        .setTitle("storage to chaiye")
                        .setCancelable(false)
                        .setPositiveButton("accept", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .setNegativeButton("reject", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                requestRuntimePermissionFunc("storage");
            }
        }
    }
}