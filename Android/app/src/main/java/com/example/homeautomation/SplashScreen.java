package com.example.homeautomation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = sharedPreferences = getSharedPreferences("CheckIntro", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isIntroduced", false)){
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(SplashScreen.this, IntroScreen.class));
            finish();
        }
    }
}
