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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Handler handler = new Handler();
        Runnable runnable = () -> {
            // isse 4700 milliseconds me AllowPermissions activity start ho jaegi
            startActivity(new Intent(Splash.this, AllowPermissions.class));
            finish();
        };
        handler.postDelayed(runnable, 4700);

        // raw folder me rkhe video ko apn uri ki help se get kr lenge
        // ye line ka matlab he
        // android.resource://com.androiddeveloperyogesh.videoplayerapp/raw/bg_bamboo
        binding.bg.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_bamboo));
        // URI.parse() function se isme jo string di he uski URI bn k apn ko mil jaegi
        // yha phir apn ne apne videoView me videoUri set kr di he
        binding.bg.start();

        // jese hi videoview prepare ho jae play hone k liye
        // us video ko loop me chla do
        binding.bg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });


        // splash animations
        binding.bg.animate().translationY(-5000).setDuration(1000).setStartDelay(3500);
        binding.parrot.animate().translationY(5000).setDuration(1000).setStartDelay(3500);
        binding.name.animate().translationY(5000).setDuration(1000).setStartDelay(3500);
        Animation animation = AnimationUtils.loadAnimation(Splash.this, R.anim.splashtextanimation2);
        binding.name.startAnimation(animation);
        binding.lottie.animate().translationY(5000).setDuration(1000).setStartDelay(3500);
    }
}


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);