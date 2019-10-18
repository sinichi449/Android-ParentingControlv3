package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.LoginActivity;
import com.sinichi.parentingcontrolv3.interfaces.d;
import com.sinichi.parentingcontrolv3.model.Model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainAlt implements d {
    private SnapshotParser<Model> parser;
    private static FirebaseRecyclerAdapter<Model, DataSholatViewHolder.DataViewHolder> mFirebaseAdapter;

    @Override
    public void parseSnapShot() {
        parser = new SnapshotParser<Model>() {
            @NonNull
            @Override
            public Model parseSnapshot(@NonNull DataSnapshot snapshot) {
                Model model = snapshot.getValue(Model.class);
                if (model != null) {
                    model.setId(snapshot.getKey());
                }
                return model;
            }
        };
    }

    @Override
    public void recyclerViewAdapterBuilder(Context context, DatabaseReference kegiatanRef, RecyclerView localRecyclerView) {
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(kegiatanRef, parser).build();
        mFirebaseAdapter =
                new FirebaseRecyclerAdapter<Model, DataSholatViewHolder.DataViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull DataSholatViewHolder.DataViewHolder dataViewHolder, int i, @NonNull Model model) {
                        dataViewHolder.tvTanggal.setText(model.getTanggal());
                        dataViewHolder.tvHari.setText(model.getHari());
                        dataViewHolder.tvBulan.setText(model.getBulan());
                        dataViewHolder.tvTahun.setText(model.getTahun());
                        dataViewHolder.tvJumlahSholat.setText(model.getJumlahSholat());
                        dataViewHolder.chkMembantuOrtu.setChecked(model.isMembantuOrangTua());
                        dataViewHolder.chkSekolah.setChecked(model.isSekolah());
                        dataViewHolder.chkMembantuOrtu.setEnabled(false);
                        dataViewHolder.chkSekolah.setEnabled(false);
                    }
                    @NonNull
                    @Override
                    public DataSholatViewHolder.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater inflater1 = LayoutInflater.from(parent.getContext());
                        return new DataSholatViewHolder.DataViewHolder(inflater1.inflate(
                                R.layout.layout_item, parent, false));
                    }
                };
        localRecyclerView.setAdapter(mFirebaseAdapter);
        localRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,
                false));
    }

    @Override
    public void retrieveData(boolean setActive) {
        if (setActive) {
            mFirebaseAdapter.startListening();
        } else {
            mFirebaseAdapter.stopListening();
        }
    }

    @Override
    public void signOut(final Activity activity, final Context context, Button btnLogOut) {
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(context, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
                activity.finish();
            }
        });
    }

    public void setCollapsingCalendar(TextView tvDate, TextView tvDateDetails) {
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
