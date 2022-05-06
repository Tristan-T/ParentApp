package com.bcttgd.parentapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bcttgd.parentapp.CustomNotification;
import com.bcttgd.parentapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.sql.Time;
import java.time.Instant;


public class AlertFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button ooz = view.findViewById(R.id.button);
        Button dd = view.findViewById(R.id.button2);
        Button nlc = view.findViewById(R.id.button3);

        ooz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomNotification.createOutOfZoneNotification("0783162623", "NAME_CHILD", "ZONE_NAME", "now", getActivity(), getContext());
            }
        });
        dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomNotification.createDownloadNotification("Tits_on_a_branch", "NAME_CHILD", getActivity(), getContext());
            }
        });

        nlc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomNotification.createNoLocationChange("0783162623", "NAME_CHILD", "ZONE_NAME", "now", getActivity(), getContext());
            }
        });

    }
}