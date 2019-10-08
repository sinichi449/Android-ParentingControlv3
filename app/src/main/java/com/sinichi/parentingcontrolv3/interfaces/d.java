package com.sinichi.parentingcontrolv3.interfaces;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

import androidx.recyclerview.widget.RecyclerView;

public interface d {

    void parseSnapShot();

    void recyclerViewAdapterBuilder(Context context, DatabaseReference kegiatanRef, RecyclerView localRecyclerView);

    void signOut(Activity activity, Context context, Button btnLogOut);

    void retrieveData(boolean setActive);
}
