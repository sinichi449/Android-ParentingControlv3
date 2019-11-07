package com.sinichi.parentingcontrolv3.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.model.QuotesModel;
import com.sinichi.parentingcontrolv3.retrofit.ApiService;
import com.sinichi.parentingcontrolv3.retrofit.Data;
import com.sinichi.parentingcontrolv3.retrofit.JadwalSholat;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.CurrentDimension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
    private DatabaseReference quotesRef;
    private List<DataModel> models;
    private List<QuotesModel> quotesArray;
    private boolean available;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private Calendar calendar;
    private String date, day, month, year;
    private TextView tvTanggal, tvHari, tvBulan, tvTahun,
            tvJumlahSholat;
//    private TextView tvLokasi;
    private CheckBox chkMembantuOrtu, chkSekolah;
//    private TextView tvSubuh, tvDhuhr, tvAshar, tvMaghrib, tvIsya;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean subuh, dhuhr, ashar, maghrib, isya;
    private boolean isGotJson = false;
    private TextView tvQuotes;

    public OverviewFragment() {
        // Required empty public constructor
    }

    private void initComponents() {
        root = inflater.inflate(R.layout.fragment_overview, container, false);
//        tvLokasi = root.findViewById(R.id.tv_lokasi);
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
        tvQuotes = root.findViewById(R.id.tv_quotes);
        quotesRef = mDatabaseReference.child("quotes");

//        tvSubuh = root.findViewById(R.id.tv_subuh);
//        tvDhuhr = root.findViewById(R.id.tv_dhuhr);
//        tvAshar = root.findViewById(R.id.tv_ashar);
//        tvMaghrib = root.findViewById(R.id.tv_maghrib);
//        tvIsya = root.findViewById(R.id.tv_isya);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        initComponents();
        showTodayData();

//        tvLokasi.setText(getLocalityName());
        getJadwalSholat(getLocalityName());

//        QuotesModel quotesModel = new QuotesModel("Wala wala wala wal wal wal wal walwa l", "Bagus Eka Saputra", true);
//        quotesRef.push().setValue(quotesModel);

        getQuotes();
        return root;
    }

    private String getLocalityName() {
        sharedPreferences = getContext().getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constant.NAMA_KOTA, "Jakarta");
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
                    int tanggal = Integer.parseInt(models.get(index).getTanggal());
                    if (tanggal < 10) {
                        tvTanggal.setGravity(Gravity.CENTER);
                    }
                    tvTanggal.setText(models.get(index).getTanggal());
                    tvHari.setText(models.get(index).getHari());
                    tvBulan.setText(models.get(index).getBulan());
                    tvTahun.setText(models.get(index).getTahun());
                    subuh = models.get(index).isSholatSubuh();
                    dhuhr = models.get(index).isSholatDhuhr();
                    ashar = models.get(index).isSholatAshar();
                    maghrib = models.get(index).isSholatMaghrib();
                    isya = models.get(index).isSholatIsya();
                    boolean[] rekap = {subuh, dhuhr, ashar, maghrib, isya};
                    int jumlahSholat = 0;
                    for (boolean adaYgTrue : rekap) {
                        if (adaYgTrue) {
                            jumlahSholat++;
                        }
                    }
                    tvJumlahSholat.setText(String.valueOf(jumlahSholat));
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

    private void getJadwalSholat(String kota) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.JADWAL_SHOLAT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<Data> call = apiService.getData(kota);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Data data = response.body();
                JadwalSholat jadwalSholat = data.getJadwalSholat();
                int percobaan = 0;
                while(!isGotJson) {
                    if (jadwalSholat != null) {
//                        tvSubuh.setText(jadwalSholat.getSubuh());
//                        tvDhuhr.setText(jadwalSholat.getDhuhr());
//                        tvAshar.setText(jadwalSholat.getAshar());
//                        tvMaghrib.setText(jadwalSholat.getMaghrib());
//                        tvIsya.setText(jadwalSholat.getIsya());
                        isGotJson = true;
                    } else {
                        isGotJson = false;
                        percobaan++;
                        if (percobaan > 3) {
                            Toast.makeText(getContext(), "Gagal mendapatkan data sholat, periksa koneksi internet.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                sharedPreferences = getContext().getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(Constant.WAKTU_SUBUH, jadwalSholat.getSubuh());
                editor.putString(Constant.WAKTU_DHUHR, jadwalSholat.getDhuhr());
                editor.putString(Constant.WAKTU_ASHAR, jadwalSholat.getAshar());
                editor.putString(Constant.WAKTU_MAGHRIB, jadwalSholat.getMaghrib());
                editor.putString(Constant.WAKTU_ISYA, jadwalSholat.getIsya());
                editor.apply();
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    private void getQuotes() {
        quotesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quotesArray = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuotesModel quotesModel = snapshot.getValue(QuotesModel.class);
                    if (quotesModel != null) {
                        quotesModel.setId(snapshot.getKey());
                    }
                    quotesArray.add(quotesModel);
                }

                int max = quotesArray.size();
                Random random = new Random();
                int number = random.nextInt(max);
                String quotes = quotesArray.get(number).getQuotes() + "\n-" + quotesArray.get(number).getAuthor();
                tvQuotes.setText(quotes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}