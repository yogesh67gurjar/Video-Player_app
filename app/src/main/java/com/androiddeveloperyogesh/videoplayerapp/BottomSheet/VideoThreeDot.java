package com.androiddeveloperyogesh.videoplayerapp.BottomSheet;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.animation.content.Content;
import com.androiddeveloperyogesh.videoplayerapp.R;
import com.androiddeveloperyogesh.videoplayerapp.databinding.FragmentVideoThreeDotBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;


public class VideoThreeDot extends BottomSheetDialogFragment {

    FragmentVideoThreeDotBinding binding;

    Context context;

    public VideoThreeDot(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoThreeDotBinding.inflate(inflater, container, false);


        binding.videoTitleBottomSheet.setText(getArguments().getString("thumbnail"));

        Glide.with(context).load(new File(getArguments().getString("name"))).placeholder(R.drawable.img_thumbnail).into(binding.thumbNail);


        return binding.getRoot();
    }
}