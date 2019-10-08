package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.interfaces.b;
import com.sinichi.parentingcontrolv3.util.Constant;

public class B implements b {

    @Override
    public void checkUserCredential(Activity activity, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                sharedPrefsEditor.putString(Constant.USERNAME, Constant.USER_ANAK).apply();
                mSignInButton.setEnabled(true);
                imgAnak.setScaleX(0.8f);
                imgAnak.setScaleY(0.8f);
                imgOrtu.setScaleX(1f);
                imgOrtu.setScaleY(1f);
                imgOrtu.setSelected(false);
            }
        });

        imgOrtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefsEditor.putString(Constant.USERNAME, Constant.USER_ORANG_TUA)
                        .apply();
                mSignInButton.setEnabled(true);
                imgOrtu.setScaleX(0.8f);
                imgOrtu.setScaleY(0.8f);
                imgAnak.setScaleX(1f);
                imgAnak.setScaleY(1f);
                imgAnak.setSelected(false);
            }
        });
    }
}
