package com.sinichi.parentingcontrolv3.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class DialogDataDiri extends LoginActivity {
    private Context context;
    private LayoutInflater layoutInflater;
    private LoginActivity loginActivity;
    private GoogleApiClient mGoogleApiClient;
    private String nama, alamat, sekolahPekerjaan, nomorTelpon, gender,
            tanggalLahir;
    private SharedPreferences sharedPrefs;
    private View dialogView;
    private TextView tvNama, tvAlamat, tvNomorTelepon, edtSekolahPekerjaan, tvSekolahPekerjaan;
    private Button btnSubmit, btnAdminMode;
    private Spinner spinner, spinnerTanggal, spinnerBulan, spinnerTahun;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    public ProgressDialog progressDialog;
    private EditText edtTanggalLahir;
    private RadioGroup rgJenisKelamin;

    public DialogDataDiri(Context context) {
        this.context = context;
    }

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
        builder.setView(dialogView);
        initComponent();
        onDialogPositiveButton();
//        onDialogNegativeButton();
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void initComponent() {
        ImageView imgDataDiri = dialogView.findViewById(R.id.head);
        Glide.with(dialogView)
                .load(R.drawable.bg2)
                .into(imgDataDiri);
        tvNama = dialogView.findViewById(R.id.txt_name);

        tvSekolahPekerjaan = dialogView.findViewById(R.id.tv_sekolah_pekerjaan);
        edtSekolahPekerjaan = dialogView.findViewById(R.id.edt_sekolah_pekerjaan);
        String username = loginActivity.username;
        String anak = Constant.USER_ANAK;
        String orangTua = Constant.USER_ORANG_TUA;
        if (!username.equals("") && username.equals(anak)) {
            tvSekolahPekerjaan.setText("Asal sekolah");
            edtSekolahPekerjaan.setHint("Masukkan sekolah");
        } else if (!username.equals("") && username.equals(orangTua)) {
            tvSekolahPekerjaan.setText("Pekerjaan");
            edtSekolahPekerjaan.setHint("Masukkan pekerjaan");
        } else {
            Log.e("TextViewSekolah", username);
        }

        tvAlamat = dialogView.findViewById(R.id.txt_alamat);
        tvNomorTelepon = dialogView.findViewById(R.id.txt_no_telp);
        rgJenisKelamin = dialogView.findViewById(R.id.radiogroup_Gender);
        btnSubmit = dialogView.findViewById(R.id.btn_submit);
        edtTanggalLahir = dialogView.findViewById(R.id.edt_tanggal_lahir);
        edtTanggalLahir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 01-01-1990
                if (s.length() == 2) {
                    edtTanggalLahir.append("-");
                } else if (s.length() == 5) {
                    edtTanggalLahir.append("-");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        btnAdminMode = dialogView.findViewById(R.id.btn_admin_mode);

    }

    private void onDialogPositiveButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nama = tvNama.getText().toString();
                int idRadioButtonJenisKelamin = rgJenisKelamin.getCheckedRadioButtonId();
                RadioButton rbJenisKelamin = dialogView.findViewById(idRadioButtonJenisKelamin);
                gender = rbJenisKelamin.getText().toString();
                alamat = tvAlamat.getText().toString();
                sekolahPekerjaan = edtSekolahPekerjaan.getText().toString();
                nomorTelpon = tvNomorTelepon.getText().toString();
                tanggalLahir = edtTanggalLahir.getText().toString();
                // TODO: Check user input
                if (isValidTextView(tvNama) && isValidTextView(tvAlamat) && isValidTextView(tvNomorTelepon)
                        && isValidTextView(tvNomorTelepon) && isValidTextView(edtSekolahPekerjaan) && isValidTextView(edtTanggalLahir)
                        && rgJenisKelamin.getCheckedRadioButtonId() != -1) {
                    putDataToSharedPrefs();
//                    progressDialog = new ProgressDialog(context);
//                    progressDialog.setMessage("Connecting to Cloud...");
//                    progressDialog.show();
                    Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    loginActivity.startActivityForResult(signIntent, Constant.RC_SIGN_IN);
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, "Mohon isi kolom yang kosong", Toast.LENGTH_LONG)
                            .show();
                }
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
