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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.interfaces.z;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, z {

    private RecyclerView mRecyclerView;
    private DatabaseReference kegiatanRef;
    private BottomNavigationView mBottomNavigation;
    private Button btnLogOut;
    private ImageView imgHeaderCalendar;
    private MainAlt mainAlt;
    private TextView tvDate;
    private TextView tvDateDetails;

    @Override
    public void initComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setExtendStatusBarWithView(this);
        }
        mainAlt = new MainAlt();
        mRecyclerView = findViewById(R.id.recyclerView);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");
        btnLogOut = findViewById(R.id.btn_logout);
        imgHeaderCalendar = findViewById(R.id.calendar);
        Glide.with(this)
                .load(R.drawable.calendar)
                .into(imgHeaderCalendar);
        tvDate = findViewById(R.id.tv_header_date);
        tvDateDetails = findViewById(R.id.tv_header_date_details);
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        Calendar calendar = Calendar.getInstance();

        // Get current date
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        tvDate.setText(formatter.format(calendar.getTime()));

        // Get current day of week
        String[] daysName = {"Minggu", "Senin", "Selasa", "Rabu",
                "Kamis", "Jum'at", "Sabtu"};
        String day = daysName[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        // Get current month
        String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "Oktober", "November",
                "December"};

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];

        // Get current year
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());

        // Build combination header details
        String combination = day + ", " + month + " " + year + " ";
        tvDateDetails.setText(combination);


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

        // Set class Model as data container from the cloud
        mainAlt.parseSnapShot();

        // Make RecyclerView to show data
        mainAlt.recyclerViewAdapterBuilder(this, kegiatanRef, mRecyclerView);

        // Start listening for any change from cloud
        mainAlt.retrieveData(true);

        // SignOut when user clicked btnLogOut
        mainAlt.signOut(this, this, btnLogOut);
     }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Service Error",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainAlt.retrieveData(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainAlt.retrieveData(false);
    }
}
