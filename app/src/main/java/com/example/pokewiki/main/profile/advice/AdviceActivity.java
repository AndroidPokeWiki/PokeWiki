package com.example.pokewiki.main.profile.advice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokewiki.R;

import qiu.niorgai.StatusBarCompat;

/**
 * created by DWF on 2022/5/29.
 */
public class AdviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.profile_suggestion_feedback);

        StatusBarCompat.setStatusBarColor(
                this,
                getResources().getColor(R.color.poke_ball_red, getTheme())
        );
    }
}
