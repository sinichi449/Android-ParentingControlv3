package com.sinichi.parentingcontrolv3.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView tvNama, tvAnakOrOrtu, tvAlamat,
            tvSekolah, tvNomorTelepon, tvTanggalLahir;
    private BottomNavigationView mBottomNavigation;
    private ImageView imgSignOut;

    private void initComponents() {
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
//        editor = sharedPreferences.edit();
        tvNama = findViewById(R.id.tv_nama);
        tvAnakOrOrtu = findViewById(R.id.tv_anak_ortu);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvSekolah = findViewById(R.id.tv_sekolah);
        tvNomorTelepon = findViewById(R.id.tv_nomor_telepon);
        tvTanggalLahir = findViewById(R.id.tv_tanggal_lahir);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        imgSignOut = findViewById(R.id.btn_signout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetAppearance.setExtendStatusBarWithView(this);
        setContentView(R.layout.activity_profile);
        SetAppearance.hideNavigationBar(this);
        SetAppearance.hideNavigationBar(this);
        initComponents();
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_profile);
        mBottomNavigation.setSelectedItemId(R.id.menu_profile);

        String nama = sharedPreferences.getString(Constant.DATA_NAMA, null);
        String tipeAkun = sharedPreferences.getString(Constant.USERNAME, null);
        String alamat = sharedPreferences.getString(Constant.DATA_ALAMAT, null);
        String sekolah = sharedPreferences.getString(Constant.DATA_SEKOLAH, "SMK Negeri 1 Turen");
        String nomorTelepon = sharedPreferences.getString(Constant.DATA_NOMOR_TELEPON, null);
        String tanggalLahir = sharedPreferences.getString(Constant.DATA_TANGGAL_LAHIR, null);
        final MainAlt mainAlt = new MainAlt();
        if (nama == null && tipeAkun == null && alamat == null &&
                sekolah == null && nomorTelepon == null && tanggalLahir  == null) {
            mainAlt.signOut(ProfileActivity.this, ProfileActivity.this, imgSignOut);
        } else {
            tvNama.setText(nama);
            tvAnakOrOrtu.setText(tipeAkun);
            tvAlamat.setText(alamat);
            tvSekolah.setText(sekolah);
            tvNomorTelepon.setText(nomorTelepon);
            tvTanggalLahir.setText(tanggalLahir);
        }
        imgSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAlt.signOut(ProfileActivity.this, ProfileActivity.this, imgSignOut);
            }
        });
    }
}
