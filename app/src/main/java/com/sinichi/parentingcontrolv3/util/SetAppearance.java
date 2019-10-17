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
import com.sinichi.parentingcontrolv3.activity.ProfileActivity;
import com.sinichi.parentingcontrolv3.fragment.OverviewFragment;

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

    public static void onBottomNavigationClick(final Context context, final Activity activity, final BottomNavigationView btmNavView, int menuItemId) {
        btmNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_map) {
                    Intent i = new Intent(context, MapsActivity.class);
                    if (context instanceof MapsActivity) {

                    } else {
                        context.startActivity(i);
                        activity.finish();
                    }
                } else if (id == R.id.menu_chat) {
                    Intent i = new Intent(context, ChatActivity.class);
                    if (context instanceof ChatActivity) {

                    } else {
                        context.startActivity(i);
                        activity.finish();
                    }
                } else if (id == R.id.menu_profile) {
                    Intent i = new Intent(context, ProfileActivity.class);
                    if (context instanceof ProfileActivity) {

                    } else {
                        context.startActivity(i);
                        activity.finish();
                    }
                } else if (id == R.id.menu_overview) {
                    Intent i = new Intent(context, MainActivity.class);
                    if (context instanceof MainActivity) {

                    } else {
                        context.startActivity(i);
                        activity.finish();
                    }
                }
                return true;
            }
        });
        btmNavView.setSelectedItemId(menuItemId);
    }

    public static void setExtendStatusBarWithView(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
