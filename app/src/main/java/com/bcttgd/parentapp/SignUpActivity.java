package com.bcttgd.parentapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    //Firebase Authenticator
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Set validation on email and password
        TextInputLayout nameLayout = findViewById(R.id.name_signup_layout);
        TextInputLayout emailLayout = findViewById(R.id.email_signup_layout);
        TextInputLayout passwordLayout = findViewById(R.id.password_signup_layout);

        nameLayout.getEditText().addTextChangedListener(new NameWatcher(nameLayout));

        emailLayout.getEditText().addTextChangedListener(new EmailWatcher(emailLayout));

        passwordLayout.getEditText().addTextChangedListener(new PasswordWatcher(passwordLayout));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
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
            String name = nameLayout.getEditText().getText().toString();
            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();

            //If all fields are filled, try to create the user account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "SignUpActivity:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                // Change the username of user
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                user.updateProfile(profileUpdates);
                                // Launch MainActivity
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "SignUpActivity:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}