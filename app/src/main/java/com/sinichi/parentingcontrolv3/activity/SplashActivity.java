package com.sinichi.parentingcontrolv3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

public class SplashActivity extends AppCompatActivity {
    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetAppearance.setExtendStatusBarWithView(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        setContentView(R.layout.activity_splash);

        boolean sudahBukaAplikasiSebelumnya = sharedPreferences.getBoolean(Constant.IS_BARU_BUKA_APLIKASI, false);
        if (!sudahBukaAplikasiSebelumnya) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }, (long) (2.3 * 1000));
            Log.e("Status", "First Time");
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(Constant.IS_BARU_BUKA_APLIKASI, true);
            edit.apply();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }, 500);
            Log.e("Status", "Terbiasa");
        }

    }
}
