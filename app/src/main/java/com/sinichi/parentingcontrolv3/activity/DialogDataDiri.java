package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.util.Constant;

import static com.sinichi.parentingcontrolv3.activity.ProfileActivity.isValidTextView;

class DialogDataDiri {
    private Context context;
    private LayoutInflater layoutInflater;
    private LoginActivity loginActivity;
    private GoogleApiClient mGoogleApiClient;
    private String nama, alamat, sekolahPekerjaan, nomorTelpon, gender,
            tanggalLahir;
    private SharedPreferences sharedPrefs;
    private View dialogView;
    private TextView tvNama, tvAlamat, tvNomorTelepon, tvTanggalLahir,
            tvSekolahPekerjaan;
    private Spinner spinner;
    private AlertDialog.Builder builder;

    DialogDataDiri(Context context, LayoutInflater layoutInflater, LoginActivity loginActivity, GoogleApiClient mGoogleApiClient) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.loginActivity = loginActivity;
        this.mGoogleApiClient = mGoogleApiClient;

        sharedPrefs = context.getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    // LAUNCH DIALOG
    void launch() {
        builder = new AlertDialog.Builder(context);
        dialogView = layoutInflater.inflate(R.layout.temp_layout_isi_data_diri, null);
        builder.setView(dialogView)
                .setCancelable(false)
                .setTitle("Isi data diri");
        initComponents();
        onDialogPositiveButton();
        onDialogNegativeButton();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initComponents() {
        spinner = dialogView.findViewById(R.id.spinnner);
        ImageView imgDataDiri = dialogView.findViewById(R.id.head);
        Glide.with(dialogView)
                .load(R.drawable.datadiri)
                .into(imgDataDiri);
        tvNama = dialogView.findViewById(R.id.txt_name);

        tvSekolahPekerjaan = dialogView.findViewById(R.id.edt_sekolah_pekerjaan);
        String username = loginActivity.username;
        String anak = Constant.USER_ANAK;
        String orangTua = Constant.USER_ORANG_TUA;
        if (!username.equals("") && username.equals(anak)) {
            tvSekolahPekerjaan.setHint("Masukkan sekolah");
        } else if (!username.equals("") && username.equals(orangTua)) {
            tvSekolahPekerjaan.setHint("Masukkan pekerjaan");
        } else {
            Log.e("TextViewSekolah", username);
        }

        tvAlamat = dialogView.findViewById(R.id.txt_alamat);
        tvNomorTelepon = dialogView.findViewById(R.id.txt_no_telp);
        tvTanggalLahir = dialogView.findViewById(R.id.txt_tanggal_lahir);

        // Spinner string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.jenis_kelamin, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void onDialogPositiveButton() {
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nama = tvNama.getText().toString();
                alamat = tvAlamat.getText().toString();
                sekolahPekerjaan = tvSekolahPekerjaan.getText().toString();
                nomorTelpon = tvNomorTelepon.getText().toString();
                tanggalLahir = tvTanggalLahir.getText().toString();
                gender = spinner.getSelectedItem().toString();
                Log.e("Spinner", gender);
                // TODO: Check user input
                if (isValidTextView(tvNama) && isValidTextView(tvAlamat) && isValidTextView(tvNomorTelepon)
                        && isValidTextView(tvNomorTelepon) && isValidTextView(tvTanggalLahir) && isValidTextView(tvSekolahPekerjaan)) {
                    putDataToSharedPrefs();
                    Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    loginActivity.startActivityForResult(signIntent, Constant.RC_SIGN_IN);
                } else {
                    Toast.makeText(context, "Mohon isi kolom yang kosong", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void onDialogNegativeButton() {
        builder.setNegativeButton("Admin Mode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loginActivity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient),
                        Constant.RC_SIGN_IN);
            }
        });
    }

    private void putDataToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constant.DATA_NAMA, nama);
        editor.putString(Constant.GENDER, gender);
        editor.putString(Constant.DATA_ALAMAT, alamat);
        editor.putString(Constant.DATA_SEKOLAH_PEKERJAAN, sekolahPekerjaan);
        editor.putString(Constant.DATA_NOMOR_TELEPON, nomorTelpon);
        editor.putString(Constant.DATA_TANGGAL_LAHIR, tanggalLahir);
        editor.apply();
    }
}
