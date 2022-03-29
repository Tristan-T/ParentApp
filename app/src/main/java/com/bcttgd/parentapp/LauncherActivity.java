package com.bcttgd.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("com.bcttgd.parentapp", Context.MODE_PRIVATE);
        //Check if the user was logged in
        if(sharedPreferences.getBoolean("loggedIn", false)){
            //If the user was logged in, go to the main activity

        } else {
            //If the user was not logged in, go to the signup activity
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        finish();
    }
}