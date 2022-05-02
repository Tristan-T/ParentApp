package com.bcttgd.parentapp.Fragments;

import static android.content.ContentValues.TAG;

import android.content.PeriodicSync;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bcttgd.parentapp.R;
import com.bcttgd.parentapp.Workers.UploadFileListWorker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataFragment extends Fragment {
    public DataFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest uploadFileListRequest = new PeriodicWorkRequest.Builder(UploadFileListWorker.class, 15, TimeUnit.MINUTES)
                                                            .setConstraints(constraints)
                                                            .build();

        WorkManager.getInstance(this.getContext()).enqueue(uploadFileListRequest);
    }


}