package com.bcttgd.parentapp;

import static android.content.ContentValues.TAG;

import android.content.PeriodicSync;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        getData();

    }

    private void getData() {
        //For all external storage that are accessible
        getData("/storage/");
        //For internal storage
        getData(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void getData(String folderName) {
        //Check if folderName contains '.', '#', '$', '[', or ']'
        if (folderName.contains(".") || folderName.contains("#") || folderName.contains("$") || folderName.contains("[") || folderName.contains("]")) {
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference(folderName);

        File home = new File(folderName);

        File[] files = home.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if(files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    fileNames.add(file.getName());
                }
            }
            myRef.setValue(fileNames);
            for(File file : files) {
                if(file.isDirectory()) {
                    getData(file.getAbsolutePath());
                }
            }
        }
    }
}