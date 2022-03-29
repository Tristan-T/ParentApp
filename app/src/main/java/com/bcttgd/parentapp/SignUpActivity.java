package com.bcttgd.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Set validation on email and password
        TextInputLayout nameLayout = findViewById(R.id.name_signup_layout);
        TextInputLayout emailLayout = findViewById(R.id.email_signup_layout);
        TextInputLayout passwordLayout = findViewById(R.id.password_signup_layout);

        nameLayout.getEditText().addTextChangedListener(new NameWatcher(nameLayout));

        emailLayout.getEditText().addTextChangedListener(new EmailWatcher(emailLayout));

        passwordLayout.getEditText().addTextChangedListener(new PasswordWatcher(passwordLayout));


    }

    public void onLoginClick(View view) {
        //Launch LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("email", getIntent().getStringExtra("email"));
        startActivity(intent);
    }

    public void onSignupClick(View view) {
        //Get name, email and password
        TextInputLayout nameLayout = findViewById(R.id.name_signup_layout);
        TextInputLayout emailLayout = findViewById(R.id.email_signup_layout);
        TextInputLayout passwordLayout = findViewById(R.id.password_signup_layout);

        //Check if all fields are filled
        if(TextUtils.isEmpty(nameLayout.getEditText().getText().toString())){
            nameLayout.setError(getString(R.string.error_name));
        }

        if(TextUtils.isEmpty(emailLayout.getEditText().getText().toString())){
            emailLayout.setError(getString(R.string.error_email));
        }

        if(TextUtils.isEmpty(passwordLayout.getEditText().getText().toString())){
            passwordLayout.setError(getString(R.string.error_password));
        }

        //Check if all fields are properly filled
        if(nameLayout.getError() == null && emailLayout.getError() == null && passwordLayout.getError() == null) {
            //If all fields are filled, go to login activity
            try {
                if(User.createUser(nameLayout.getEditText().getText().toString(), emailLayout.getEditText().getText().toString(), passwordLayout.getEditText().getText().toString())) {
                    //Launch LoginActivity
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("email", emailLayout.getEditText().getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.error_signup), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //TODO : CATCH PROPER ERRORS
                Toast.makeText(this, getString(R.string.error_signup), Toast.LENGTH_SHORT).show();
            }
        }

    }
}