package com.androiddeveloperyogesh.videoplayerapp.VideoPlayerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.androiddeveloperyogesh.videoplayerapp.BottomSheet.Playlist;
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

import java.util.List;
import java.util.Objects;

public class VideoPlayer extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;

    // currently jo bhi video play ho rhi he uski int me position kya he List<Videos>videos me
    int playlistPos;

    Video video;
    List<Video> videos;
    ImageView playPauseBtn;

    SimpleExoPlayer player;
    // player jo ki poora management dekhega us view k liye jisme video play hoga ya dikhega
    DefaultDataSourceFactory defaultDataSourceFactory;
    //    DefaultDataSourceFactory he ek class under ExoPlayer library helps krti he bnane me DataSource.isse handle kr skte retrieval of media from various sources(local files,network streams,etc).
    //    allows to create & configure a DataSource to play media
    ConcatenatingMediaSource concatenatingMediaSource;
    // to ye int ka array he
    //    ConcatenatingMediaSource is a class provided by the ExoPlayer library that represents a media source that concatenates multiple media sources together. It allows you to create a single source that plays multiple media items sequentially, one after another.
    MediaSource mediaSource;
    // agr ye int he
    //    MediaSource is an interface provided by the ExoPlayer library that represents a source of media data. It serves as an abstraction for different types of media sources, such as progressive media files, adaptive streaming formats (e.g., DASH or HLS), or other custom media sources.

    boolean rotationFlag = false;
    int zoomFlag = 1;
    int selectedPosition;

    FragmentManager fragmentManager;

    private GestureDetector gestureDetector;
    private VideoGestureListener gestureListener;
    CardView tenSecRewindCard;
    CardView tenSecForwardCard;

    private AudioManager audioManager;
    private Window window;

    private int maxVolume;
    private float brightness;
    TextView volumeSwipe, brightnessSwipe;
    View controlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 10 second gestures
        gestureListener = new VideoGestureListener(VideoPlayer.this);
        gestureDetector = new GestureDetector(VideoPlayer.this, gestureListener);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        window = getWindow();

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        brightness = window.getAttributes().screenBrightness;

        fragmentManager = getSupportFragmentManager();

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get from intent's bundle
        video = (Video) getIntent().getExtras().getSerializable("video");
        videos = (List<Video>) getIntent().getExtras().getSerializable("videos");

        // play kro video ko or kya
        playVideo();

        binding.exoPlayerView.setOnTouchListener((v, event) -> {
            boolean consumed = gestureDetector.onTouchEvent(event);

            // Manually show the controls when the screen is touched again
            if (event.getAction() == MotionEvent.ACTION_UP && !consumed) {
                binding.exoPlayerView.showController();
            }

            return consumed;
        });

    }

    public void skipVideo(int seconds) {
        long currentPosition = player.getCurrentPosition();
        long newPosition = currentPosition + (seconds * 1000); // Convert seconds to milliseconds

        player.seekTo(newPosition);
    }

    private class VideoGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 200;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;
        private float prevBrightness = 0.5f; // Initial brightness value
        private int prevVolume = 0; // Initial volume value
        private boolean areControlsVisible = true; // Initial state of controls visibility



        private final Context context;
        private boolean isLeftSwipe = false;
        private boolean isRightSwipe = false;

        private ValueAnimator brightnessAnimator;
        private ValueAnimator volumeAnimator;

        public VideoGestureListener(Context context) {
            this.context = context;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            toggleControlsVisibility();
            return true;
        }

        private void toggleControlsVisibility() {
            if (areControlsVisible) {
                hideControls();
            } else {
                showControls();
            }
        }

        private void hideControls() {
            // Hide your custom controls
            // For example:
            controlView.setVisibility(View.GONE);

            areControlsVisible = false;
        }

        private void showControls() {
            // Show your custom controls
            // For example:
            controlView.setVisibility(View.VISIBLE);

            areControlsVisible = true;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            isLeftSwipe = false;
            isRightSwipe = false;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float deltaY = e1.getY() - e2.getY();
            float deltaX = e1.getX() - e2.getX();

            if (!isLeftSwipe && !isRightSwipe) {
                // Determine if it's a left or right swipe
                if (e1.getX() < getWindowManager().getDefaultDisplay().getWidth() / 2) {
                    isLeftSwipe = true;
                } else {
                    isRightSwipe = true;
                }
            }

            // Check if the swipe is vertical and handle brightness and volume accordingly
            if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(distanceY) > SWIPE_VELOCITY_THRESHOLD) {
                if (isLeftSwipe) {
                    // Vertical swipe on the left side: Adjust brightness
                    float brightnessDelta = deltaY / getWindowManager().getDefaultDisplay().getHeight();
                    brightness += brightnessDelta;
                    brightness = Math.max(0, Math.min(brightness, 1));

                    // Cancel any ongoing brightness animation
                    if (brightnessAnimator != null && brightnessAnimator.isRunning()) {
                        brightnessAnimator.cancel();
                    }

                    // Smoothly adjust brightness using animation
                    brightnessAnimator = ValueAnimator.ofFloat(prevBrightness, brightness);
                    brightnessAnimator.setDuration(500); // Adjust the duration as needed
                    brightnessAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float interpolatedValue = (float) animation.getAnimatedValue();
                            WindowManager.LayoutParams layoutParams = window.getAttributes();
                            layoutParams.screenBrightness = interpolatedValue;
                            window.setAttributes(layoutParams);
                        }
                    });
                    brightnessAnimator.start();

                    // Update previous brightness value
                    prevBrightness = brightness;

                    brightnessSwipe.setText(getBrightnessInt(brightness));
                    brightnessSwipe.setAlpha(1);
                    brightnessSwipe.animate().alpha(0).setDuration(800);
                } else if (isRightSwipe) {
                    // Vertical swipe on the right side: Adjust volume
                    float volumeDelta = deltaY / getWindowManager().getDefaultDisplay().getHeight();
                    int volumeChange = (int) (volumeDelta * maxVolume);
                    int newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + volumeChange;
                    newVolume = Math.max(0, Math.min(newVolume, maxVolume));

                    // Cancel any ongoing volume animation
                    if (volumeAnimator != null && volumeAnimator.isRunning()) {
                        volumeAnimator.cancel();
                    }

                    // Smoothly adjust volume using animation
                    volumeAnimator = ValueAnimator.ofInt(prevVolume, newVolume);
                    volumeAnimator.setDuration(500); // Adjust the duration as needed
                    volumeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int interpolatedValue = (int) animation.getAnimatedValue();
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, interpolatedValue, 0);
                        }
                    });
                    volumeAnimator.start();

                    // Update previous volume value
                    prevVolume = newVolume;

                    volumeSwipe.setText(String.valueOf(newVolume));
                    volumeSwipe.setAlpha(1);
                    volumeSwipe.animate().alpha(0).setDuration(800);
                }
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Determine the tap position relative to the screen width
            float tapX = e.getX();
            float screenWidth = ((View) binding.exoPlayerView.getParent()).getWidth();
            float tapPercentage = tapX / screenWidth;

            // Calculate the skip duration based on the tap position
            int skipSeconds;
            if (tapPercentage < 0.5) {
                tenSecRewindCard.setAlpha(1);
                tenSecRewindCard.animate().alpha(0).setDuration(800);
                // Skip backward 10 seconds if tapped on the left side
                skipSeconds = -10;
            } else {
                tenSecForwardCard.setAlpha(1);
                tenSecForwardCard.animate().alpha(0).setDuration(800);
                // Skip forward 10 seconds if tapped on the right side
                skipSeconds = 10;
            }

            // Perform the action to skip the specified duration of the video
            skipVideo(skipSeconds);
            return true;
        }
    }

    private String getBrightnessInt(float brightness) {
//        String str = String.format("%.1f", brightness);
//        Log.d("strstrstr", str);
//        str = str.split(".")[0];
        return "str";
    }


    private void playVideo() {
        //  apn ne exoplayer ka jo view bnaya he in xml
        //  usme ek line likhi he                   app:controller_layout_id="@layout/exoplayer_custom_playback_view"
        //  iska mtlb he ki video ko control krne k liye apn ek custom layout bna rhe he
        //  to ye jo niche findViewById kr rhe he apn and jo ids he ye usi layout ki id he jo ki he exoplayer_custom_playback_view
        LottieAnimationView buffering = binding.exoPlayerView.findViewById(R.id.lottieLoading);
        ImageView next = binding.exoPlayerView.findViewById(R.id.exo_next);
        ImageView previous = binding.exoPlayerView.findViewById(R.id.exo_prev);
        playPauseBtn = binding.exoPlayerView.findViewById(R.id.exo_play_pause);
        TextView title = binding.exoPlayerView.findViewById(R.id.exoPlayerTitle);
        ImageView backBtn = binding.exoPlayerView.findViewById(R.id.exoPlayerBack);
        ImageView scaling = binding.exoPlayerView.findViewById(R.id.exoPlayerScaling);
        ImageView rotate = binding.exoPlayerView.findViewById(R.id.exoPlayerRotate);
        ImageView playlist = binding.exoPlayerView.findViewById(R.id.exoPlayerPlaylist);
        ConstraintLayout rootLayout = binding.exoPlayerView.findViewById(R.id.rootLayout);
        ImageView unLock = binding.exoPlayerView.findViewById(R.id.exoPlayerUnLock);
        ImageView lock = binding.exoPlayerView.findViewById(R.id.exoplayer_lock);
        CardView scaleCard = binding.exoPlayerView.findViewById(R.id.scaleCard);
        TextView scaleCardText = binding.exoPlayerView.findViewById(R.id.scaleCardText);
        ImageView scaleCardImage = binding.exoPlayerView.findViewById(R.id.scaleCardImage);
        tenSecRewindCard = binding.exoPlayerView.findViewById(R.id.tenSecRewindCard);
        tenSecForwardCard = binding.exoPlayerView.findViewById(R.id.tenSecForwardCard);
        volumeSwipe = binding.exoPlayerView.findViewById(R.id.volumeSwipe);
        brightnessSwipe = binding.exoPlayerView.findViewById(R.id.brightnessSwipe);
        // yha apn ne exoplayer ko initialize kr diya ki isi same activity me apn he and isko use krna chahte he
        player = new SimpleExoPlayer.Builder(VideoPlayer.this).build();
        defaultDataSourceFactory = new DefaultDataSourceFactory(VideoPlayer.this, Util.getUserAgent(VideoPlayer.this, "videoPlayerApp"));
        concatenatingMediaSource = new ConcatenatingMediaSource();


// In your onCreate or onCreateView method, initialize the controlView
        controlView = binding.exoPlayerView.findViewById(R.id.root); // Replace R.id.custom_control_view with the ID of your custom control view

        for (int i = 0; i < videos.size(); i++) {
            //  jo video play krna he uski position lene k liye he ye loop bs or kuch nhi
            if (Objects.equals(videos.get(i).getDisplayName(), video.getDisplayName())) {
                selectedPosition = i;
            }


            String path = videos.get(i).getPath();
            Uri uri = Uri.parse(path);
            mediaSource = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
            //  ProgressiveMediaSource: It is designed to handle progressive media formats, such as MP4 or HLS.

            //  yha apn ne ProgressiveMediaSource use kiya he
            //  iski jgh apn or bhi mediaSources use kr skte he like

            //  DashMediaSource: This is used for streaming Dynamic Adaptive Streaming over HTTP (DASH) content. It handles media formats that are segmented and encoded at different quality levels, allowing for adaptive streaming based on the available network conditions.
            //  HlsMediaSource: This is used for streaming HTTP Live Streaming (HLS) content. HLS is a streaming protocol that divides media content into small segments and provides a playlist file (.m3u8) that specifies the available segments and their URLs.
            //  ExtractorMediaSource: This is used for playing local or remote media files that can be extracted using a media extractor. It supports a wide range of media formats and relies on the underlying media extractor to extract the audio and video data from the media files.
            //  ConcatenatingMediaSource: This is used to concatenate multiple media sources into a single source. It allows you to play a sequence of media items without interruption, such as playing multiple videos or audio tracks back-to-back.
            //  ClippingMediaSource: This allows you to clip a portion of a media source, specifying the start and end positions. It is useful when you want to play only a specific section of a media file.
            //  AdsMediaSource: This is used for handling ad insertion within the media playback. It allows you to seamlessly insert ads into the content being played.


            concatenatingMediaSource.addMediaSource(mediaSource);
        }


        player.addMediaSource(concatenatingMediaSource);
        player.prepare();

        player.seekTo(selectedPosition, C.TIME_UNSET);
        binding.exoPlayerView.setPlayer(player);
        binding.exoPlayerView.setKeepScreenOn(true);
        player.setPlayWhenReady(true);


        // ab saare click listeners
        title.setText(video.getDisplayName());
        backBtn.setOnClickListener(v -> finish());


        // for playlist recyclerview
        playlistPos = selectedPosition;
        //  to control visibility of playlist dropdown
        playlist.setOnClickListener(v -> {
            Log.d("this is the matter", String.valueOf(videos.get(player.getCurrentWindowIndex()).getDisplayName()));
            Playlist bottomSheet = new Playlist(VideoPlayer.this, videos, playlistPos, videos.get(player.getCurrentWindowIndex()).getDisplayName());
            bottomSheet.show(fragmentManager, bottomSheet.getTag());
        });


        scaling.setOnClickListener(v -> {
            if (zoomFlag == 1) {
                zoomFlag = 2;
                binding.exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                scaling.setImageResource(R.drawable.ic_fill);
                scaleCardText.setText("fill");
                scaleCardImage.setImageResource(R.drawable.ic_fill);
                scaleCard.setAlpha(1);
                scaleCard.animate().alpha(0).setDuration(500);
            } else if (zoomFlag == 2) {
                zoomFlag = 3;
                binding.exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                scaling.setImageResource(R.drawable.ic_fit);
                scaleCardText.setText("fit");
                scaleCardImage.setImageResource(R.drawable.ic_fit);
                scaleCard.setAlpha(1);
                scaleCard.animate().alpha(0).setDuration(500);
            } else {
                zoomFlag = 1;
                binding.exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                scaling.setImageResource(R.drawable.ic_zoom);
                scaleCardText.setText("zoom");
                scaleCardImage.setImageResource(R.drawable.ic_zoom);
                scaleCard.setAlpha(1);
                scaleCard.animate().alpha(0).setDuration(500);
            }

//            Toast.makeText(VideoPlayer.this, "dfdgfdgfd", Toast.LENGTH_SHORT).show();
//            // Generate random integers in range 0 to 999
//            Random rand = new Random();
//            int num = rand.nextInt(3);
//            player.setVideoScalingMode(scales[num]);
        });


        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //  ye built-in function he apn ne nhi bnaya he isko
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
            //  agr player already chl rha ho to pause ho jae
            if (player.isPlaying()) {
                playPauseBtn.setImageResource(R.drawable.ic_play);
                player.pause();
            } else {
                //  agr player ruka hua ho to play ho jae
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
                    buffering.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY && playWhenReady) {
                    buffering.setVisibility(View.GONE);
                } else if (playbackState == Player.STATE_ENDED) {
                    playNextVideo();
                }
            }
        });
    }


    // pip
