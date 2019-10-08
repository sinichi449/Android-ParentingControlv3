package com.sinichi.parentingcontrolv3.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.LoginUtils;
import com.sinichi.parentingcontrolv3.util.Constant;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private ImageView imgAnak, imgOrangTua;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedPrefsEdit;
    private LoginUtils b;


    private void initComponents() {
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
        b = new LoginUtils();

        // If user has credential, go to MainActivity
        b.checkUserCredential(this, this);

        // Status bar color
        SetAppearance.setStatusBarColor(this, R.color.colorBlue);

        // Initializing XML Components
        initComponents();

        // Changed image when user clicked
        b.setLoginButtonBehaviour(imgAnak, imgOrangTua, mSignInButton,
                this, sharedPrefsEdit);

        // Create GoogleApiClient
        mFirebaseAuth = FirebaseAuth.getInstance();
        mGoogleApiClient = b.buildGoogleApi(this);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signIntent, Constant.RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent
        b.getRequestCodeAndLogin(this, this, requestCode, data);
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
