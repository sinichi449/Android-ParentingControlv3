package com.sinichi.parentingcontrolv3.fragment;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.sinichi.parentingcontrolv3.model.ServerJadwalSholat;
import com.sinichi.parentingcontrolv3.retrofit.MuslimSalat.ApiServiceMuslimSalat;
import com.sinichi.parentingcontrolv3.retrofit.MuslimSalat.ItemMuslimSalat;
import com.sinichi.parentingcontrolv3.retrofit.MuslimSalat.JadwalSholatMuslimSalat;
import com.sinichi.parentingcontrolv3.retrofit.TimeSiswadi.ApiServiceSiswandi;
import com.sinichi.parentingcontrolv3.retrofit.TimeSiswadi.DataSiswandi;
import com.sinichi.parentingcontrolv3.retrofit.TimeSiswadi.JadwalSholatSiswandi;
import com.sinichi.parentingcontrolv3.service.AlarmNotificationReceiver;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.CurrentDimension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * ChatViewHolder simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements ValueEventListener {
    private LayoutInflater inflater;
    private ViewGroup container;
    private View root;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private DatabaseReference quotesRef;
    private List<DataModel> models;
    private List<QuotesModel> quotesArray;
    private List<ServerJadwalSholat> serverList;
    private boolean available;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private Calendar calendar;
    private String date, day, month, year, locality = "Jakarta", server;
    private TextView tvTanggal, tvHari, tvBulan, tvTahun,
            tvJumlahSholat;
//    private TextView tvLokasi;
    private CheckBox chkMembantuOrtu, chkSekolah;
//    private TextView tvSubuh, tvDhuhr, tvAshar, tvMaghrib, tvIsya;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editorJadwal;
    private boolean subuh, dhuhr, ashar, maghrib, isya;
    private boolean isGotJson = false;
    private TextView tvQuotes, tvAuthor;
    private int index;
    private List<Address> a;
    private ProgressDialog progressDialog;
    private Context context;
    private boolean sudahSholatSubuh, sudahSholatDhuhr, sudahSholatAshar, sudahSholatMaghrib, sudahSholatIsya;
    private String jamSekarang, menitSekarang, waktuSekarang;
    private String jamSubuh, jamSyurooq, jamDhuhr, jamAshar, jamMaghrib, jamIsya, waktuSubuh, waktuDhuhr, waktuAshar, waktuMaghrib, waktuIsya;
    private String formattedJam;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;

        initComponents();
        listenDataFromCloud();
        getServer();
        getLocalityName();
        showTodayData();
        getQuotes();
        setOverviewOnClicks();

        return root;
    }

    private void initComponents() {
        context = getContext();
        root = inflater.inflate(R.layout.fragment_overview, container, false);
//        tvLokasi = root.findViewById(R.id.tv_lokasi);
        tvTanggal = root.findViewById(R.id.tv_tanggal);
        tvHari = root.findViewById(R.id.tv_hari);
        tvBulan = root.findViewById(R.id.tv_bulan);
        tvTahun = root.findViewById(R.id.tv_tahun);
        tvJumlahSholat = root.findViewById(R.id.tv_jumlahSholat);
        chkMembantuOrtu = root.findViewById(R.id.chkbx_membantuOrtu);
        chkSekolah = root.findViewById(R.id.chkbx_sekolah);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");
        tvQuotes = root.findViewById(R.id.tv_quotes);
        tvAuthor = root.findViewById(R.id.tv_author);
        quotesRef = mDatabaseReference.child("quotes");
        sharedPrefs = getContext().getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
//        tvSubuh = root.findViewById(R.id.tv_subuh);
//        tvDhuhr = root.findViewById(R.id.tv_dhuhr);
//        tvAshar = root.findViewById(R.id.tv_ashar);
//        tvMaghrib = root.findViewById(R.id.tv_maghrib);
//        tvIsya = root.findViewById(R.id.tv_isya);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Memuat data dari cloud, mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void listenDataFromCloud() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child(Constant.KEGIATAN_REF_CHILD);
        kegiatanRef.addValueEventListener(this);
    }

    private void getServer() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference serverRef = mDatabaseReference.child(Constant.JADWAL_SHOLAT_CHILD);
        serverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ServerJadwalSholat serverJadwalSholat = snapshot.getValue(ServerJadwalSholat.class);
                    if (serverJadwalSholat != null) {
                        serverJadwalSholat.setId(snapshot.getKey());
                    }
                    server = serverJadwalSholat.getServer();
                }
                Log.e(Constant.TAG, "Server Jadwal Sholat: " + server);
                boolean isServerMuslimSalat = server.equals("muslimsalat");
                boolean isServerSiswandi = server.equals("siswandi");
                if (isServerMuslimSalat) {
                    getJadwalSholatMuslimSalat(locality);
                } else if (isServerSiswandi) {
                    getJadwalSholatSiswandi(locality);
                }
                Log.e(Constant.TAG, locality);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error occured, periksa koneksi internet Anda",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocalityName() {
            // Dapatkan lokasi
        FusedLocationProviderClient mFusedLocation = new FusedLocationProviderClient(getActivity());
        a = new ArrayList<>();
        mFusedLocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        // Get nama kota
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        a = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        sharedPrefs = getContext().getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                        editorJadwal = sharedPrefs.edit();
                        locality = a.get(0).getSubAdminArea();
                        editorJadwal.putString(Constant.NAMA_KOTA, locality);
                        editorJadwal.apply();
                        Log.e(Constant.TAG, "Locality: " + a.get(0).getSubAdminArea());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Akses lokasi terganggu, silakan aktifkan gps Anda lalu restart Aplikasi",
                            Toast.LENGTH_LONG).show();
                    locality = "Jakarta";
                }
            }
        });
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
                    index = models.size() - 1;
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
                    chkSekolah.setChecked(models.get(index).isLiterasi());
                    chkMembantuOrtu.setEnabled(false);
                    chkSekolah.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getJadwalSholatSiswandi(String kota) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.JADWAL_SHOLAT_SISWANDI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiServiceSiswandi apiServiceSiswandi = retrofit.create(ApiServiceSiswandi.class);
        Call<DataSiswandi> call = apiServiceSiswandi.getData(kota);
        call.enqueue(new Callback<DataSiswandi>() {
            @Override
            public void onResponse(Call<DataSiswandi> call, Response<DataSiswandi> response) {
                DataSiswandi dataSiswandi = response.body();
                JadwalSholatSiswandi jadwalSholatSiswandi = dataSiswandi.getJadwalSholatSiswandi();
                if (jadwalSholatSiswandi != null) {
                    TextView tvSubuh = root.findViewById(R.id.tv_subuh);
                    TextView tvSyurooq = root.findViewById(R.id.tv_syurooq);
                    TextView tvDhuhr = root.findViewById(R.id.tv_dhuhr);
                    TextView tvAshar = root.findViewById(R.id.tv_ashar);
                    TextView tvMaghrib = root.findViewById(R.id.tv_maghrib);
                    TextView tvIsya = root.findViewById(R.id.tv_isya);

                    tvSubuh.setText(jadwalSholatSiswandi.getSubuh());
                    Log.e(Constant.TAG, jadwalSholatSiswandi.getSubuh());
                    tvDhuhr.setText(jadwalSholatSiswandi.getDhuhr());
                    tvAshar.setText(jadwalSholatSiswandi.getAshar());
                    tvMaghrib.setText(jadwalSholatSiswandi.getMaghrib());
                    tvIsya.setText(jadwalSholatSiswandi.getIsya());

                    editorJadwal.putString(Constant.WAKTU_SUBUH, jadwalSholatSiswandi.getSubuh());
                    editorJadwal.putString(Constant.WAKTU_DHUHR, jadwalSholatSiswandi.getDhuhr());
                    editorJadwal.putString(Constant.WAKTU_ASHAR, jadwalSholatSiswandi.getAshar());
                    editorJadwal.putString(Constant.WAKTU_MAGHRIB, jadwalSholatSiswandi.getMaghrib());
                    editorJadwal.putString(Constant.WAKTU_ISYA, jadwalSholatSiswandi.getIsya());
                    editorJadwal.apply();
                    isGotJson = true;
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Data sholat di server siswandi kosong",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataSiswandi> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(),
                                Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getJadwalSholatMuslimSalat(final String kota) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.JADWAL_SHOLAT_MUSLIM_SALAT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiServiceMuslimSalat apiServiceMuslimSalat = retrofit.create(ApiServiceMuslimSalat.class);
        Call<JadwalSholatMuslimSalat> call = apiServiceMuslimSalat.getJadwalMuslimSalat(kota);
        call.enqueue(new Callback<JadwalSholatMuslimSalat>() {
            @Override
            public void onResponse(Call<JadwalSholatMuslimSalat> call, Response<JadwalSholatMuslimSalat> response) {
                List<ItemMuslimSalat> itemMuslimSalats = response.body().getItemMuslimSalats();
                if (itemMuslimSalats != null) {
                    TextView tvLokasi = root.findViewById(R.id.tv_lokasi);
                    TextView tvSubuh = root.findViewById(R.id.tv_subuh);
                    TextView tvSyurooq = root.findViewById(R.id.tv_syurooq);
                    TextView tvDhuhr = root.findViewById(R.id.tv_dhuhr);
                    TextView tvAshar = root.findViewById(R.id.tv_ashar);
                    TextView tvMaghrib = root.findViewById(R.id.tv_maghrib);
                    TextView tvIsya = root.findViewById(R.id.tv_isya);

                    jamSubuh = formatTo24Hour(itemMuslimSalats.get(0).getSubuh());
                    jamSyurooq = formatTo24Hour(itemMuslimSalats.get(0).getSyurooq());
                    jamDhuhr = formatTo24Hour(itemMuslimSalats.get(0).getDhuhr());
                    jamAshar = formatTo24Hour(itemMuslimSalats.get(0).getAshar());
                    jamMaghrib = formatTo24Hour(itemMuslimSalats.get(0).getMaghrib());
                    jamIsya = formatTo24Hour(itemMuslimSalats.get(0).getIsya());

                    tvLokasi.setText(kota);
                    tvSubuh.setText(jamSubuh);
                    tvSyurooq.setText(jamSyurooq);
                    tvDhuhr.setText(jamDhuhr);
                    tvAshar.setText(jamAshar);
                    tvMaghrib.setText(jamMaghrib);
                    tvIsya.setText(jamIsya);
                    Log.e(Constant.TAG, "Subuh :" + jamSubuh);
                    Log.e(Constant.TAG, "Syurooq :" + jamSyurooq);
                    Log.e(Constant.TAG, "Dhuhr :" + jamDhuhr);
                    Log.e(Constant.TAG, "Ashar :" + jamAshar);
                    Log.e(Constant.TAG, "Maghrib :" + jamMaghrib);
                    Log.e(Constant.TAG, "Isya' :" + jamIsya);

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editorJadwal = sharedPreferences.edit();
                    editorJadwal.putString(Constant.WAKTU_SUBUH, jamSubuh);
                    editorJadwal.putString(Constant.WAKTU_DHUHR, jamDhuhr);
                    editorJadwal.putString(Constant.WAKTU_ASHAR, jamAshar);
                    editorJadwal.putString(Constant.WAKTU_MAGHRIB, jamMaghrib);
                    editorJadwal.putString(Constant.WAKTU_ISYA, jamIsya);
                    editorJadwal.apply();

                    isGotJson = true;
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Data sholat di muslimsalat kosong",
                                Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JadwalSholatMuslimSalat> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(),
                                Toast.LENGTH_LONG).show();
            }
        });

    }

    private void makeNotification(String waktuSholat, int notificationId, int requestCode, int jam, int menit) {
        Intent intent = new Intent(context, AlarmNotificationReceiver.class);
        intent.putExtra(Constant.INTENT_WAKTU_SHOLAT, waktuSholat);
        intent.putExtra(Constant.INTENT_NOTIFICATION_ID, notificationId);
        // TODO: Change request code
        intent.putExtra("request_code", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void makeNotifikasiSholat() {
        boolean isUserAnak = sharedPrefs.getString(Constant.USERNAME, null).equals(Constant.USER_ANAK);
        if (isUserAnak) {
            if (!waktuSubuh.equals("kosong")
                    && !sudahSholatSubuh) {
                int jamSubuh = getJam(Constant.WAKTU_SUBUH);
                int menitSubuh = getMenit(Constant.WAKTU_SUBUH);
                String waktuSubuh = jamSubuh + ":" + menitSubuh;
                makeNotification(Constant.WAKTU_SUBUH, 1, 10, jamSubuh, menitSubuh);
                Log.e(Constant.TAG, "Belum sholat " + Constant.WAKTU_SUBUH);
                Log.e(Constant.TAG, "Notifikasi Subuh: " + waktuSubuh);
            }

            if (!waktuDhuhr.equals("kosong")
                    && !sudahSholatDhuhr) {
                makeNotification(Constant.WAKTU_DHUHR, 2, 20, getJam(Constant.WAKTU_DHUHR), getMenit(Constant.WAKTU_DHUHR));
                Log.e(Constant.TAG, "Belum sholat " + Constant.WAKTU_DHUHR);
            }

            if (!waktuAshar.equals("kosong")
                    && !sudahSholatAshar) {
                int jamAshar = getJam(Constant.WAKTU_ASHAR);
                int menitAshar = getMenit(Constant.WAKTU_ASHAR);
                String waktuAshar = jamAshar + ":" + menitAshar;
                makeNotification(Constant.WAKTU_ASHAR, 3, 30, jamAshar, menitAshar);
                Log.e(Constant.TAG, "Belum sholat " + Constant.WAKTU_ASHAR);
                Log.e(Constant.TAG, "Notifikasi Ashar: " + waktuAshar);
            }

            if (!waktuMaghrib.equals("kosong")
                    && !sudahSholatMaghrib) {
                makeNotification(Constant.WAKTU_MAGHRIB, 4, 40, getJam(Constant.WAKTU_MAGHRIB), getMenit(Constant.WAKTU_MAGHRIB));
                Log.e("Msg", "Belum sholat " + Constant.WAKTU_MAGHRIB);
            }

            if (!waktuIsya.equals("kosong")
                    && !sudahSholatIsya) {
                makeNotification(Constant.WAKTU_ISYA, 5, 50, getJam(Constant.WAKTU_ISYA), getMenit(Constant.WAKTU_ISYA));
                Log.e("Msg", "Belum sholat " + Constant.WAKTU_ISYA);
            }
        }
    }

    private String formatTo24Hour(String waktu) {
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
        String hasil = "";
        try {
            hasil = (date24Format.format(date12Format.parse(waktu)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hasil;
    }

    private int getJam(String keyWaktu) {
        String strJam = sharedPrefs.getString(keyWaktu, "Kosong").substring(0, 2);
        return Integer.parseInt(strJam);
    }

    private int getMenit(String keyMenit) {
        String strMenit = sharedPrefs.getString(keyMenit, "Kosong").substring(3, 5);
        return Integer.parseInt(strMenit);
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
                String quotes = quotesArray.get(number).getQuotes();
                String author = quotesArray.get(number).getAuthor();
                tvQuotes.setText(quotes);
                tvAuthor.setText(author);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOverviewOnClicks() {
        CardView cardView = root.findViewById(R.id.cardview_overview);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDialogRekapHariIni();
            }
        });
    }

    private void launchDialogRekapHariIni() {
        Context context = getContext();
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_data_hariini, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView)
                .setCancelable(true);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView tvHeaderStatus = dialogView.findViewById(R.id.tv_header_status);
        String username = sharedPrefs.getString(Constant.USERNAME, null);
        String header = "";
        if (username.equals(Constant.USER_ORANG_TUA)) {
            header = "Status Anak Anda Hari Ini";
            tvHeaderStatus.setText(header);
        } else if (username.equals(Constant.USER_ANAK)) {
            header = "Status Kamu Hari Ini";
            tvHeaderStatus.setText(header);
        }

        setCheckedTextViewSholat(dialogView);
        setCheckedTextViewMembantuOrtu(dialogView);
        setCheckedTextViewLiterasi(dialogView);
    }

    private void setCheckedTextViewSholat(View v) {
        CheckedTextView chktvSubuh = v.findViewById(R.id.chktv_subuh);
        CheckedTextView chktvDhuhur = v.findViewById(R.id.chktv_dhuhur);
        CheckedTextView chktvAshar = v.findViewById(R.id.chktv_ashar);
        CheckedTextView chktvMaghrib = v.findViewById(R.id.chktv_maghrib);
        CheckedTextView chktvIsya = v.findViewById(R.id.chktv_isya);

        if (models.get(index).isSholatSubuh()) {
            chktvSubuh.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
        if (models.get(index).isSholatDhuhr()) {
            chktvDhuhur.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
        if (models.get(index).isSholatAshar()) {
            chktvAshar.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
        if (models.get(index).isSholatMaghrib()) {
            chktvMaghrib.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
        if (models.get(index).isSholatIsya()) {
            chktvIsya.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
    }

    private void setCheckedTextViewMembantuOrtu(View v) {
        CheckedTextView chktvMembantuOrtu = v.findViewById(R.id.chktv_membantu_ortu);
        EditText edtDeskripsiMembantuOrtu = v.findViewById(R.id.edt_deskripsi_membantu_ortu);
        if (models.get(index).isMembantuOrangTua()) {
            chktvMembantuOrtu.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
        if (models.get(index).getKegiatanMembantu()!= null) {
            edtDeskripsiMembantuOrtu.setText(models.get(index).getKegiatanMembantu());
        }
    }

    private void setCheckedTextViewLiterasi(View v) {
        CheckedTextView chktvLiterasi = v.findViewById(R.id.chktv_literasi);
        EditText edtDeskripsiLiterasi = v.findViewById(R.id.edt_deskripsi_literasi);
        if (models.get(index).getJudulBuku() != null) {
            edtDeskripsiLiterasi.setText(models.get(index).getJudulBuku());
        }
        if (models.get(index).isLiterasi()) {
            chktvLiterasi.setCheckMarkDrawable(R.drawable.ic_check_black_24dp);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        // Get data dari Firebase, lalu dimasukkan ke dalam List<DataModel>
        models = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            DataModel model = snapshot.getValue(DataModel.class);
            if (model != null) {
                model.setId(snapshot.getKey());
//                progressDialog.dismiss();
            }
            models.add(model);
        }

        // Cek apakah data untuk hari ini sudah tersedia
        boolean isAvailableTodayData = false;
        for (DataModel dataModel : models) {
            isAvailableTodayData = dataModel.getTanggal().equals(date)
                    || dataModel.getHari().equals(day)
                    || dataModel.getBulan().equals(month)
                    || dataModel.getTahun().equals(year);
        }

        Log.e(Constant.TAG, "Data Sholat available : " + isAvailableTodayData);
        if (isAvailableTodayData) {
            // Get data terbaru
            int index = models.size() - 1;
            sudahSholatSubuh = models.get(index).isSholatSubuh();
            sudahSholatDhuhr = models.get(index).isSholatDhuhr();
            sudahSholatAshar = models.get(index).isSholatAshar();
            sudahSholatMaghrib = models.get(index).isSholatMaghrib();
            sudahSholatIsya = models.get(index).isSholatIsya();

            Calendar cal = Calendar.getInstance();
            jamSekarang = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
            menitSekarang = String.valueOf(cal.get(Calendar.MINUTE));
            waktuSekarang = jamSekarang + ":" + menitSekarang;

            SharedPreferences sharedPrefs = context.getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);

            waktuSubuh = sharedPrefs.getString(Constant.WAKTU_SUBUH, "kosong");
            waktuDhuhr = sharedPrefs.getString(Constant.WAKTU_DHUHR, "kosong");
            waktuAshar = sharedPrefs.getString(Constant.WAKTU_ASHAR, "kosong");
            waktuMaghrib = sharedPrefs.getString(Constant.WAKTU_MAGHRIB, "kosong");
            waktuIsya = sharedPrefs.getString(Constant.WAKTU_ISYA, "kosong");

            // Hanya user anak yang akan mendapatkan notifikasi
            // Jika user anak belum sholat, beri notifikasi
            makeNotifikasiSholat();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(context, "Something error, please check your internet connection" + "\n" + databaseError.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
}