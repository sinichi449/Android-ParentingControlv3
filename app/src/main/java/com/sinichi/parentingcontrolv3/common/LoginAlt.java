package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.LoginActivity;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.interfaces.b;
import com.sinichi.parentingcontrolv3.util.Constant;

import androidx.annotation.NonNull;

public class LoginAlt extends LoginActivity implements b{

    @Override
    public void checkUserCredential(Activity activity, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.finish();
        }
    }


    @Override
    public void setLoginButtonBehaviour(final ImageView imgAnak, final ImageView imgOrtu,
                                        final SignInButton mSignInButton, final Context context,
                                        final SharedPreferences.Editor sharedPrefsEditor) {
        imgAnak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sharedPrefsEditor.putString(Constant.USERNAME, Constant.USER_ANAK).apply();
                mSignInButton.setEnabled(true);
                imgAnak.setImageResource(R.drawable.btn_anakblue);
                imgOrtu.setImageResource(R.drawable.btn_orangtua);
                imgAnak.setSelected(true);
                imgOrtu.setSelected(false);
            }
        });

        imgOrtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sharedPrefsEditor.putString(Constant.USERNAME, Constant.USER_ORANG_TUA).apply();
                mSignInButton.setEnabled(true);
                imgOrtu.setImageResource(R.drawable.btn_orangtuablue);
                imgAnak.setImageResource(R.drawable.btn_anak);
                imgAnak.setSelected(false);
                imgOrtu.setSelected(true);
            }
        });
    }

    @Override
    public GoogleApiClient buildGoogleApi(final Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("620226023898-da2auc1aqqd8q0ipbtiq4bamel0ugj7l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        return new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void getRequestCodeAndLogin(Context context, Activity activity, int requestCode, Intent data) {
        if (requestCode == Constant.RC_SIGN_IN) { // 9001 == 9001 true
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                assert account != null;
                firebaseAuthWithGoogle(context, this, account);
            } else {
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final Context context, final Activity activity, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            context.startActivity(new Intent(context, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            activity.finish();
                        }
                    }
                });
    }

    @Override
    public void setBottomNavigationAction(Context context, BottomNavigationView mBottomNav) {

    }
}
