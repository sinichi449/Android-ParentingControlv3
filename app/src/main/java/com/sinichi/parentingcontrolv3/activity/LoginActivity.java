package com.sinichi.parentingcontrolv3.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.util.SetAppearance;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SetAppearance.setStatusBarColor(this, R.color.statusbar);
        }

        // TODO: findviewbyid(R.id.btn_login)
        mSignInButton = findViewById(R.id.btn_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("620226023898-da2auc1aqqd8q0ipbtiq4bamel0ugj7l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                assert account != null;
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


}
