package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.adapter.MainViewPagerAdapter;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.interfaces.z;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements z {
    private BottomNavigationView mBottomNavigation;
    private ViewPager viewPager;

    @Override
    public void initComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setExtendStatusBarWithView(this);
        }
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_overview);

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        MainAlt mainAlt = new MainAlt();
        Button btnLogOut = findViewById(R.id.btn_logout);
        mainAlt.signOut(this, this, btnLogOut);

        ImageView imgHeaderCalendar = findViewById(R.id.calendar);
        TextView tvDate = findViewById(R.id.tv_header_date);
        TextView tvDateDetails = findViewById(R.id.tv_header_date_details);

        Glide.with(this)
                .load(R.drawable.calendar)
                .into(imgHeaderCalendar);

        Calendar calendar = Calendar.getInstance();
        // Get current date
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        tvDate.setText(formatter.format(calendar.getTime()));

        // Get current day of week
        String[] daysName = {"Minggu", "Senin", "Selasa", "Rabu",
                "Kamis", "Jum'at", "Sabtu"};
        String day = daysName[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        // Get current month
        String[] monthName = {"Januari", "Februari",
                "Maret", "April", "Mei", "Juni", "Juli",
                "Agustus", "September", "Oktober", "November",
                "Desember"};

        String month = monthName[calendar.get(Calendar.MONTH)];

        // Get current year
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());

        // Build combination header details
        String combination = day + ", " + month + " " + year + " ";
        tvDateDetails.setText(combination);
     }
}
