package com.bcttgd.parentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get FragmentContainer
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerMain);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Google recommends to use if instead of switches}
                if(item.getItemId() == R.id.bottom_navigation_menu_item_data) {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new DataFragment()).commit();
                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_camera) {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new CameraFragment()).commit();
                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_map) {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new MapFragment()).commit();
                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_alert) {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new AlertFragment()).commit();
                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_account) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new AccountFragment()).commit();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Logs out the user and sends them back to the login screen
     * @param view
     */
    public void onLogoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        //Destroy MainActivity and launch LoginActivity
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Delete the user account and send them back to the login screen
     * @param view
     */
    public void onDeleteClick(View view) {
        AlertDialog builder = new MaterialAlertDialogBuilder(this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton(R.string.Yes, (dialog, which) -> {
                //Delete Firebase account
                FirebaseAuth.getInstance().getCurrentUser().delete();
                FirebaseAuth.getInstance().signOut();
                //Destroy MainActivity and launch LoginActivity
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                finish();
            })
            .setNegativeButton(R.string.No, (dialog, which) -> {
                //Do nothing
            })
        .show();
    }


}
