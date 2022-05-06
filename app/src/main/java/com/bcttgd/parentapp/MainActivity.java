package com.bcttgd.parentapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.bcttgd.parentapp.Fragments.AccountFragment;
import com.bcttgd.parentapp.Fragments.AlertFragment;
import com.bcttgd.parentapp.Fragments.DataFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnMapClickListener {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MaterialToolbar toolbar;
    Menu menu;
    FirebaseAuth mAuth;
    FirebaseUser user;
    public String selectedDevice;





    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_DARK_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_LIGHT_GREEN_ARGB = 0xff81C784;
    private static final int COLOR_DARK_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_LIGHT_ORANGE_ARGB = 0x7EFF8300;
    private static final int COLOR_DARK_RED_ARGB = 0xFFFF1816;
    private static final int COLOR_LIGHT_RED_ARGB = 0x98FF1816;
    private static final int COLOR_DARK_YELLOW_ARGB = 0xF3FFFF37;
    private static final int COLOR_LIGHT_YELLOW_ARGB = 0x7EFFFF00;

    private static final int POLYGON_STROKE_WIDTH_PX = 4;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 0;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    private static final String SENSIBLE = "Sensible";
    private static final String MOY_SENSIBLE = "Moyennement sensible";
    private static final String TRES_SENSIBLE = "Très sensible";

    private static final String[] TAGS = new String[] {
            SENSIBLE, MOY_SENSIBLE, TRES_SENSIBLE
    };

    GoogleMap gMap;
    EditText areaNameTv;
    EditText areaTagTv;
    View customAlertDialogView;
    View infoDialogView;
    boolean definingArea;
    boolean modifyingArea;
    ExtendedFloatingActionButton fab;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    Polygon currentPolygon;
    String areaName;
    String areaTag;
    List<Area> areaList = new ArrayList<>();
    boolean hasLocationPermission;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Area getZoneFromPolygon(Polygon p) {
        Area area = areaList
                .stream()
                .filter(z -> z.getPolygon().equals(p))
                .findFirst()
                .orElse(null);
        return area;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Area getZoneFromMarker(Marker marker) {
        Area area = areaList
                .stream()
                .filter(z -> {
                    for (Marker m : z.getMarkerList()) {
                        if (m.equals(marker)) {
                            return true;
                        }
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
        return area;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void createMap() {
        fab = findViewById(R.id.fab);
        definingArea = false;
        modifyingArea = false;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerMain);
        mapFragment.getMapAsync(this);

        fab.setOnClickListener(view -> {
            if(!definingArea && !modifyingArea) {
                customAlertDialogView = LayoutInflater.from(this)
                        .inflate(R.layout.new_zone_dialog, null, false);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.sensibilite_zone, TAGS);
                AutoCompleteTextView textView = (AutoCompleteTextView) customAlertDialogView.findViewById(R.id.ZonesTags);
                textView.setAdapter(adapter);

                areaNameTv = customAlertDialogView.findViewById(R.id.areaName);
                areaTagTv = textView;

                new MaterialAlertDialogBuilder(this).setView(customAlertDialogView)
                        .setTitle(getResources().getString(R.string.newZone))
                        .setNegativeButton(R.string.Annuler, (dialog, which) -> {})
                        .setPositiveButton(R.string.Ok, (dialog, which) -> {
                            fab.setIconResource(R.drawable.ic_baseline_check_24);
                            fab.setExtended(false);
                            fab.setText("");
                            definingArea = true;
                            areaName = areaNameTv.getText().toString();
                            areaTag = areaTagTv.getText().toString();
                        })
                        .show();

            } else if(definingArea) {
                Polygon polygon = gMap.addPolygon(new PolygonOptions()
                        .clickable(true)
                        .addAll(latLngList));
                polygon.setTag(areaTag);
                stylePolygon(polygon);
                gMap.setOnPolygonClickListener(this);
                currentPolygon = null;

                Area area = new Area(new ArrayList(latLngList), new ArrayList(markerList), polygon, areaTag, areaName);
                areaList.add(area);

                for (Marker m: markerList) {
                    m.setVisible(false);
                }
                latLngList.clear();
                markerList.clear();
                areaTag = null;
                areaName = null;
                definingArea = false;

                fab.setIconResource(R.drawable.ic_baseline_add_24);
                fab.setExtended(true);
                fab.setText("Zone");
            } else if(modifyingArea) {
                if(currentPolygon != null) {
                    Area area = getZoneFromPolygon(currentPolygon);
                    area.hideMarkers();
                    currentPolygon = null;
                }
                modifyingArea = false;
                fab.setIconResource(R.drawable.ic_baseline_add_24);
                fab.setExtended(true);
                fab.setText("Zone");
            }
        });
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        boolean isInPolygon = PolyUtil.containsLocation(new LatLng(53.520790, 17.404158), polygon.getPoints(), true);

        currentPolygon = polygon;
        Area area = getZoneFromPolygon(polygon);

        if(isInPolygon) {
            CustomNotification.createOutOfZoneNotification("0664757895", "Kevin", area.getName(), "", MainActivity.this, this);
        }
        if(!modifyingArea && !definingArea) {
            infoDialogView = LayoutInflater.from(this)
                    .inflate(R.layout.area_info, null, false);

            TextView areaNameInfoTv = infoDialogView.findViewById(R.id.areaNameInfo);
            TextView areaTagInfoTv = infoDialogView.findViewById(R.id.areaTagInfo);

            areaNameInfoTv.setText("Nom : " + area.getName());
            areaTagInfoTv.setText("Caractéristique : " + area.getTag());

            new MaterialAlertDialogBuilder(this).setView(infoDialogView)
                    .setTitle(getResources().getString(R.string.areaInfoTitle))
                    .setNegativeButton(R.string.Annuler, (dialog, which) -> {
                    })
                    .setPositiveButton(R.string.Modifier, (dialog, which) -> {
                        fab.setIconResource(R.drawable.ic_baseline_check_24);
                        fab.setExtended(false);
                        fab.setText(null);
                        modifyingArea = true;
                        area.drawMarkers();
                    })
                    .show();
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        //checkLocationPermission();
        MarkerOptions markerOptionsTest = new MarkerOptions()
                .position(new LatLng(53.520790, 17.404158))
                .draggable(true);
        gMap.addMarker(markerOptionsTest);
        gMap.setOnMapClickListener(latLng -> {
            if(definingArea) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true);
                //.icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24));
                Marker marker = gMap.addMarker(markerOptions);
                latLngList.add(latLng);
                markerList.add(marker);
            }
        });
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            LatLng temp = null;
            List<LatLng> points;
            int index;
            Area area;
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onMarkerDragStart(Marker marker) {
                temp = marker.getPosition();
                if(currentPolygon != null) {
                    area = getZoneFromMarker(marker);
                    index = area.getMarkerList().indexOf(marker);
                    points = currentPolygon.getPoints();
                } else markerList.indexOf(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(currentPolygon != null) {
                    index = area.getMarkerList().indexOf(marker);
                    if(index == 0) {
                        points.set(points.size() - 1, temp);
                    }
                    points.set(index, temp);
                    currentPolygon.setPoints(points);
                    area.getLatLngList().set(index, temp);
                }
                else {
                    index = markerList.indexOf(marker);
                    latLngList.set(index, temp);
                }
                marker.setPosition(temp);
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                temp = marker.getPosition();
                if(currentPolygon != null) {
                    if(index == 0) {
                        points.set(points.size() - 1, temp);
                    }
                    points.set(index, temp);
                    currentPolygon.setPoints(points);
                }
                marker.setPosition(temp);
            }
        });
    }

    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        Log.d("system out", "type : " + type);
        switch (type) {
            // If no type is given, allow the API to use the default.
            case SENSIBLE:
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_DARK_YELLOW_ARGB;
                fillColor = COLOR_LIGHT_YELLOW_ARGB;
                break;
            case MOY_SENSIBLE:
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_DARK_ORANGE_ARGB;
                fillColor = COLOR_LIGHT_ORANGE_ARGB;
                break;
            case TRES_SENSIBLE:
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_DARK_RED_ARGB;
                fillColor = COLOR_LIGHT_RED_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMap();
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
