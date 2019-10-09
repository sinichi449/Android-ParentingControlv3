package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, z {

    private RecyclerView mRecyclerView;
    private DatabaseReference kegiatanRef;
    private BottomNavigationView mBottomNavigation;
    private Button btnLogOut;
    private MainAlt mainAlt;

    @Override
    public void initComponents() {
        mainAlt = new MainAlt();
        mRecyclerView = findViewById(R.id.recyclerView);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");
        btnLogOut = findViewById(R.id.btn_logout);
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {
        SetAppearance.onBottomNavigationClick(context, mBottomNav);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setBottomNavigationAction(this, mBottomNavigation);

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
