package com.bcttgd.parentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_camera) {

                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_map) {

                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_alert) {

                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_account) {

                    return true;
                }
                return false;
            }
        });
    }
}
