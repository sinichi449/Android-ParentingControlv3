package com.sinichi.parentingcontrolv3.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public interface b {

    void checkUserCredential(Activity activity, Context context);

    void setLoginButtonBehaviour(ImageView imgAnak, ImageView imgOrtu,
                                 SignInButton signButton, Context context,
                                 SharedPreferences.Editor sharedPrefsEditor);

    GoogleApiClient buildGoogleApi(Context context);

    void getRequestCodeAndLogin(Context context, Activity activity, int requestCode, Intent data);
}
