package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.LoginAlt;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import static com.sinichi.parentingcontrolv3.activity.ProfileActivity.isValidTextView;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private ImageView imgAnak, imgOrangTua;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEdit;
    private LoginAlt loginAlt;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public void initComponents() {
        mSignInButton = findViewById(R.id.btn_google);
        mSignInButton.setEnabled(false);
        imgAnak = findViewById(R.id.btn_saya_anak);
        imgOrangTua = findViewById(R.id.btn_saya_orangtua);
        sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPrefsEdit = sharedPrefs.edit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SetAppearance.hideNavigationBar(this);
        loginAlt = new LoginAlt();

        // If user has credential, go to MainActivity
        loginAlt.checkUserCredential(this, this);

        // Status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setStatusBarColor(this, R.color.colorBlue);
        }

        // Initializing XML Components
        initComponents();

        // Changed image when user clicked
        loginAlt.setLoginButtonBehaviour(imgAnak, imgOrangTua, mSignInButton,
                this, sharedPrefsEdit);

        // Create GoogleApiClient
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mGoogleApiClient = loginAlt.buildGoogleApi(this);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                if (imgAnak.isSelected()) {
                    editor.putString(Constant.USERNAME, Constant.USER_ANAK).apply();
                } else if (imgOrangTua.isSelected()) {
                    editor.putString(Constant.USERNAME, Constant.USER_ORANG_TUA).apply();
                } else {
                    editor.putString(Constant.USERNAME, "Anonymous").apply();
                }
                // TODO: Launch custom alertdialog
                launchAlerDialog();
            }
        });

    }

    private void launchAlerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.temp_layout_isi_data_diri, null);
        ImageView imgDataDiri = dialogView.findViewById(R.id.head);
        Glide.with(dialogView)
                .load(R.drawable.datadiri)
                .into(imgDataDiri);
        builder.setView(dialogView)
                .setCancelable(false)
                .setTitle("Isi data diri");
        final TextView tvNama = dialogView.findViewById(R.id.txt_name);
        final TextView tvAlamat = dialogView.findViewById(R.id.txt_alamat);
        final TextView tvNomorTelepon = dialogView.findViewById(R.id.txt_no_telp);
        final TextView tvTanggalLahir = dialogView.findViewById(R.id.txt_tanggal_lahir);

        final Spinner spinner = dialogView.findViewById(R.id.spinnner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jenis_kelamin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nama = tvNama.getText().toString();
                String alamat = tvAlamat.getText().toString();
                String nomorTelpon = tvNomorTelepon.getText().toString();
                String tanggalLahir = tvTanggalLahir.getText().toString();
                String gender = spinner.getSelectedItem().toString();
                Log.e("Spinner", gender);
                editor.putString(Constant.GENDER, gender);
                // TODO: Check user input
                if (isValidTextView(tvNama) && isValidTextView(tvAlamat) && isValidTextView(tvNomorTelepon)
                        && isValidTextView(tvNomorTelepon) && isValidTextView(tvTanggalLahir)) {
                    editor.putString(Constant.DATA_NAMA, nama);
                    editor.putString(Constant.DATA_ALAMAT, alamat);
                    editor.putString(Constant.DATA_NOMOR_TELEPON, nomorTelpon);
                    editor.putString(Constant.DATA_TANGGAL_LAHIR, tanggalLahir);
                    editor.apply();
                    Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signIntent, Constant.RC_SIGN_IN);
                } else {
                    Toast.makeText(LoginActivity.this, "Mohon isi kolom yang kosong", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        builder.setNegativeButton("Admin Mode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient),
                        Constant.RC_SIGN_IN);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent
        loginAlt.getRequestCodeAndLogin(this, this, requestCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
