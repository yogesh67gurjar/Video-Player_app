package com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androiddeveloperyogesh.videoplayerapp.Adapters.PlaylistAdapter;
import com.androiddeveloperyogesh.videoplayerapp.Models.Video;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class VideoPlayer extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;
    //    PlayerView playerView;
    SimpleExoPlayer player;

    int[] scales;
    int playlistPos;

    String duration;
    String name;
    long position = 0;
    Video video;
    List<Video> videos;
    DefaultDataSourceFactory defaultDataSourceFactory;
    ConcatenatingMediaSource concatenatingMediaSource;

    int selectedPosition;
    MediaSource mediaSource;

    boolean rotationFlag = false;
    boolean playlistFlag = false;
    PlaylistAdapter playlistAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        scales = new int[3];

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        playerView = findViewById(R.id.exoPlayerView);

        video = (Video) getIntent().getExtras().getSerializable("video");
        duration = getIntent().getExtras().getString("duration");
        videos = (List<Video>) getIntent().getExtras().getSerializable("videos");
        name = getIntent().getExtras().getString("name");

        Log.d("ggggggggggg", String.valueOf(videos.size()));


        playVideo();

    }

    private void setplaylist(int pos) {
        playlistAdapter = new PlaylistAdapter(VideoPlayer.this, videos, pos);
        RecyclerView playlistRecyclerView = binding.exoPlayerView.findViewById(R.id.playlistRecyclerview);
        playlistRecyclerView.setAdapter(playlistAdapter);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(VideoPlayer.this));
    }

    private void playVideo() {
        LottieAnimationView progress = binding.exoPlayerView.findViewById(R.id.lottieLoading);
        ImageView next = binding.exoPlayerView.findViewById(R.id.exoPlayerNext);
        ImageView previous = binding.exoPlayerView.findViewById(R.id.exoPlayerPrevious);
        ImageView playPauseBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerPlayPause);
        TextView title = binding.exoPlayerView.findViewById(R.id.exoPlayerTitle);
        ImageView backBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerBack);
        ImageView scaling = binding.exoPlayerView.findViewById(R.id.exoPlayerScaling);
        ImageView rotate = binding.exoPlayerView.findViewById(R.id.exoPlayerRotate);
        ImageView playlist = binding.exoPlayerView.findViewById(R.id.exoPlayerPlaylist);
        CardView playlistCard = binding.exoPlayerView.findViewById(R.id.playlistCard);
        ConstraintLayout rootLayout = binding.exoPlayerView.findViewById(R.id.rootLayout);

        // yha apn ne exoplayer ko initialize kr diya ki isi same activity me apn he and isko use krna chahte he
        player = new SimpleExoPlayer.Builder(VideoPlayer.this).build();
        defaultDataSourceFactory = new DefaultDataSourceFactory(VideoPlayer.this, Util.getUserAgent(VideoPlayer.this, "videoPlayerApp"));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        for (int i = 0; i < videos.size(); i++) {
            if (Objects.equals(videos.get(i).getDisplayName(), name)) {
                selectedPosition = i;
            }
            String path = videos.get(i).getPath();
            Uri uri = Uri.parse(path);
            mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        player.addMediaSource(concatenatingMediaSource);
        player.prepare();
        playlistPos = selectedPosition;
        player.seekTo(selectedPosition, C.TIME_UNSET);
        binding.exoPlayerView.setPlayer(player);
        binding.exoPlayerView.setKeepScreenOn(true);
        player.setPlayWhenReady(true);


        setplaylist(playlistPos);

        title.setText(video.getDisplayName());
        backBtn.setOnClickListener(v -> finish());

        playlist.setOnClickListener(v -> {
            if (playlistFlag) {
                playlistCard.setVisibility(View.GONE);
                playlistFlag = false;
            } else {
                playlistFlag = true;
                playlistCard.setVisibility(View.VISIBLE);
            }
        });
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistFlag) {
                    playlistCard.setVisibility(View.GONE);
                    playlistFlag = false;
                }
            }
        });
        scaling.setOnClickListener(v -> {
//            Toast.makeText(VideoPlayer.this, "dfdgfdgfd", Toast.LENGTH_SHORT).show();
//            // Generate random integers in range 0 to 999
//            Random rand = new Random();
//            int num = rand.nextInt(3);
//            player.setVideoScalingMode(scales[num]);
        });
        rotate.setOnClickListener(v -> {
            if (rotationFlag) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                rotationFlag = false;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                rotationFlag = true;
            }
        });
        playPauseBtn.setOnClickListener(v -> {
            if (player.isPlaying()) {
                playPauseBtn.setImageResource(R.drawable.ic_play);
                player.pause();
            } else {
                player.play();
                playPauseBtn.setImageResource(R.drawable.ic_pause);
            }
        });
        next.setOnClickListener(v -> playNextVideo());
        previous.setOnClickListener(v -> playPreviousVideo());
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.d("dgdfgdfdfgsdfg", error.toString());
                Toast.makeText(VideoPlayer.this, "video playing error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progress.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY && playWhenReady) {
                    progress.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_ENDED) {
                    playNextVideo();
                }
            }

        });
    }


    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isInPictureInPictureMode())
                // Handle the case when the user presses the home button
                // while the activity is in PiP mode.
                // You can enter PiP mode explicitly here.
                enterPictureInPictureMode();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
            if (isInPictureInPictureMode) {
                // The activity is in PiP mode.
                // You can adjust your UI here, such as hiding controls.
            } else {
                // The activity is not in PiP mode.
                // You can restore your UI here, such as showing controls.
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle the configuration changes when the activity is in PiP mode.
    }

    private void playPreviousVideo() {
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex != 0) {
            // Play the next video in the list
            player.seekTo(currentWindowIndex - 1, 0);
            playlistPos = currentWindowIndex - 1;
            setplaylist(playlistPos);
            player.setPlayWhenReady(true);
        }
    }

    private void playNextVideo() {
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex < concatenatingMediaSource.getSize() - 1) {
            // Play the next video in the list
            player.seekTo(currentWindowIndex + 1, 0);
            playlistPos = currentWindowIndex + 1;
            setplaylist(playlistPos);
            player.setPlayWhenReady(true);
        } else {
            // No more videos to play, finish the activity or handle as needed
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.isPlaying()) {

            enterPictureInPictureMode();
//            player.setPlayWhenReady(false);
//            player.getPlaybackState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }
}