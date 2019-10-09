package com.sinichi.parentingcontrolv3.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.activity.ChatActivity;
import com.sinichi.parentingcontrolv3.activity.MainActivity;
import com.sinichi.parentingcontrolv3.activity.MapsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class SetAppearance {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity,color));
    }

    public static void onBottomNavigationClick(final Context context, final BottomNavigationView btmNavView) {
        btmNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_overview:
                        if (context.getClass().getSimpleName().equals("MainActivity")) {
                            break;
                        } else {
                            context.startActivity(new Intent(context, MainActivity.class));
                        }
                    case R.id.menu_map:
                        if (context.getClass().getSimpleName().equals("MapsActivity")) {
                            break;
                        } else {
                            context.startActivity(new Intent(context, MapsActivity.class));
                        }
                    case R.id.menu_chat:
                        if (context.getClass().getSimpleName().equals("ChatActivity")) {
                            break;
                        } else {
                            context.startActivity(new Intent(context, ChatActivity.class));
                        }
                }
                return true;
            }
        });
    }
}
