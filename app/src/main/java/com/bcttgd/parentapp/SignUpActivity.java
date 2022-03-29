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
        TextInputLayout emailLayout = findViewById(R.id.email_signup_layout);
        TextInputLayout passwordLayout = findViewById(R.id.password_signup_layout);

        emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()){
                    emailLayout.setError(null);
                } else {
                    emailLayout.setError(getString(R.string.error_email));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            //CF : https://stackoverflow.com/questions/36574183/how-to-validate-password-field-in-android
            public boolean isValidPassword(final String password) {
                Pattern pattern;
                Matcher matcher;
                final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
                pattern = Pattern.compile(PASSWORD_PATTERN);
                matcher = pattern.matcher(password);

                return matcher.matches();

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isValidPassword(charSequence.toString())){
                    passwordLayout.setError(getString(R.string.error_password));
                } else {
                    passwordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void onLoginClick(View view) {
        //Launch LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("email", getIntent().getStringExtra("email"));
        startActivity(intent);
        finish();
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

        if(emailLayout.getError() != null && passwordLayout.getError() != null) {
            //If all fields are filled, go to login activity
            Toast.makeText(this, "All fields are filled correctly", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}