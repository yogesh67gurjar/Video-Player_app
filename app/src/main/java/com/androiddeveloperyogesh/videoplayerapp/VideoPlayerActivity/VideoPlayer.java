package com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androiddeveloperyogesh.videoplayerapp.Models.VideoRelatedDetails;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityVideoPlayerBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoPlayer extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;
    PlayerView playerView;
    SimpleExoPlayer player;

    int position;
    String videoTitle;
    List<VideoRelatedDetails> videoRelatedDetailsList;

    ConcatenatingMediaSource concatenatingMediaSource;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoRelatedDetailsList = new ArrayList<>();

        playerView = findViewById(R.id.exoPlayerView);

        position = getIntent().getIntExtra("position", 1);
        videoTitle = getIntent().getStringExtra("videoTitle");
        videoRelatedDetailsList = (List<VideoRelatedDetails>) getIntent().getSerializableExtra("videosList");


        View view = getLayoutInflater().inflate(R.layout.exoplayer_custom_playback_view, null);
        title = view.findViewById(R.id.exoPlayerTitle);
        title.setText(videoTitle);

        playVideo();

        //       binding.exoPlayerView
    }

    private void playVideo() {


        player = new SimpleExoPlayer.Builder(this).build();

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "videoPlayerApp"));

        concatenatingMediaSource = new ConcatenatingMediaSource();

        for (int i = 0; i < videoRelatedDetailsList.size(); i++) {
            new File(String.valueOf(videoRelatedDetailsList.get(i)));
            String path = videoRelatedDetailsList.get(position).getPath();
            Uri uri = Uri.parse(path);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }


        playerView.setPlayer(player);

        binding.exoPlayerView.setKeepScreenOn(true);

        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);

        playError();
    }

    private void playError() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Toast.makeText(VideoPlayer.this, "video playing error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }
}