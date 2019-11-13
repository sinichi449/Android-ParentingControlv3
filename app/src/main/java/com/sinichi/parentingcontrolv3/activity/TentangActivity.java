package com.sinichi.parentingcontrolv3.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.sinichi.parentingcontrolv3.R;

public class TentangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
        setActionBar();

    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tentang Aplikasi");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorSkyBlue)));
    }
}
