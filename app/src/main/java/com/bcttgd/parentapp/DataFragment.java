package com.bcttgd.parentapp;

import static android.content.ContentValues.TAG;

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

    private void sendData(String folderName, File[] files) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        //Check that folderName does not contain ".", "#", "$", "[", "]" with regex
        if (folderName.matches("[^.#$\\[\\]]+")) {
            Log.d(TAG, "sendData: NOT FOUND INVALID CHARACTER : " + folderName);
            DatabaseReference myRef = database.getReference(folderName);

            ArrayList<String> fileNames = new ArrayList<>();
            for(File file : files) {
                fileNames.add(file.getName());
            }
            myRef.setValue(fileNames);
        }
    }

    private void getData() {
        getData(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private void getData(String folderName) {
        File home = new File(folderName);
        Log.d("DataFragment", "Home: " + home.getAbsolutePath());
        File[] files = home.listFiles();
        //Check that there are files in the folder
        if (files != null) {
            sendData(folderName, files);
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.d("DataFragment", "Directory: " + file.getName());
                    getData(folderName + "/" + file.getName());
                } else {
                    Log.d("DataFragment", "File: " + file.getName());
                }
            }
        }
    }
}