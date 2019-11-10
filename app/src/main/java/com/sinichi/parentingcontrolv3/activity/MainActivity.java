package com.sinichi.parentingcontrolv3.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.adapter.MainViewPagerAdapter;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.service.LocationService;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.CurrentDimension;
import com.sinichi.parentingcontrolv3.util.GpsUtil;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.view.LineChartView;

import static com.sinichi.parentingcontrolv3.common.MainAlt.getCurrentDay;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigation;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainAlt mainAlt;
    private ImageView imgHeaderCollapsingToolbar;
    private ConstraintLayout constraintLayout;
    private LinearLayout linearLayoutCollapsingToolbar, linearLayoutJadwalSholat;
    private ConstraintSet constraintSet;
    private TextView tvHariIni;
    private TextView tvHeaderDate;
    private TextView tvHeaderDetails;
    private LineChartView chart;
    private boolean isGPS = false;
    private boolean isAvailableTodayData;
    private boolean sudahSholatSubuh, sudahSholatDhuhr, sudahSholatAshar,
            sudahSholatMaghrib, sudahSholatIsya;
    private boolean flag;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor edit;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private String date, day, month, year;
    private String waktuSubuh, waktuDhuhr, waktuAshar,
            waktuMaghrib, waktuIsya;
    private String jamSekarang, menitSekarang, waktuSekarang;
    private List<DataModel> models;
    private List<Address> a;
    // Floating button
    private ImageView flbAdd;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressDialog progressDialog;
    public boolean dataNotFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setInitialJadwalSholat();
//        getUsernameFromCloud();
        Intent intent = getIntent();
        boolean isDiperbarui = intent.getBooleanExtra("after_notification", false);
        if (isDiperbarui) {
            Toast.makeText(MainActivity.this, "Data berhasil diperbarui",
                    Toast.LENGTH_LONG).show();
        }

        SetAppearance.hideNavigationBar(this);
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_overview);
        // Setting ViewPager
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.TabLayoutSelectedIndicator));
        // Set header background berdasarkan hari
        setBackgroundReferToDays(imgHeaderCollapsingToolbar);
        // Setting layout
        tabZeroView();
//        tabLayout.addOnTabSelectedListener(this);
        getNamaKota();
        startLocationService();
        // Get user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        // Get database
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");
        // Get tanggal, hari, bulan, tahun hari ini
        Calendar calendar = Calendar.getInstance();
        date = String.valueOf(calendar.get(Calendar.DATE));
        day = CurrentDimension.defineDays(calendar.get(Calendar.DAY_OF_WEEK));
        month = CurrentDimension.defineMonth(calendar.get(Calendar.MONTH));
        year = String.valueOf(calendar.get(Calendar.YEAR));
        // Lakukan operasi berikut jika username == anak
        String username = sharedPrefs.getString(Constant.USERNAME, null); // Get nama username
        boolean isUsernameAnak = username.equals(Constant.USER_ANAK);
        boolean isUsernameOrangTua = username.equals(Constant.USER_ORANG_TUA);
//        kegiatanRef.addValueEventListener(this);
        if (username != null
                && isUsernameAnak) {
            onFlbAddClicked();
        } else if (username != null
                && isUsernameOrangTua) {
            flbAdd.setVisibility(View.GONE);
        }
        tabZeroView();

    }

    public void initComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setExtendStatusBarWithView(this);
        }
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        mainAlt = new MainAlt();
        imgHeaderCollapsingToolbar = findViewById(R.id.img_header_calendar);
        constraintSet = new ConstraintSet();
        constraintLayout = findViewById(R.id.constraint_layout_collapsingtoolbar);
        constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        flbAdd = findViewById(R.id.flb_add);
        linearLayoutCollapsingToolbar = findViewById(R.id.linear_layout_collapsingtoolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    }


    private void getNamaKota() {
        if (isLocationPermissionGranted()) {
            String username = sharedPrefs.getString(Constant.USERNAME, null);
            //  Aktifkan GPS
            new GpsUtil(MainActivity.this).turnGPSOn(new GpsUtil.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    isGPS = true;
                }
            });
