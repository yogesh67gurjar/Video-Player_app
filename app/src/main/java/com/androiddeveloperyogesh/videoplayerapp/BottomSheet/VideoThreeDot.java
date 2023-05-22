package com.androiddeveloperyogesh.videoplayerapp.BottomSheet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoFilesList;
import com.androiddeveloperyogesh.videoplayerapp.databinding.FragmentVideoThreeDotBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class VideoThreeDot extends BottomSheetDialogFragment {

    FragmentVideoThreeDotBinding binding;

    Context context;
    List<Video> videos;

    String nameOfVideo;
    String folderPath, folderName;

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
        folderPath = getArguments().getString("folderPath");
        folderName = getArguments().getString("folderName");
        Glide.with(context).load(new File(getArguments().getString("thumbnail"))).placeholder(R.drawable.img_thumbnail).into(binding.thumbNail);

        binding.renameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Rename to");
                EditText editText = new EditText(context);
                String path = video.getPath();
                final File file = new File(path);
                String videoName = file.getName();
                videoName = videoName.substring(0, videoName.lastIndexOf("."));
                editText.setText(videoName);
                alertDialog.setView(editText);
                alertDialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String onlyPath = file.getParentFile().getAbsolutePath();
                        String ext = file.getAbsolutePath();
                        ext = ext.substring(ext.lastIndexOf("."));
                        String newPath = onlyPath + "/" + editText.getText().toString() + ext;
                        File newFile = new File(newPath);
                        boolean renameBoolean = file.renameTo(newFile);
                        if (renameBoolean) {
                            ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
                            contentResolver.delete(MediaStore.Files.getContentUri("external"), MediaStore.MediaColumns.DATA + "=?", new String[]{file.getAbsolutePath()});

                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(newFile));
                            context.getApplicationContext().sendBroadcast(intent);

                            Toast.makeText(context, "video renamed", Toast.LENGTH_SHORT).show();

                            SystemClock.sleep(200);
                            ((Activity) context).recreate();
                        } else {
                            Toast.makeText(context, "rename failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
                dismiss();
            }
        });


        binding.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Delete");
            alertDialog.setMessage("Do u really want to delete this video  ?");
            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(video.getId()));
                    File file = new File(video.getPath());
                    boolean deleteBoolean = file.delete();
                    if (deleteBoolean) {
                        dialog.dismiss();
                        context.getContentResolver().delete(contentUri, null, null);
                        Toast.makeText(context, "Video Deleted Successfully", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(200);
                        ((Activity) context).recreate();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(context, "Some error occured while deleting the video", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.create().show();

            dismiss();
        });

        binding.infoBtn.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Properties");
            String one = "File: " + video.getDisplayName();

            String path = video.getPath();
            int indexOfPath = path.lastIndexOf("/");
            String two = "Path: " + path.substring(0, indexOfPath);

            String three = "Size: " + android.text.format.Formatter.formatFileSize(context, Long.parseLong(video.getSize()));
            double milliSeconds = Double.parseDouble(video.getDuration());
            String four = "Length: " + timeConversion((long) milliSeconds);

            String nameWithFormat = video.getDisplayName();
            int index = nameWithFormat.lastIndexOf(".");
            String format = nameWithFormat.substring(index + 1);
            String five = "Format: " + format;

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(video.getPath());
            String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

            String six = "Resolution: " + width + " x " + height;
            alertDialog.setMessage(one + "\n\n" + two + "\n\n" + three + "\n\n" + four + "\n\n" + five + "\n\n" + six);
            alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.create().show();

            dismiss();
        });


        binding.shareBtn.setOnClickListener(v -> {
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
        });
        return binding.getRoot();
    }

    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mins = (duration / 60000) % 60000;
        int secs = (duration % 60000) / 1000;
        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mins, secs);
        } else {
            videoTime = String.format("%02d:%02d", mins, secs);
        }
        return videoTime;
    }
}