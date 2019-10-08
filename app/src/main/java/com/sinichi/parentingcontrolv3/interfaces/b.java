package com.sinichi.parentingcontrolv3.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public interface b {

    public void checkUserCredential(Activity activity, Context context);

    public void setLoginButtonBehaviour(ImageView imgAnak, ImageView imgOrtu,
                                        SignInButton signButton, Context context,
                                        SharedPreferences.Editor sharedPrefsEditor);

    public GoogleApiClient buildGoogleApi(Context context);
}
