package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.interfaces.b;

public class B implements b {

    @Override
    public void checkUserCredential(Activity activity, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            activity.startActivity(new Intent(
                    context, MainActivity.class));
        }
    }
}
