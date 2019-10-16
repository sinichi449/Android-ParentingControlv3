package com.sinichi.parentingcontrolv3.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinichi.parentingcontrolv3.R;
import com.sinichi.parentingcontrolv3.common.MainAlt;
import com.sinichi.parentingcontrolv3.interfaces.z;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_overview, container, false);
        MainAlt mainAlt = new MainAlt();

        Button btnLogOut = root.findViewById(R.id.btn_logout);
        mainAlt.signOut(getActivity(), getContext(), btnLogOut);

        ImageView imgHeaderCalendar = root.findViewById(R.id.calendar);
        TextView tvDate = root.findViewById(R.id.tv_header_date);
        TextView tvDateDetails = root.findViewById(R.id.tv_header_date_details);

        Glide.with(this)
                .load(R.drawable.calendar)
                .into(imgHeaderCalendar);

        Calendar calendar = Calendar.getInstance();
        // Get current date
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        tvDate.setText(formatter.format(calendar.getTime()));

        // Get current day of week
        String[] daysName = {"Minggu", "Senin", "Selasa", "Rabu",
                "Kamis", "Jum'at", "Sabtu"};
        String day = daysName[calendar.get(Calendar.DAY_OF_WEEK) - 1];

        // Get current month
        String[] monthName = {"Januari", "Februari",
                "Maret", "April", "Mei", "Juni", "Juli",
                "Agustus", "September", "Oktober", "November",
                "Desember"};

        String month = monthName[calendar.get(Calendar.MONTH)];

        // Get current year
        String year = new SimpleDateFormat("yyyy").format(calendar.getTime());

        // Build combination header details
        String combination = day + ", " + month + " " + year + " ";
        tvDateDetails.setText(combination);


        return root;
    }
}
