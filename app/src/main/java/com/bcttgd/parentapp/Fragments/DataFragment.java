package com.bcttgd.parentapp.Fragments;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bcttgd.parentapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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

        //Check if device has been selected
        if(getArguments() != null) {
            String device = getArguments().getString("device");
            if(device != null) {
                displayData(device, "");
            } else {
                Toast.makeText(getContext(), "No device selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display data from a device
     * @param deviceID
     */
    public void displayData(String deviceID, String path) {
        //Get Database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        String reference = "users/" + mAuth.getCurrentUser().getUid() + "/devicesData/" + deviceID + "/storage/" + path;
        Log.d("DataFragment", reference);
        DatabaseReference myRef = database.getReference(reference);

        //Get ListView
        ListView listView = getView().findViewById(R.id.listData);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Set data
                ArrayList<String> data = new ArrayList<>();
                data.add("..");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //If it has children, it's a folder, else it's a file
                    if(snapshot.hasChildren()) {
                        data.add(snapshot.getKey());
                    } else {
                        data.add(snapshot.getValue(String.class));
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, data);
                listView.setAdapter(adapter);

                //Set onClickListener
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    String item = (String) parent.getItemAtPosition(position);
                    Log.d("DataFragment", "Item clicked: " + item);
                    if(item.equals("..")) {
                        //Go back
                        String[] parts = path.split("/");
                        String newPath = "";
                        for(int i = 0; i < parts.length - 1; i++) {
                            newPath += parts[i] + "/";
                        }
                        displayData(deviceID, newPath);
                    }
                    //Check that item does not contain '.', '#', '$', '[', or ']'
                    if(!item.contains(".") && !item.contains("#") && !item.contains("$") && !item.contains("[") && !item.contains("]")) {
                        //Check if item has children in database
                        if(dataSnapshot.child(item).hasChildren()) {
                            Log.d("DataFragment", "Folder: " + item);
                            displayData(deviceID, path + "/" + item);
                        } else {
                            Toast.makeText(getContext(), "This is a file", Toast.LENGTH_SHORT).show();
                            fetchFile(deviceID, path + "/" + item);
                        }
                    } else {
                        Toast.makeText(getContext(), "This is a file", Toast.LENGTH_SHORT).show();
                        fetchFile(deviceID, path + "/" + item);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchFile(String deviceID, String path) {
        Log.d("DataFragment", "Fetching file: " + path);
        //Get Database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        String reference = "users/" + mAuth.getCurrentUser().getUid() + "/devicesData/" + deviceID + "/storageMessage";
        Log.d("DataFragment", reference);
        DatabaseReference myRef = database.getReference(reference);
        myRef.setValue(path);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                Log.d("DataFragment", "Message: " + message);
                if(message != null) {
                    if(message.equals("SUCCESS")) {
                        //Get from Firebase Storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        String filePath = deviceID + path;
                        StorageReference gsReference = storage.getReferenceFromUrl("gs://devmobilem1.appspot.com/" + filePath);

                        //Create file
                        //Get the extension of path
                        String[] parts = path.split("/");
                        String extension = "";
                        for (int i = 0; i < parts.length; i++) {
                            if (parts[i].contains(".")) {
                                extension = parts[i].substring(parts[i].lastIndexOf("."));
                            }
                        }
                        //Get the name of file
                        String fileName = parts[parts.length - 1];

                        //Download file in download folder
                        final File rootPath = new File(Environment.getExternalStorageDirectory(), "Download");

                        if (!rootPath.exists()) {
                            rootPath.mkdirs();
                        }

                        final File localFile = new File(rootPath, fileName);

                        gsReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            // File downloaded successfully
                            Log.d("DataFragment", "File downloaded successfully");
                            Toast.makeText(getContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();
                            //Prompt user to select app for unknown file type
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(localFile), "*/*");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }).addOnFailureListener(exception -> {
                            // Handle failures
                            Log.d("DataFragment", "Failed to download file");
                            Toast.makeText(getContext(), "Failed to download file", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to download file", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}