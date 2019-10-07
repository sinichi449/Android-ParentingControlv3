package com.sinichi.parentingcontrolv3.interfaces;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;

public interface A {

    public FirebaseUser getUser();
    public void buildGoogleApi(Context context, SignInButton signInButton,
                               Activity activity);
    public void initilizeFirebaseAuth();
    public void firebaseAuthWithGoogle();
}
