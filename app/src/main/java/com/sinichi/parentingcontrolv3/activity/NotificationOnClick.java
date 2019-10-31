package com.sinichi.parentingcontrolv3.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sinichi.parentingcontrolv3.R;

public class NotificationOnClick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_on_click);

        String waktuSholat = getIntent().getStringExtra("notif_sholat");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lorem ipsum")
                .setMessage("Apakah Anda sudah melakukan sholat " + waktuSholat + " ?\n\n WARNING: Apabila Anda klik cancel, maka data Anda tidak akan ditulis lagi dan dianggap tidak melakukan sholat tertentu!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Change the database
                        Intent intent = new Intent(NotificationOnClick.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(NotificationOnClick.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                        finish();
                    }
                }).setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
