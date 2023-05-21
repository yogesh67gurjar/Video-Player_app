package com.androiddeveloperyogesh.videoplayerapp.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.FragmentVideoThreeDotBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoThreeDot extends BottomSheetDialogFragment {

    FragmentVideoThreeDotBinding binding;

    Context context;
    List<Video> videos;

    String nameOfVideo;

    public VideoThreeDot(Context context) {
        this.context = context;
    }

    Video video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoThreeDotBinding.inflate(inflater, container, false);
        videos = new ArrayList<>();

        nameOfVideo = getArguments().getString("name");
        binding.videoTitleBottomSheet.setText(nameOfVideo);

        video = (Video) getArguments().getSerializable("video");
        videos = (List<Video>) getArguments().getSerializable("videos");

        Glide.with(context).load(new File(getArguments().getString("thumbnail"))).placeholder(R.drawable.img_thumbnail).into(binding.thumbNail);
        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog deleteDialog = new Dialog(context);
                deleteDialog.setContentView(R.layout.dialog_delete);
                AppCompatButton yes = deleteDialog.findViewById(R.id.deleteYesBtn);
                AppCompatButton no = deleteDialog.findViewById(R.id.deleteNoBtn);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(context, "yes delete", Toast.LENGTH_SHORT).show();
                        deleteDialog.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "no delete", Toast.LENGTH_SHORT).show();
                        deleteDialog.dismiss();
                    }
                });

                deleteDialog.show();
                dismiss();
            }
        });

        binding.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "info", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        binding.renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog renameDialog = new Dialog(context);
                renameDialog.setContentView(R.layout.dialog_rename);
                EditText text = renameDialog.findViewById(R.id.renameEdittext);
                AppCompatButton saveBtn = renameDialog.findViewById(R.id.renameSaveBtn);
                text.setText(nameOfVideo);
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (text.getText().toString().isEmpty()) {
                            text.setError("name should not be empty");
                            text.requestFocus();
                        } else {
                            Toast.makeText(context, "rename", Toast.LENGTH_SHORT).show();
                            renameDialog.dismiss();
                        }
                    }
                });
                renameDialog.show();
                dismiss();
            }
        });
        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoPath = video.getPath();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");

                // Set the video file path using a File object
                File videoFile = new File(videoPath);
                Uri videoUri = FileProvider.getUriForFile(context, "com.example.app.fileprovider", videoFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
//              shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video!");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Video"));
                dismiss();
            }
        });
        return binding.getRoot();
    }
}