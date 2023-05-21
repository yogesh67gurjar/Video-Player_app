package com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

    Player.Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        scales = new int[3];


        scales[0] = C.VIDEO_SCALING_MODE_DEFAULT;
        scales[1] = C.VIDEO_SCALING_MODE_SCALE_TO_FIT;
        scales[2] = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        playerView = findViewById(R.id.exoPlayerView);

        video = (Video) getIntent().getExtras().getSerializable("video");
        duration = getIntent().getExtras().getString("duration");
        videos = (List<Video>) getIntent().getExtras().getSerializable("videos");
        name = getIntent().getExtras().getString("name");

        Log.d("ggggggggggg", String.valueOf(videos.size()));

        playVideo();

        //       binding.exoPlayerView
    }

    private void playVideo() {

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
        player.seekTo((int) selectedPosition, C.TIME_UNSET);

        binding.exoPlayerView.setPlayer(player);
        binding.exoPlayerView.setKeepScreenOn(true);

        player.setPlayWhenReady(true);

//        AspectRatioFrameLayout aspectRatioFrameLayout = binding.exoPlayerView.findViewById(R.id.exo_content_frame);


        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.d("dgdfgdfdfgsdfg", error.toString());
                Toast.makeText(VideoPlayer.this, "video playing error", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView playPauseBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerPlayPause);

        TextView title = binding.exoPlayerView.findViewById(R.id.exoPlayerTitle);
        title.setText(video.getTitle());

        ImageView backBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerBack);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView scaling = binding.exoPlayerView.findViewById(R.id.exoPlayerScaling);
        scaling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VideoPlayer.this, "dfdgfdgfd", Toast.LENGTH_SHORT).show();
                // Generate random integers in range 0 to 999
                Random rand = new Random();
                int num = rand.nextInt(3);
                player.setVideoScalingMode(scales[num]);
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

        ImageView next = binding.exoPlayerView.findViewById(R.id.exoPlayerNext);
        ImageView previous = binding.exoPlayerView.findViewById(R.id.exoPlayerPrevious);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextVideo();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousVideo();
            }
        });

        listener = new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progress.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY && playWhenReady) {
                    progress.setVisibility(View.GONE);
                    Log.d("jjjjjjjjj", "me hu me hu");
                    updateTimeBar();
                } else if (playbackState == Player.STATE_ENDED) {
                    playNextVideo();

                }
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                updateTimeBar();
            }
        };
        player.addListener(listener);
    }

    private void playPreviousVideo() {
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex != 0) {
            // Play the next video in the list
            player.seekTo(currentWindowIndex - 1, 0);
            player.setPlayWhenReady(true);
        }
    }

    private void playNextVideo() {
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex < concatenatingMediaSource.getSize() - 1) {
            // Play the next video in the list
            player.seekTo(currentWindowIndex + 1, 0);
            player.setPlayWhenReady(true);
        } else {
            // No more videos to play, finish the activity or handle as needed
            finish();
        }
    }

    private void updateTimeBar() {
//        Log.d("gdgsdfg", String.valueOf(player.getCurrentPosition()));
        long duration = player.getDuration();
        long currentPosition = player.getCurrentPosition();
        long bufferPosition = player.getBufferedPosition();

//        timeBar.setDuration(duration);
//        timeBar.setPosition(currentPosition);
//        timeBar.setBufferedPosition(bufferPosition);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
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