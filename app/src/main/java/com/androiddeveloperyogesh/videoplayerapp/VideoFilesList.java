package com.androiddeveloperyogesh.videoplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityVideoFilesListBinding;

public class VideoFilesList extends AppCompatActivity {
    ActivityVideoFilesListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFilesListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}