package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.LoginAlt;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private ImageView imgAnak;
    private ImageView imgOrangTua;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEdit;
    private LoginAlt loginAlt;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String username = "Anonymous";

    public void initComponents() {
        mSignInButton = findViewById(R.id.btn_google);
        mSignInButton.setEnabled(false);
        imgAnak = findViewById(R.id.btn_saya_anak);
        imgOrangTua = findViewById(R.id.btn_saya_orangtua);
        ImageView imgHeader = findViewById(R.id.header2);
        Glide.with(this)
                .load(R.drawable.header2)
                .into(imgHeader);
        ImageView imgFooter = findViewById(R.id.footer2);
        Glide.with(this)
                .load(R.drawable.footer2)
                .into(imgFooter);
        sharedPrefs = getSharedPreferences(Constant.SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPrefsEdit = sharedPrefs.edit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SetAppearance.setExtendStatusBarWithView(this);
        SetAppearance.hideNavigationBar(this);
        loginAlt = new LoginAlt();

        // If user has credential, go to MainActivity
        loginAlt.checkUserCredential(this, this);

//        // Status bar color
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            SetAppearance.setStatusBarColor(this, R.color.colorBlue);
//        }

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
                    username = Constant.USER_ANAK;
                } else if (imgOrangTua.isSelected()) {
                    editor.putString(Constant.USERNAME, Constant.USER_ORANG_TUA).apply();
                    username = Constant.USER_ORANG_TUA;
                } else {
                    editor.putString(Constant.USERNAME, "Anonymous").apply();
                }
                DialogDataDiri dialogDataDiri = new DialogDataDiri(LoginActivity.this, getLayoutInflater(), LoginActivity.this, mGoogleApiClient);
                dialogDataDiri.launch();
            }
        });

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
