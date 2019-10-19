package com.sinichi.parentingcontrolv3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetAppearance.setExtendStatusBarWithView(this);
        setContentView(R.layout.activity_profile);
        SetAppearance.hideNavigationBar(this);

        BottomNavigationView mBottomNavigation = findViewById(R.id.bottom_navigation);
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_profile);
        mBottomNavigation.setSelectedItemId(R.id.menu_profile);
    }
}
