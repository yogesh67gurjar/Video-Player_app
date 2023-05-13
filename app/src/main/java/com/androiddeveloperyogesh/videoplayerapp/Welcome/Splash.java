package com.androiddeveloperyogesh.videoplayerapp.Welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.androiddeveloperyogesh.videoplayerapp.MainActivity;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.ActivitySplashBinding;

public class Splash extends AppCompatActivity {
    ActivitySplashBinding binding;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    protected void onStart() {
        super.onStart();

        new CountDownTimer(2600, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                intent = new Intent(Splash.this, AllowPermissions.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}