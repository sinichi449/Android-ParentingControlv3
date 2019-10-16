package com.sinichi.parentingcontrolv3.activity;

import android.os.Bundle;

import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.MakeSplash;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private MakeSplash a = new MakeSplash();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetAppearance.setExtendStatusBarWithView(this);
        setContentView(R.layout.activity_splash);
        a.makeSplash(this, this, LoginActivity.class, 2.3f);
    }
}
