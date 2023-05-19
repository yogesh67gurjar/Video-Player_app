package com.androiddeveloperyogesh.videoplayerapp.Welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.androiddeveloperyogesh.videoplayerapp.MainActivity;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivitySplashBinding;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

public class Splash extends AppCompatActivity {
    ActivitySplashBinding binding;
    Intent intent;
    long l = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        binding.bg.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_bamboo));
        binding.bg.start();

        binding.bg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });


        binding.bg.animate().translationY(-5000).setDuration(1000).setStartDelay(3500);
        binding.parrot.animate().translationY(5000).setDuration(1000).setStartDelay(3500);
        binding.name.animate().translationY(5000).setDuration(1000).setStartDelay(3500);
        Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.splashtextanimation2);
        binding.name.startAnimation(animation);
        binding.lottie.animate().translationY(5000).setDuration(1000).setStartDelay(3500);
        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                intent = new Intent(Splash.this, AllowPermissions.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 4700);
    }


}