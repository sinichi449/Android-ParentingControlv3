package com.sinichi.parentingcontrolv3.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.WindowManager;

import com.sinichi.parentingcontrolv3.interfaces.splash;

public class MakeSplash implements splash {

    @Override
    public void setToFullScreenActivity(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void makeSplash(final Activity activity, final Context first, final Class<?> next) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(first, next);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
                activity.finish();
            }
        }, 3000);
    }
}
