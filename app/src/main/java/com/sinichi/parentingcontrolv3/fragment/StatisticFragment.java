package com.sinichi.parentingcontrolv3.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.model.UserModel;
import com.sinichi.parentingcontrolv3.util.CurrentDimension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatViewHolder simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {
    private MainAlt mainAlt = new MainAlt();
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private FirebaseUser mFirebaseUser;
    private List<DataModel> models;
    private boolean available;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);


        RecyclerView mRecyclerView = root.findViewById(R.id.recyclerView);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");
        mainAlt.parseSnapShot();
        mainAlt.recyclerViewAdapterBuilder(getContext(), kegiatanRef, mRecyclerView);
        mainAlt.retrieveData(true);

        addDataEveryDay();

        return root;
    }

    private void addDataEveryDay() {
        final Calendar calendar = Calendar.getInstance();
        final String date = String.valueOf(calendar.get(Calendar.DATE));
        final String day = CurrentDimension.defineDays(calendar.get(Calendar.DAY_OF_WEEK));
        final String month = CurrentDimension.defineMonth(calendar.get(Calendar.MONTH));
        final String year = String.valueOf(calendar.get(Calendar.YEAR));
        kegiatanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataModel model = snapshot.getValue(DataModel.class);
                    if (model != null) {
                        model.setId(snapshot.getKey());
                    }
                    models.add(model);
                }
                for (DataModel dataModel : models) {
                    available = dataModel.getTanggal().equals(date)
                            && dataModel.getBulan().equals(month)
                            && dataModel.getTahun().equals(year);
                }
                if (!available) {
                    // TODO: Add new data
                    DataModel dataModel = new DataModel(date, day, month, year, "0",
                            false, false);
                    kegiatanRef.push().setValue(dataModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
