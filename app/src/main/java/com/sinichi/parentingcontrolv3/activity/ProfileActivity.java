package com.sinichi.parentingcontrolv3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.TentangActivity;
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
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SetAppearance.setExtendStatusBarWithView(this);
        setContentView(R.layout.activity_profile);
        setActionBarView();
        SetAppearance.hideNavigationBar(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setStatusBarColor(this, R.color.HeaderProfileBlue);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_menu_profile, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    private void setActionBarView() {
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_tentang_kami:
                        Intent intent = new Intent(ProfileActivity.this, TentangActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_log_out:
                        Intent intent1 = new Intent(ProfileActivity.this, LoginActivity.class);
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent1);
                        Toast.makeText(ProfileActivity.this, "Log out berhasil",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}
