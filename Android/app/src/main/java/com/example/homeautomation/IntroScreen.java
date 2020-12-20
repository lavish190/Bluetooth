package com.example.homeautomation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroScreen extends AppIntro {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        sharedPreferences = sharedPreferences = getSharedPreferences("CheckIntro", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setColorDoneText(getResources().getColor(R.color.orange));
        setColorSkipButton(getResources().getColor(R.color.orange));
        setSeparatorColor(getResources().getColor(R.color.orange));
        setIndicatorColor(getResources().getColor(R.color.orange), getResources().getColor(R.color.un_selected_color));

        addSlide(AppIntroFragment.newInstance("",
                "Make your Home Smarter",
                R.drawable.frame4,
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.orange)));


        addSlide(AppIntroFragment.newInstance("",
                "Access your home smart devices right from your mobile ",
                R.drawable.fan,
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.orange)));

        addSlide(AppIntroFragment.newInstance("",
                "Connect your devices with Bluetooth & Wifi",
                R.drawable.lamp,
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.orange)));
    }

    @Override
    public void onSkipPressed() {
        editor.putBoolean("isIntroduced", true).apply();
        startActivity(new Intent(IntroScreen.this, MainActivity.class));
        finish();
    }

    @Override
    public void onNextPressed() {
        // no need to implement here
    }

    @Override
    public void onDonePressed() {
        editor.putBoolean("isIntroduced", true).apply();
        startActivity(new Intent(IntroScreen.this, MainActivity.class));
        finish();
    }

    @Override
    public void onSlideChanged() {
        // no need to implement here
    }
}
