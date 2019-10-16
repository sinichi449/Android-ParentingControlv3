package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.adapter.MainViewPagerAdapter;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.interfaces.z;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements z {
    private BottomNavigationView mBottomNavigation;
    private Button btnLogOut;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public void initComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setExtendStatusBarWithView(this);
        }
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        btnLogOut = findViewById(R.id.btn_logout);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_map) {
                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(i);
                    finish();
                } else if (id == R.id.menu_chat) {
                    Intent i = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(i);
                    finish();
                }
                return true;
            }
        });
        mBottomNavigation.setSelectedItemId(R.id.menu_overview);

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
     }
}
