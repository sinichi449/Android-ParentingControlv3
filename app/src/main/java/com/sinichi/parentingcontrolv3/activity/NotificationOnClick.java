package com.sinichi.parentingcontrolv3.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.model.DataModel;
import com.sinichi.parentingcontrolv3.util.Constant;

public class NotificationOnClick extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private DatabaseReference kegiatanRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String waktuSholat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_on_click);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid()).child("data_kegiatan");

        waktuSholat = getIntent().getStringExtra("notif_sholat");
        Log.e("Waktu", waktuSholat);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Perbarui data sholat")
                .setMessage("Apakah kamu sudah melakukan sholat " + waktuSholat + " ?\n\n WARNING: Apabila kamu klik Tidak, maka tidak akan bisa diperbarui lagi dan dianggap tidak melakukan sholat tertentu.")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Change the database
                        // Get latest data
                        kegiatanRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DataModel model = new DataModel();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    model = snapshot.getValue(DataModel.class);
                                    if (model != null) {
                                        model.setId(snapshot.getKey());
                                    }
                                }
                                switch (waktuSholat.replace(" ", "")) { // Remove whitespace
                                    case "Subuh":
                                        kegiatanRef.child(model.getId()).child("sholatSubuh").setValue(true);
                                        break;
                                    case "Dhuhr":
                                        kegiatanRef.child(model.getId()).child("sholatDhuhr").setValue(true);
                                        break;
                                    case "Ashar":
                                        kegiatanRef.child(model.getId()).child("sholatAshar").setValue(true);
                                        break;
                                    case "Maghrib":
                                        kegiatanRef.child(model.getId()).child("sholatMaghrib").setValue(true);
                                        break;
                                    case "Isya":
                                        kegiatanRef.child(model.getId()).child("sholatIsya").setValue(true);
                                        break;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(NotificationOnClick.this, "Error occured, hubungi developer untuk mendapatkan bantuan."
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(NotificationOnClick.this, MainActivity.class);
                        intent.putExtra("after_notification", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(NotificationOnClick.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        switch (waktuSholat.replace(" ", "")) { // Remove whitespace
                            case "Subuh":
                                editor.putBoolean(Constant.TIDAK_SHOLAT_SUBUH, true);
                                break;
                            case "Dhuhr":
                                editor.putBoolean(Constant.TIDAK_SHOLAT_DHUHR, true);
                                break;
                            case "Ashar":
                                editor.putBoolean(Constant.TIDAK_SHOLAT_ASHAR, true);
                                break;
                            case "Maghrib":
                                editor.putBoolean(Constant.TIDAK_SHOLAT_MAGHRIB, true);
                                break;
                            case "Isya":
                                editor.putBoolean(Constant.TIDAK_SHOLAT_ISYA, true);
                                break;
                        }
                        editor.apply();

                        finish();
                    }
                }).setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
