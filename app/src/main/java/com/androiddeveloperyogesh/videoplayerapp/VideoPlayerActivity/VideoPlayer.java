package com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androiddeveloperyogesh.videoplayerapp.Models.VideoRelatedDetails;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivityVideoPlayerBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
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
    //    PlayerView playerView;
    SimpleExoPlayer player;

    int position;
    VideoRelatedDetails video;
    DefaultDataSourceFactory defaultDataSourceFactory;
    ConcatenatingMediaSource concatenatingMediaSource;
    MediaSource mediaSource;

    boolean rotationFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        playerView = findViewById(R.id.exoPlayerView);

        video = (VideoRelatedDetails) getIntent().getExtras().getSerializable("video");

        playVideo();

        //       binding.exoPlayerView
    }

    private void playVideo() {

        // yha apn ne exoplayer ko initialize kr diya ki isi same activity me apn he and isko use krna chahte he
        player = new SimpleExoPlayer.Builder(VideoPlayer.this).build();


        defaultDataSourceFactory = new DefaultDataSourceFactory(VideoPlayer.this, Util.getUserAgent(VideoPlayer.this, "videoPlayerApp"));

        concatenatingMediaSource = new ConcatenatingMediaSource();

        String path = video.getPath();
        Uri uri = Uri.parse(path);
        mediaSource = new ProgressiveMediaSource
                .Factory(defaultDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));

        concatenatingMediaSource.addMediaSource(mediaSource);

        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);

        binding.exoPlayerView.setPlayer(player);
        binding.exoPlayerView.setKeepScreenOn(true);

        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.d("dgdfgdfdfgsdfg", error.toString());
                Toast.makeText(VideoPlayer.this, "video playing error", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView playPauseBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerPlayPause);

//        binding.exoPlayerView.setUseController(false); // Disable the default controller
//        binding.exoPlayerView.setControllerShowTimeoutMs(0); // Disable the default controller timeout
//        binding.exoPlayerView.setOverlayFrameLayout(controllerView);

        TextView title = binding.exoPlayerView.findViewById(R.id.exoPlayerTitle);
        title.setText(video.getTitle());

        ImageView backBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerBack);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView rotate = binding.exoPlayerView.findViewById(R.id.exoPlayerRotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rotationFlag) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    rotationFlag = false;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    rotationFlag = true;
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    playPauseBtn.setImageResource(R.drawable.ic_play);
                    player.pause();
//                    Toast.makeText(VideoPlayer.this, "pause kra he", Toast.LENGTH_SHORT).show();
                } else {
                    player.play();
                    playPauseBtn.setImageResource(R.drawable.ic_pause);
//                    Toast.makeText(VideoPlayer.this, "play kra he", Toast.LENGTH_SHORT).show();
                }

            }
        });


        LottieAnimationView progress = binding.exoPlayerView.findViewById(R.id.lottieLoading);


        player.addListener(new Player.Listener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progress.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    progress.setVisibility(View.GONE);
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release();
            player = null;
        }
    }
}