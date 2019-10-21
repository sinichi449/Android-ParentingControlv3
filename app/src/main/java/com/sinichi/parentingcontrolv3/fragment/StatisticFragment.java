package com.sinichi.parentingcontrolv3.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.common.MainAlt;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ChatViewHolder simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {
    MainAlt mainAlt = new MainAlt();

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);


        RecyclerView mRecyclerView = root.findViewById(R.id.recyclerView);
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");

        mainAlt.parseSnapShot();
        mainAlt.recyclerViewAdapterBuilder(getContext(), kegiatanRef, mRecyclerView);
        mainAlt.retrieveData(true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainAlt.retrieveData(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainAlt.retrieveData(false);
    }
}
