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

        BottomNavigationView mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_map) {
                    Intent i = new Intent(ProfileActivity.this, MapsActivity.class);
                    startActivity(i);
                    finish();
                } else if (id == R.id.menu_chat) {
                    Intent i = new Intent(ProfileActivity.this, ChatActivity.class);
                    startActivity(i);
                    finish();
                } else if (id == R.id.menu_overview) {
                    Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                return true;
            }
        });
        mBottomNavigation.setSelectedItemId(R.id.menu_profile);
    }
}
