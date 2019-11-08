package com.sinichi.parentingcontrolv3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView tvNama, tvTipeAkun, tvAlamat,
            tvSekolah, tvNomorTelepon, tvTanggalLahir, tv2;
    private ImageView imgHeaderProfile;
    private CircleImageView circleImageView;
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
        circleImageView = findViewById(R.id.circle_image);
        tvNama = findViewById(R.id.tv_nama);
        tvTipeAkun = findViewById(R.id.tv_anak_ortu);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvSekolah = findViewById(R.id.tv_sekolah);
        tvNomorTelepon = findViewById(R.id.tv_nomor_telepon);
        tvTanggalLahir = findViewById(R.id.tv_tanggal_lahir);
        tv2 = findViewById(R.id.tv2);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        imgSignOut = findViewById(R.id.btn_signout);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SetAppearance.setExtendStatusBarWithView(this);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2f9cd7")));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SetAppearance.hideNavigationBar(this);
        initComponents();
        SetAppearance.onBottomNavigationClick(this, this, mBottomNavigation, R.id.menu_profile);
        mBottomNavigation.setSelectedItemId(R.id.menu_profile);

        final MainAlt mainAlt = new MainAlt();
        String nama = sharedPreferences.getString(Constant.DATA_NAMA, null);
        String tipeAkun = sharedPreferences.getString(Constant.USERNAME, null);
        String alamat = sharedPreferences.getString(Constant.DATA_ALAMAT, null);
        String sekolah = sharedPreferences.getString(Constant.DATA_SEKOLAH_PEKERJAAN, "SMK Negeri 1 Turen");
        String nomorTelepon = sharedPreferences.getString(Constant.DATA_NOMOR_TELEPON, null);
        String tanggalLahir = sharedPreferences.getString(Constant.DATA_TANGGAL_LAHIR, null);
        try {
            if (nama.equals("") && tipeAkun.equals("") && alamat.equals("")
                    && sekolah.equals("") && nomorTelepon.equals("") && tanggalLahir.equals("")) {
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                setProfileDefaultImage();
                tvNama.setText(nama);
                tvTipeAkun.setText(tipeAkun);
                tvAlamat.setText(alamat);

                String user = sharedPreferences.getString(Constant.USERNAME, null);
                Log.e("Username", user);
                String anak = Constant.USER_ANAK;
                String orangTua = Constant.USER_ORANG_TUA;
                if (!user.equals("") && user.equals(anak)) {
                    tv2.setText("Sekolah");
                } else if (!user.equals("") && user.equals(orangTua)) {
                    tv2.setText("Pekerjaan");
                }

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
        setSekolahOrPekerjaanIcon();
    }

    public static boolean isValidTextView(TextView textView) {
        return !textView.getText().toString().equals("");
    }

    private void setProfileDefaultImage() {
        // TODO: Female male
        if (sharedPreferences.getString(Constant.GENDER, null)
                .equals(Constant.LAKI_LAKI)) {
            circleImageView.setImageResource(R.drawable.male);
        } else if (sharedPreferences.getString(Constant.GENDER, null)
                .equals(Constant.PEREMPUAN)) {
            circleImageView.setImageResource(R.drawable.female);
        }
    }

    private void setSekolahOrPekerjaanIcon() {
        String username = sharedPreferences.getString(Constant.USERNAME, null);
        String userOrangTua = Constant.USER_ORANG_TUA;
        if (username.equals(userOrangTua)) {
            ImageView imgSekolahOrPekerjaan = findViewById(R.id.img_sekolah);
            imgSekolahOrPekerjaan.setImageResource(R.drawable.kerja);
        }
    }
}