//            getLokasi();
            Log.e("Status", "User saat ini " + username);
        }
    }

    private void startLocationService() {
        String username = sharedPrefs.getString(Constant.USERNAME, null);
        if (username != null && username.equals(Constant.USER_ANAK)) {
            Intent intent = new Intent(this, LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
            Log.e("Status", "User saat ini " + username);
        }
    }

    private boolean isLocationPermissionGranted() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, permission[0])
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, permission[1])
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permission, Constant.GPS_REQUEST);
            return false;
        }
        return true;
    }

    public static int setDp(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void attachImage(int resid, ImageView imageTarget) {
        Glide.with(MainActivity.this)
                .load(resid)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageTarget);
    }

    private void tabZeroView() {
        setTvHariIni();
        setTvHeaderDate();
        setTvHeaderDetails();
        constraintLayout.addView(tvHariIni);
        constraintLayout.addView(tvHeaderDate);
        constraintLayout.addView(tvHeaderDetails);
        constraintSet.applyTo(constraintLayout);
        mainAlt.setCollapsingCalendar(tvHeaderDate, tvHeaderDetails);
    }

    private void setTvHariIni() {
        tvHariIni = new TextView(this);
        tvHariIni.setId(ViewCompat.generateViewId());
        constraintSet.constrainWidth(tvHariIni.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(tvHariIni.getId(), ConstraintSet.WRAP_CONTENT);
//        constraintSet.setMargin(tvHariIni.getId(), ConstraintSet.TOP, setDp(MainActivity.this, 10));
        tvHariIni.setText("Hari ini");
        tvHariIni.setTypeface(ResourcesCompat.getFont(this, R.font.arial));
        tvHariIni.setTextColor(getResources().getColor(R.color.colorWhite));
        tvHariIni.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        tvHariIni.setTypeface(null, Typeface.BOLD);
        constraintSet.connect(tvHariIni.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        constraintSet.connect(tvHariIni.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        constraintSet.connect(tvHariIni.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, setDp(MainActivity.this, 10));
    }

    private void setTvHeaderDate() {
        tvHeaderDetails = new TextView(this);
        tvHeaderDetails.setId(ViewCompat.generateViewId());
        tvHeaderDate = new TextView(this);
        tvHeaderDate.setId(ViewCompat.generateViewId());
        constraintSet.constrainWidth(tvHeaderDate.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(tvHeaderDate.getId(), ConstraintSet.WRAP_CONTENT);
        tvHeaderDate.setTypeface(ResourcesCompat.getFont(this, R.font.arial_rounded));
        tvHeaderDate.setText("16");
        tvHeaderDate.setTextColor(getResources().getColor(R.color.colorWhite));
        tvHeaderDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 120);
        tvHeaderDate.setTypeface(null, Typeface.BOLD);
        constraintSet.connect(tvHeaderDate.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        constraintSet.connect(tvHeaderDate.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        constraintSet.connect(tvHeaderDate.getId(), ConstraintSet.TOP, tvHariIni.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(tvHeaderDate.getId(), ConstraintSet.BOTTOM, tvHeaderDetails.getId(), ConstraintSet.TOP);
    }

    private void setTvHeaderDetails() {
        constraintSet.constrainWidth(tvHeaderDetails.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(tvHeaderDetails.getId(), ConstraintSet.WRAP_CONTENT);
        tvHeaderDetails.setText("Jumat, Maret 2001");
        tvHeaderDetails.setTypeface(null, Typeface.BOLD_ITALIC);
        tvHeaderDetails.setTypeface(ResourcesCompat.getFont(this, R.font.arial));
        tvHeaderDetails.setTextColor(getResources().getColor(R.color.colorWhite));
        tvHeaderDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        constraintSet.connect(tvHeaderDetails.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
        constraintSet.connect(tvHeaderDetails.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
        constraintSet.connect(tvHeaderDetails.getId(), ConstraintSet.TOP, tvHeaderDate.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(tvHeaderDetails.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, setDp(MainActivity.this, 10));
    }

    private void setBackgroundReferToDays(ImageView imgTarget) {
        switch (getCurrentDay()) {
            case "Senin":
                attachImage(R.drawable.back2, imgTarget);
                break;
            case "Selasa":
                attachImage(R.drawable.back3, imgTarget);
                break;
            case "Rabu":
                attachImage(R.drawable.back4, imgTarget);
                break;
            case "Kamis":
                attachImage(R.drawable.back1, imgTarget);
                break;
            case "Jumat":
                attachImage(R.drawable.back5, imgTarget);
                break;
            case "Sabtu":
                attachImage(R.drawable.back6, imgTarget);
                break;
            case "Minggu":
                attachImage(R.drawable.back7, imgTarget);
                break;
        }
    }

    private void onFlbAddClicked() {
        ImageView imgAdd = findViewById(R.id.flb_add);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = getLayoutInflater().inflate(R.layout.layout_add_data, null);
                final Context context = MainActivity.this;
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                final RadioGroup rgMembantuOrtu = dialogView.findViewById(R.id.rg_membantu_ortu);
                final RadioGroup rgLiterasi = dialogView.findViewById(R.id.rg_literasi);
                final EditText edtJudulBuku = dialogView.findViewById(R.id.edt_judul_buku);
                final EditText edtKegiatanMembantu = dialogView.findViewById(R.id.edt_membantu_ortu);
                final Button btnSubmit = dialogView.findViewById(R.id.btn_ok);
                Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String judulBuku = edtJudulBuku.getText().toString();
                        String kegiatanMembantuOrtu = edtKegiatanMembantu.getText().toString();
                        boolean judulBukuTidakKosong = !edtJudulBuku.getText().toString().equals("");
                        boolean radioGroupTidakKosong = rgMembantuOrtu.getCheckedRadioButtonId() != -1;
                        boolean radioGroupLiterasiTidakKosong = rgLiterasi.getCheckedRadioButtonId() != -1;
                        if (radioGroupTidakKosong && judulBukuTidakKosong && radioGroupLiterasiTidakKosong) {
                            int selectedIdMembantuOrtu = rgMembantuOrtu.getCheckedRadioButtonId();
                            int selectedIdLiterasi = rgLiterasi.getCheckedRadioButtonId();
                            RadioButton radioButtoMembantuOrtu = dialogView.findViewById(selectedIdMembantuOrtu);
                            RadioButton radioButtonLiterasi = dialogView.findViewById(selectedIdLiterasi);

                            Log.e(Constant.TAG, String.valueOf(radioButtoMembantuOrtu.getText()));

                            String membantuOrangTua = String.valueOf(radioButtoMembantuOrtu.getText());
                            switch (membantuOrangTua) {
                                case "Sudah":
                                    setMembantuOrangTua(true, kegiatanMembantuOrtu);
                                    break;
                                case "Belum":
                                    setMembantuOrangTua(false, kegiatanMembantuOrtu);
                                    break;
                            }

                            String sudahLiterasi = String.valueOf(radioButtonLiterasi.getText());
                            switch (sudahLiterasi) {
                                case "Sudah":
                                    sendJudulBuku(true, judulBuku);
                                    break;
                                case "Belum":
                                    sendJudulBuku(false, judulBuku);
                            }

                            Toast.makeText(MainActivity.this, "Data berhasil diperbarui",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Mohon isi form yang kosong!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void setMembantuOrangTua(final boolean sudahMembantuOrtu, final String kegiatan) {
        kegiatanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataModel model = new DataModel();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    model = snapshot.getValue(DataModel.class);
                    if (model != null) {
                        model.setId(snapshot.getKey());
                    }
                }
                if (sudahMembantuOrtu && !kegiatan.equals("")) {
                    kegiatanRef.child(model.getId()).child("membantuOrangTua").setValue(true);
                    kegiatanRef.child(model.getId()).child(Constant.KEGIATANMEMBANTU_CHILD).setValue(kegiatan);
                } else if (!sudahMembantuOrtu) {
                    kegiatanRef.child(model.getId()).child("membantuOrangTua").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error occured, hubungi developer untuk mendapatkan bantuan."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendJudulBuku(final boolean sudahLiterasi, final String judulBuku) {
        kegiatanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataModel model = new DataModel();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    model = snapshot.getValue(DataModel.class);
                    if (model != null) {
                        model.setId(snapshot.getKey());
                    }
                }
                if (!judulBuku.equals("")) {
                    kegiatanRef.child(model.getId()).child(Constant.JUDULBUKU_CHILD).setValue(judulBuku);
                }
                if (sudahLiterasi) {
                    kegiatanRef.child(model.getId()).child(Constant.LITERASI).setValue(sudahLiterasi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error occured, hubungi developer untuk mendapatkan bantuan."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetAppearance.hideNavigationBar(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SetAppearance.hideNavigationBar(this);
    }

    private void setInitialJadwalSholat() {
        SharedPreferences sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.WAKTU_SUBUH, "04:00");
        editor.putString(Constant.WAKTU_DHUHR, "12:00");
        editor.putString(Constant.WAKTU_ASHAR, "15:00");
        editor.putString(Constant.WAKTU_MAGHRIB, "18:00");
        editor.putString(Constant.WAKTU_ISYA, "19:15");
        editor.apply();
    }

}
