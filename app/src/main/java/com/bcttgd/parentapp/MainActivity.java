package com.bcttgd.parentapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bcttgd.parentapp.Fragments.AccountFragment;
import com.bcttgd.parentapp.Fragments.AlertFragment;
import com.bcttgd.parentapp.Fragments.DataFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    Menu menu;
    FirebaseAuth mAuth;
    FirebaseUser user;
    public String selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        //Get FragmentContainer
        FragmentContainerView fragmentContainerView = findViewById(R.id.fragmentContainerMain);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle bundle = new Bundle();
                bundle.putString("device", selectedDevice);
                // Google recommends to use if instead of switches
                if(item.getItemId() == R.id.bottom_navigation_menu_item_data) {
                    //Pass the selected device to the fragment
                    DataFragment dataFragment = new DataFragment();
                    dataFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, dataFragment).commit();
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new AlertFragment()).commit();
                    return true;
                }

                if(item.getItemId() == R.id.bottom_navigation_menu_item_account) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerMain, new AccountFragment()).commit();
                    return true;
                }
                return false;
            }
        });

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Get current user
        user = mAuth.getCurrentUser();

        //Get Navigation Drawer
        navigationView = findViewById(R.id.navigationView);

        //Get the TopAppBar
        toolbar = findViewById(R.id.topAppBar);

        //Get the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        //Set the items in the navigation drawer
        menu = navigationView.getMenu();

        initializeNavigationDrawer();
    }

    /**
     * Initialize the navigation drawer
     */
    public void initializeNavigationDrawer() {
        //Get all the devices in the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("users/" + mAuth.getUid() + "/devices");

        ArrayList<String> deviceNames = new ArrayList<>();
        ArrayList<String> deviceIDs = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    deviceNames.add(ds.getValue(String.class));
                    deviceIDs.add(ds.getKey());
                    menu.add(ds.getValue(String.class));
                    Log.d(TAG, "Value is: " + ds.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Set the title of the TopAppBar
        toolbar.setTitle("Select a Device");

        //Open Navigation Drawer when toolbar is clicked
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //Set the listener for the navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Uncheck all other items
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setChecked(false);
                }
                //Set the text of the selected item
                item.setChecked(true);
                //Set the title of the activity
                toolbar.setTitle(item.getTitle());
                //Close the navigation drawer
                drawerLayout.closeDrawers();

                //Get the device ID
                Log.d(TAG, "Device ID: " + deviceIDs.get(item.getItemId()));
                String deviceID = deviceIDs.get(item.getItemId());
                //Set the device ID
                selectedDevice = deviceID;
                return true;
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
        new MaterialAlertDialogBuilder(this,
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
