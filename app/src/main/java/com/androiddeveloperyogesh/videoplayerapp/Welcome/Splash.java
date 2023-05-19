package com.androiddeveloperyogesh.videoplayerapp.Welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivitySplashBinding;

public class Splash extends AppCompatActivity {
    ActivitySplashBinding binding;

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler();
        runnable = () -> {
            startActivity(new Intent(Splash.this, AllowPermissions.class));
            finish();
        };
        handler.postDelayed(runnable, 4700);

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

    }
}