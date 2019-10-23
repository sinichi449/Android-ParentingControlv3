package com.sinichi.parentingcontrolv3.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.sinichi.parentingcontrolv3.R;


/**
 * ChatViewHolder simple {@link Fragment} subclass.
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

        TextView tvLoremIpsum = root.findViewById(R.id.tv_lorem_ipsum);

        return root;
    }
}
