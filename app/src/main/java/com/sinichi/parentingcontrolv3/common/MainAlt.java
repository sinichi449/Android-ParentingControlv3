package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.LoginActivity;
import com.sinichi.parentingcontrolv3.interfaces.d;
import com.sinichi.parentingcontrolv3.model.DataModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainAlt implements d {
    private SnapshotParser<DataModel> parser;
    private static FirebaseRecyclerAdapter<DataModel, DataSholatViewHolder.DataViewHolder> mFirebaseAdapter;
    private boolean subuh, dhuhr, ashar, maghrib, isya;

    @Override
    public void parseSnapShot() {
        parser = new SnapshotParser<DataModel>() {
            @NonNull
            @Override
            public DataModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                DataModel dataModel = snapshot.getValue(DataModel.class);
                if (dataModel != null) {
                    dataModel.setId(snapshot.getKey());
                }
                return dataModel;
            }
        };
    }

    @Override
    public void recyclerViewAdapterBuilder(Context context, DatabaseReference kegiatanRef, RecyclerView localRecyclerView) {
        FirebaseRecyclerOptions<DataModel> options = new FirebaseRecyclerOptions.Builder<DataModel>()
                .setQuery(kegiatanRef, parser).build();
        mFirebaseAdapter =
                new FirebaseRecyclerAdapter<DataModel, DataSholatViewHolder.DataViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull DataSholatViewHolder.DataViewHolder dataViewHolder, int i, @NonNull DataModel dataModel) {
                        int tanggal = Integer.parseInt(dataModel.getTanggal());
                        if (tanggal < 10) {
                            dataViewHolder.tvTanggal.setGravity(Gravity.CENTER);
                        }
                        dataViewHolder.tvTanggal.setText(dataModel.getTanggal());
                        dataViewHolder.tvHari.setText(dataModel.getHari());
                        dataViewHolder.tvBulan.setText(dataModel.getBulan());
                        dataViewHolder.tvTahun.setText(dataModel.getTahun());
                        subuh = dataModel.isSholatSubuh();
                        dhuhr = dataModel.isSholatDhuhr();
                        ashar = dataModel.isSholatAshar();
                        maghrib = dataModel.isSholatMaghrib();
                        isya = dataModel.isSholatIsya();
                        boolean[] rekap = {subuh, dhuhr, ashar, maghrib, isya};
                        int jumlahSholat = 0;
                        for (boolean adaYgTrue : rekap) {
                            if (adaYgTrue) {
                                jumlahSholat++;
                            }
                        }
                        dataViewHolder.tvJumlahSholat.setText(String.valueOf(jumlahSholat));
                        dataViewHolder.chkMembantuOrtu.setChecked(dataModel.isMembantuOrangTua());
                        dataViewHolder.chkSekolah.setChecked(dataModel.isSekolah());
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
                true));
        localRecyclerView.setHasFixedSize(true);
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

    public void signOut(final Activity activity, final Context context, ImageView imgSignOut) {
        imgSignOut.setOnClickListener(new View.OnClickListener() {
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

    public static String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        String[] days = {"Minggu", "Senin", "Selasa", "Rabu",
                "Kamis", "Jumat", "Sabtu"};
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
