package com.bcttgd.parentapp.Workers;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;

public class UploadFileListWorker extends Worker {
    public UploadFileListWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        getData();

        // Indicate whether the work finished successfully with the Result
        return Result.success();
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
