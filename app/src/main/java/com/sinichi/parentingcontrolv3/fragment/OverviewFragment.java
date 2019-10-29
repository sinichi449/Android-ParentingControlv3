package com.sinichi.parentingcontrolv3.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.ApiService;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.model.Item;
import com.sinichi.parentingcontrolv3.model.JadwalSholat;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.CurrentDimension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * ChatViewHolder simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {
    private LayoutInflater inflater;
    private ViewGroup container;
    private View root;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private List<DataModel> models;
    private boolean available;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private Calendar calendar;
    private String date, day, month, year;
    private TextView tvTanggal, tvHari, tvBulan, tvTahun,
            tvJumlahSholat;
    private CheckBox chkMembantuOrtu, chkSekolah;
    private List<JadwalSholat> jadwalSholats;
    private TextView tvSubuh, tvDhuhr, tvAshar, tvMaghrib, tvIsya;
    private String lokasi;
    private String subuh, dhuhr, ashar, maghrib, isya;
    private ProgressBar progressBar;

    public OverviewFragment() {
        // Required empty public constructor
    }

    private void initComponents() {
        root = inflater.inflate(R.layout.fragment_overview, container, false);
        tvTanggal = root.findViewById(R.id.tv_tanggal);
        tvHari = root.findViewById(R.id.tv_hari);
        tvBulan = root.findViewById(R.id.tv_bulan);
        tvTahun = root.findViewById(R.id.tv_tahun);
        tvJumlahSholat = root.findViewById(R.id.tv_jumlahSholat);
        chkMembantuOrtu = root.findViewById(R.id.chkbx_membantuOrtu);
        chkSekolah = root.findViewById(R.id.chkbx_sekolah);
        progressBar = root.findViewById(R.id.progress_circular);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");

        tvSubuh = root.findViewById(R.id.tv_subuh);
        tvDhuhr = root.findViewById(R.id.tv_dhuhr);
        tvAshar = root.findViewById(R.id.tv_ashar);
        tvMaghrib = root.findViewById(R.id.tv_maghrib);
        tvIsya = root.findViewById(R.id.tv_isya);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        initComponents();

        showTodayData();
        getJadwalSholat();

        return root;
    }

    private void showTodayData() {
        calendar = Calendar.getInstance();
        date = String.valueOf(calendar.get(Calendar.DATE));
        day = CurrentDimension.defineDays(calendar.get(Calendar.DAY_OF_WEEK));
        month = CurrentDimension.defineMonth(calendar.get(Calendar.MONTH));
        year = String.valueOf(calendar.get(Calendar.YEAR));
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
                            || dataModel.getBulan().equals(month)
                            || dataModel.getTahun().equals(year);
                }
                if (available) {
                    int index = models.size() - 1;
                    tvTanggal.setText(models.get(index).getTanggal());
                    tvHari.setText(models.get(index).getHari());
                    tvBulan.setText(models.get(index).getBulan());
                    tvTahun.setText(models.get(index).getTahun());
                    tvJumlahSholat.setText(models.get(index).getJumlahSholat());
                    chkMembantuOrtu.setChecked(models.get(index).isMembantuOrangTua());
                    chkSekolah.setChecked(models.get(index).isSekolah());
                    tvJumlahSholat.setEnabled(false);
                    chkMembantuOrtu.setEnabled(false);
                    chkSekolah.setEnabled(false);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getJadwalSholat() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.JADWAL_SHOLAT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService api = retrofit.create(ApiService.class);
        Call<Item> call = api.getJadwalSholat();
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                jadwalSholats = response.body().getItems();
                writeToTextViews(jadwalSholats);

            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    private void writeToTextViews(List<JadwalSholat> jadwalSholatList) {
        tvSubuh.setText(jadwalSholatList.get(0).getSubuh());
        tvDhuhr.setText(jadwalSholatList.get(0).getDhuhr());
        tvAshar.setText(jadwalSholatList.get(0).getAshar());
        tvMaghrib.setText(jadwalSholatList.get(0).getMaghrib());
        tvIsya.setText(jadwalSholatList.get(0).getIsya());
    }
}
