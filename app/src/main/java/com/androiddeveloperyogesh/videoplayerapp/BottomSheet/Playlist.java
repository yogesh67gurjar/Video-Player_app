package com.androiddeveloperyogesh.videoplayerapp.BottomSheet;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androiddeveloperyogesh.videoplayerapp.Adapters.PlaylistAdapter;
import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity.VideoPlayer;
import com.androiddeveloperyogesh.videoplayerapp.databinding.FragmentPlaylistBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist extends BottomSheetDialogFragment {
    FragmentPlaylistBinding binding;
    Context context;
    List<Video> videos;
    int i;


    public Playlist(Context context, List<Video> videos,int i) {
        this.context = context;
        this.videos = videos;
        this.i=i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false);


        binding.playlistRecyclerview.setAdapter(new PlaylistAdapter(context, videos, i));
        binding.playlistRecyclerview.setLayoutManager(new LinearLayoutManager(context));

        return binding.getRoot();
    }
}