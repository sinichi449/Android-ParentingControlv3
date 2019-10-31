package com.sinichi.parentingcontrolv3.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.adapter.MainViewPagerAdapter;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.util.AlarmNotificationReceiver;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.CurrentDimension;
import com.sinichi.parentingcontrolv3.util.GpsUtil;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.view.LineChartView;

import static com.sinichi.parentingcontrolv3.common.MainAlt.getCurrentDay;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNavigation;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MainAlt mainAlt;
    private ImageView imgHeaderCollapsingToolbar;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;
    private TextView tvHariIni;
    private TextView tvHeaderDate;
    private TextView tvHeaderDetails;
    private LineChartView chart;
    private boolean isGPS = false;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor edit;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private String date, day, month, year;
    private List<DataModel> models;
    private boolean isAvailableTodayData;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        SetAppearance.hideNavigationBar(this);
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_overview);

        if (isLocationPermissionGranted()) { // Cek izin lokasi
            //  Aktifkan GPS
            new GpsUtil(MainActivity.this).turnGPSOn(new GpsUtil.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    isGPS = true;
                }
            });

            // Dapatkan lokasi
            FusedLocationProviderClient mFusedLocation = new FusedLocationProviderClient(MainActivity.this);
            mFusedLocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        try {
                            // Dapatkan nama daerah
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                    location.getLongitude(), 1);
                            sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                            edit = sharedPrefs.edit();
                            edit.putString(Constant.NAMA_KOTA, addresses.get(0).getLocality());
                            edit.apply();
                            Log.e("Locality", addresses.get(0).getLocality());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Akses lokasi terganggu, silakan aktifkan gps Anda lalu restart Aplikasi",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Setting ViewPager
            MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

            // Set header background berdasarkan hari
            setBackgroundReferToDays(imgHeaderCollapsingToolbar);

            // Setting layout
            makeView();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    if (position == 0) {
                        // TODO: Set Calendar Header
//                    constraintLayout.removeView(chart);
                        setBackgroundReferToDays(imgHeaderCollapsingToolbar);
                        makeView();
                    } else if (position == 1) {
                        // TODO: Statistic Header
                        constraintLayout.removeView(tvHariIni);
                        constraintLayout.removeView(tvHeaderDate);
                        constraintLayout.removeView(tvHeaderDetails);
//                    attachImage(R.drawable.background_yellow, imgHeaderCollapsingToolbar);
                        setLineChartView();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    Glide.get(MainActivity.this).clearMemory();
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    if (position == 0) {
                        // TODO: Set Calendar Header
//                    constraintLayout.removeView(chart);
                        setBackgroundReferToDays(imgHeaderCollapsingToolbar);
                        makeView();
                    } else if (position == 1) {
                        // TODO: Statistic Header
                        // TODO: Remove Header date
                        constraintLayout.removeView(tvHariIni);
                        constraintLayout.removeView(tvHeaderDate);
                        constraintLayout.removeView(tvHeaderDetails);
//                    attachImage(R.drawable.background_yellow, imgHeaderCollapsingToolbar);
                        setLineChartView();
                    }
                }
            });
        }
        sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        if (sharedPrefs.getString(Constant.WAKTU_ISYA, null) != null) {
            // TODO: Check di database jika user belum melakukan sholat, munculkan notifikasi
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");
            Calendar calendar = Calendar.getInstance();
            date = String.valueOf(calendar.get(Calendar.DATE));
            day = CurrentDimension.defineDays(calendar.get(Calendar.DAY_OF_WEEK));
            month = CurrentDimension.defineMonth(calendar.get(Calendar.MONTH));
            year = String.valueOf(calendar.get(Calendar.YEAR));
//            kegiatanRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    models = new ArrayList<>();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        DataModel model = snapshot.getValue(DataModel.class);
//                        if (model != null) {
//                            model.setId(snapshot.getKey());
//                        }
//                        models.add(model);
//                    }
//                    // TODO:
//                    for (DataModel dataModel : models) {
//                        isAvailableTodayData = dataModel.getTanggal().equals(date)
//                                || dataModel.getHari().equals(day)
//                                || dataModel.getBulan().equals(month)
//                                || dataModel.getTahun().equals(year);
//                    }
//                    if (!isAvailableTodayData) {
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
            makeNotification(Constant.WAKTU_SUBUH, 1, 10, getJam(Constant.WAKTU_SUBUH), getMenit(Constant.WAKTU_SUBUH));
            makeNotification(Constant.WAKTU_DHUHR, 2, 20, getJam(Constant.WAKTU_DHUHR), getMenit(Constant.WAKTU_DHUHR));
            makeNotification(Constant.WAKTU_ASHAR, 3, 30, getJam(Constant.WAKTU_ASHAR), getMenit(Constant.WAKTU_ASHAR));
            makeNotification(Constant.WAKTU_MAGHRIB, 4, 40, getJam(Constant.WAKTU_MAGHRIB), getMenit(Constant.WAKTU_MAGHRIB));
            makeNotification(Constant.WAKTU_ISYA, 5, 50,getJam(Constant.WAKTU_ISYA), getMenit(Constant.WAKTU_ISYA));
        }
    }

    private void makeNotification(String waktuSholat, int notificationId, int requestCode, int jam, int menit) {
        Intent intent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        intent.putExtra(Constant.INTENT_WAKTU_SHOLAT, waktuSholat);
        intent.putExtra(Constant.INTENT_NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private int getJam(String keyWaktu) {
        String strJam = sharedPrefs.getString(keyWaktu, "Kosong").substring(0, 2);
        return Integer.parseInt(strJam);
    }

    private int getMenit(String keyMenit) {
        String strMenit = sharedPrefs.getString(keyMenit, "Kosong").substring(3,5);
        return Integer.parseInt(strMenit);
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

    private void makeView() {
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

    private void setLineChartView() {
//        chart = new LineChartView(this);
//        chart.setId(ViewCompat.generateViewId());
//        constraintSet.constrainWidth(chart.getId(), ConstraintSet.MATCH_CONSTRAINT);
//        constraintSet.constrainHeight(chart.getId(), setDp(this, 250));
//        chart.setInteractive(true);
//        List<PointValue> values = new ArrayList<>();
//        values.add(new PointValue(0, 2));
//        values.add(new PointValue(1, 4));
//        values.add(new PointValue(2, 3));
//        values.add(new PointValue(3, 4));
//        Line line = new Line(values).setColor(getResources().getColor(R.color.colorSkyBlue))
//                .setCubic(true);
//
//        List<Line> lines = new ArrayList<>();
//        lines.add(line);
//        LineChartData data = new LineChartData();
//        data.setLines(lines);
//        chart.setLineChartData(data);
//        constraintSet.connect(chart.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
//        constraintSet.connect(chart.getId(), ConstraintSet.END, constraintLayout.getId(), ConstraintSet.END);
//        constraintSet.connect(chart.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.BOTTOM);
//        constraintSet.connect(chart.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
//        constraintSet.applyTo(constraintLayout);
//        constraintLayout.addView(chart);
    }

    private void setBackgroundReferToDays(ImageView imgTarget) {
        switch (getCurrentDay()) {
            case "Senin":
                attachImage(R.drawable.bg_colapse_senin, imgTarget);
                break;
            case "Selasa":
                attachImage(R.drawable.bg_colapse_selasa, imgTarget);
                break;
            case "Rabu":
                attachImage(R.drawable.bg_colapse_rabu, imgTarget);
                break;
            case "Kamis":
                attachImage(R.drawable.bg_colapse_kamis, imgTarget);
                break;
            case "Jumat":
                attachImage(R.drawable.bg_colapse_jumat, imgTarget);
                break;
            case "Sabtu":
                attachImage(R.drawable.bg_colapse_sabtu, imgTarget);
                break;
            case "Minggu":
                attachImage(R.drawable.bg_colapse_minggu, imgTarget);
                break;
        }
    }

}
