package com.sinichi.parentingcontrolv3.common;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sinichi.parentingcontrolv3.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class B {

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTanggal;
        public TextView tvHari;
        public TextView tvBulan;
        public TextView tvTahun;
        public TextView tvJumlahSholat;
        public CheckBox chkMembantuOrtu;
        public CheckBox chkSekolah;

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
}
