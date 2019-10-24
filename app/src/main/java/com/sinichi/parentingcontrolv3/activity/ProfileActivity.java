package com.sinichi.parentingcontrolv3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView tvNama, tvTipeAkun, tvAlamat,
            tvSekolah, tvNomorTelepon, tvTanggalLahir;
    private ImageView imgHeaderProfile;
    private BottomNavigationView mBottomNavigation;
    private ImageView imgSignOut;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private void initComponents() {
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
        imgHeaderProfile = findViewById(R.id.img_profile_header);
        Glide.with(this)
                .load(R.drawable.headprofile)
                .into(imgHeaderProfile);
        tvNama = findViewById(R.id.tv_nama);
        tvTipeAkun = findViewById(R.id.tv_anak_ortu);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvSekolah = findViewById(R.id.tv_sekolah);
        tvNomorTelepon = findViewById(R.id.tv_nomor_telepon);
        tvTanggalLahir = findViewById(R.id.tv_tanggal_lahir);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        imgSignOut = findViewById(R.id.btn_signout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
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

        final MainAlt mainAlt = new MainAlt();
        String nama = sharedPreferences.getString(Constant.DATA_NAMA, null);
        String tipeAkun = sharedPreferences.getString(Constant.USERNAME, null);
        String alamat = sharedPreferences.getString(Constant.DATA_ALAMAT, null);
        String sekolah = sharedPreferences.getString(Constant.DATA_SEKOLAH, "SMK Negeri 1 Turen");
        String nomorTelepon = sharedPreferences.getString(Constant.DATA_NOMOR_TELEPON, null);
        String tanggalLahir = sharedPreferences.getString(Constant.DATA_TANGGAL_LAHIR, null);
        try {
            if (nama.equals("") && tipeAkun.equals("") && alamat.equals("")
                    && sekolah.equals("") && nomorTelepon.equals("") && tanggalLahir.equals("")) {
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                tvNama.setText(nama);
                tvTipeAkun.setText(tipeAkun);
                tvAlamat.setText(alamat);
                tvSekolah.setText(sekolah);
                tvNomorTelepon.setText(nomorTelepon);
                tvTanggalLahir.setText(tanggalLahir);
            }
        } catch (NullPointerException npe) {
            Toast.makeText(ProfileActivity.this, "The user data is empty. You must relogin.", Toast.LENGTH_SHORT)
                    .show();
            mFirebaseAuth.signOut();
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        imgSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAlt.signOut(ProfileActivity.this, ProfileActivity.this, imgSignOut);
            }
        });
    }

    public static boolean isValidTextView(TextView textView) {
        return !textView.getText().toString().equals("");
    }
}
