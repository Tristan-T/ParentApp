package com.bcttgd.parentapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        TextInputLayout mail = findViewById(R.id.email_signup_layout);
        mail.getEditText().addTextChangedListener(new EmailWatcher(mail));
    }

    public void onLoginClick(View view) {
        //Launch LoginActivity
        //Get mail and password
        TextInputLayout mail = findViewById(R.id.email_signup_layout);
        TextInputLayout password = findViewById(R.id.password_signup_layout);

        if(TextUtils.isEmpty(mail.getEditText().getText().toString())){
            mail.setError(getString(R.string.error_email));
            return;
        }

        if(TextUtils.isEmpty(password.getEditText().getText().toString())){
            password.setError(getString(R.string.empty_password));
            return;
        }

        try {
            if(User.authenticate(mail.getEditText().getText().toString(), password.getEditText().getText().toString())) {
                //Set user as logged in
                SharedPreferences sharedPreferences = getSharedPreferences("com.bcttgd.parentapp", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", true);
                editor.apply();

                //Launch MainActivity
                //TODO : Launch MainActivity
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //TODO : Proper error handling
            e.printStackTrace();
        }

    }
}

