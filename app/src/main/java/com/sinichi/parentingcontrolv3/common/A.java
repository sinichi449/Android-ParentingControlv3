package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinichi.parentingcontrolv3.util.Constant;

public class A implements com.sinichi.parentingcontrolv3.interfaces.A {

    @Override
    public FirebaseUser getUser() {
        return null;
    }

    @Override
    public void buildGoogleApi(final Context context, SignInButton signInButton, final Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("620226023898-da2auc1aqqd8q0ipbtiq4bamel0ugj7l.apps.googleusercontent.com")
                .requestEmail()
                .build();
        final GoogleApiClient client = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(client);
                activity.startActivityForResult(signIntent, Constant.RC_SIGN_IN);
            }
        });
    }

    @Override
    public void initilizeFirebaseAuth() {

    }

    @Override
    public void firebaseAuthWithGoogle() {

    }
}
