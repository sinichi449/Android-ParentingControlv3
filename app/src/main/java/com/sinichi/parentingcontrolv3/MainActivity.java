package com.sinichi.parentingcontrolv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.ModelLoader;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Model, DataViewHolder> mFirebaseAdapter;
    private RecyclerView mRecyclerView;
    private DatabaseReference kegiatanRef;

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView tvTanggal;
        TextView tvHari;
        TextView tvBulan;
        TextView tvTahun;
        TextView tvJumlahSholat;
        CheckBox chkMembantuOrtu;
        CheckBox chkSekolah;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvHari = itemView.findViewById(R.id.tv_hari);
            tvBulan = itemView.findViewById(R.id.tv_bulan);
            tvTahun = itemView.findViewById(R.id.tv_tahun);
            tvJumlahSholat = itemView.findViewById(R.id.tv_jumlahSholat);
            chkMembantuOrtu = itemView.findViewById(R.id.chkbx_membantuOrtu);
            chkSekolah = itemView.findViewById(R.id.chkbx_sekolah);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        SnapshotParser<Model> parser = new SnapshotParser<Model>() {
            @NonNull
            @Override
            public Model parseSnapshot(@NonNull DataSnapshot snapshot) {
                Model model = snapshot.getValue(Model.class);
                if (model != null) {
                    model.setId(snapshot.getKey());
                }
                return model;
            }
        };

        kegiatanRef = mDatabaseReference.child(mFirebaseUser.getUid())
                .child("data_kegiatan");

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(kegiatanRef,parser).build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Model, DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int i, @NonNull Model model) {
                dataViewHolder.tvTanggal.setText(model.getTanggal());
                dataViewHolder.tvHari.setText(model.getHari());
                dataViewHolder.tvBulan.setText(model.getBulan());
                dataViewHolder.tvTahun.setText(model.getTahun());
                dataViewHolder.tvJumlahSholat.setText(model.getJumlahSholat());
                dataViewHolder.chkMembantuOrtu.setChecked(model.isMembantuOrangTua());
                dataViewHolder.chkSekolah.setChecked(model.isSekolah());
                dataViewHolder.chkMembantuOrtu.setEnabled(false);
                dataViewHolder.chkSekolah.setEnabled(false);
            }

            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater1 = LayoutInflater.from(parent.getContext());
                return new DataViewHolder(inflater1.inflate(
                        R.layout.layout_item, parent, false));
            }
        };

        mRecyclerView.setAdapter(mFirebaseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,
                false));
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mFirebaseUser == null) {
            startActivity(new Intent(MainActivity.this,
                    LoginActivity.class));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Service Error",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAdapter.stopListening();
    }
}