//    @Override
//    protected void onUserLeaveHint() {
//        //  ye call hoga jb user home button dabata he ya phir doore app me chle jata he
//        super.onUserLeaveHint();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            enterPictureInPictureMode();
//
////            PictureInPictureParams.Builder paramsBuilder = new PictureInPictureParams.Builder();
////            Rational aspectRatio = new Rational(2, 1);
////            paramsBuilder.setAspectRatio(aspectRatio);
////            enterPictureInPictureMode(paramsBuilder.build());
//        }
//    }

    // pip
//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
//            if (!isInPictureInPictureMode) {
//                // The activity exited PiP mode
//                if (player != null && player.isPlaying()) {
//
//                }
//            }
//        }
//    }

//    // pip
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Handle the configuration changes when the activity is in PiP mode.
//    }

    private void playPreviousVideo() {
        //  getCurrentWindowIndex se apn ko position pta chl jaegi ki kaha pr he apn abhi concatenatingMediaSource me
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex != 0) {
            // Play the previous video if possible
            player.seekTo(currentWindowIndex - 1, 0);
            playlistPos = currentWindowIndex - 1;
            player.setPlayWhenReady(true);

            //  playlistRecyclerview set kr do phir se qki position change hui he
        }
    }

    private void playNextVideo() {
        //  getCurrentWindowIndex se apn ko position pta chl jaegi ki kaha pr he apn abhi concatenatingMediaSource me
        int currentWindowIndex = player.getCurrentWindowIndex();
        if (currentWindowIndex < concatenatingMediaSource.getSize() - 1) {
            // next video pe chle jao
            player.seekTo(currentWindowIndex + 1, 0);
            playlistPos = currentWindowIndex + 1;
            player.setPlayWhenReady(true);

            //  playlistRecyclerview set kr do phir se qki position change hui he
        } else {
            // No more videos to play, finish the activity or handle as needed
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player.isPlaying()) {
            player.stop();
            player.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.getPlaybackState();
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Pass the touch event to the GestureDetector to handle gestures
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    //    @Override
//    protected void onStop() {
//        super.onStop();
//        if (player.isPlaying()) {
//            player.stop();
//            player.release();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        player.setPlayWhenReady(true);
//        player.getPlaybackState();
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // agar video play ho rhi he to sb khatam kr do
//        if (player.isPlaying()) {
//            player.stop();
//            player.release();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        // jb user back button dabae to stop kr do video playback ko
//        if (player.isPlaying()) {
//            player.stop();
//        }
//    }


}


//  app ko pip mode me daalne ka function kaha daale


//  The decision to use enterPictureInPictureMode() in the onPause() method or the onUserLeaveHint() method depends on your specific use case and the behavior you want to achieve.
//  If you want to enter picture-in-picture mode whenever the activity is paused, regardless of the reason (such as when the user presses the home button or switches to another app), then using enterPictureInPictureMode() in the onPause() method is appropriate.
//  However, if you specifically want to enter picture-in-picture mode only when the user presses the home button or navigates away from the app, then using enterPictureInPictureMode() in the onUserLeaveHint() method would be more suitable. This method is specifically called when the user leaves the activity to go to the home screen or switch to another app.
//  Consider your desired behavior and choose the appropriate method accordingly.


//  are the onConfigurationChanged and onPictureInPictureModeChanged functions same ??

//    The onConfigurationChanged() and onPictureInPictureModeChanged() methods serve different purposes and are not repetitive.
//
//        The onConfigurationChanged() method is called when a configuration change occurs in your activity, such as a screen orientation change or a language change. You can override this method to handle specific behaviors or UI adjustments when these changes occur. In the case of picture-in-picture mode, it is important to handle configuration changes to ensure that your activity adapts correctly when transitioning to or from picture-in-picture mode.
//
//        The onPictureInPictureModeChanged() method, on the other hand, is specifically called when the activity's picture-in-picture mode state changes. It is called whenever the activity enters or exits picture-in-picture mode. You can use this method to adjust your UI or perform specific actions based on whether the activity is currently in picture-in-picture mode or not.
//
//        Both methods serve different purposes and are necessary for proper handling of configuration changes and picture-in-picture mode transitions in your activity.