package com.bcttgd.parentapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bcttgd.parentapp.Area;
import com.bcttgd.parentapp.CustomNotification;
import com.bcttgd.parentapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnMapClickListener {

    private MapView mapView;

    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
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
    ExtendedFloatingActionButton fabLocation;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    Polygon currentPolygon = null;
    String areaName;
    String areaTag;
    List<Area> areaList = new ArrayList<>();
    boolean hasLocationPermission;
    private LocalDateTime lastTimeCheck;
    boolean childMarkerCreated;
    Marker childPositionMarker;
    LatLng childPosition;

    public MapFragment() {
        // Required empty public constructor
    }

    private void drawAreas() {
        List<Area> areaListPreferences = getAreaListFromPreferences("areaList");
        if (areaListPreferences != null) {
            areaList = areaListPreferences;
            for (Area area : areaList) {
                Log.d("system out", "area name : " + area.getName());
                Log.d("system out", "area : " + area);
                Polygon polygon = gMap.addPolygon(new PolygonOptions()
                        .clickable(true)
                        //.addAll(area.getPolygon().getPoints()));
                        .addAll(area.getLatLngList()));
                polygon.setTag(area.getTag());
                stylePolygon(polygon);
                area.setPolygon(polygon);
                gMap.setOnPolygonClickListener(this);

                List<Marker> temp = new ArrayList<>();
                for (LatLng l : area.getLatLngList()) {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(l)
                            .draggable(true);
                    //.icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24));
                    Marker marker = gMap.addMarker(markerOptions);
                    temp.add(marker);
                }
                area.setMarkerList(temp);
                area.hideMarkers();
            }
        }
    }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        Toast.makeText(getContext(), "appuie zone", Toast.LENGTH_SHORT).show();
        boolean isInPolygon = PolyUtil.containsLocation(new LatLng(53.520790, 17.404158), polygon.getPoints(), true);

        Area area = getZoneFromPolygon(polygon);
        Log.d("system out", "area after click polygon : " + area);

        if(isInPolygon) {
            CustomNotification.createOutOfZoneNotification("0664757895", "Kevin", area.getName(), "", getActivity(), getContext());
        }
        if(!modifyingArea && !definingArea) {
            currentPolygon = polygon;
            infoDialogView = LayoutInflater.from(getContext())
                    .inflate(R.layout.area_info, null, false);

            ImageButton deleteAreaBtn = infoDialogView.findViewById(R.id.deleteAreaBtn);
            TextView areaNameInfoTv = infoDialogView.findViewById(R.id.areaNameInfo);
            TextView areaTagInfoTv = infoDialogView.findViewById(R.id.areaTagInfo);

            areaNameInfoTv.setText(String.format("%s%s", getString(R.string.areaNameTitle), area.getName()));
            areaTagInfoTv.setText(String.format("%s%s", getString(R.string.areaTagTitle), area.getTag()));

            AlertDialog alert = new MaterialAlertDialogBuilder(getContext()).setView(infoDialogView)
                    .setTitle(getResources().getString(R.string.areaInfoTitle))
//                    .setNeutralButton(R.string.Delete, (dialog, which) -> {
//                        area.getPolygon().remove();
//                        areaList.remove(area);
//                        saveAreaListToPreferences(areaList, "areaList");
//                    })
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

            deleteAreaBtn.setOnClickListener(view -> {
                area.getPolygon().remove();
                areaList.remove(area);
                alert.dismiss();
                saveAreaListToPreferences(areaList, "areaList");
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        this.drawAreas();
        //checkLocationPermission();

        gMap.setOnMapClickListener(latLng -> {
            if(definingArea) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .draggable(true);
                //.icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_location_on_24));
                Marker marker = gMap.addMarker(markerOptions);
                latLngList.add(latLng);
                markerList.add(marker);
                if(markerList.size() == 1) {
                    Polygon polygon = gMap.addPolygon(new PolygonOptions()
                            .clickable(true)
                            .addAll(latLngList));
                    polygon.setTag(areaTag);
                    stylePolygon(polygon);
                    gMap.setOnPolygonClickListener(this);
                    currentPolygon = polygon;
                } else {
                    currentPolygon.setPoints(latLngList);
                }
            }
        });
        gMap.setOnMarkerClickListener(marker -> {
            if(marker.equals(childPositionMarker)) {
                long diff = java.time.Duration.between(lastTimeCheck, LocalDateTime.now()).getSeconds();
                if(diff > 60) {
                    long min = diff / 60;
                    long sec = diff % 60;
                    marker.setTitle("Il y a " + min + "min et " + sec + "sec");
                } else marker.setTitle("Il y a " + diff + "sec");
            }
            return false;
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
                if(definingArea) {
                    index = markerList.indexOf(marker);
                    points = currentPolygon.getPoints();
                }
                else if(currentPolygon != null) {
                    area = getZoneFromMarker(marker);
                    index = area.getMarkerList().indexOf(marker);
                    points = currentPolygon.getPoints();
                } else markerList.indexOf(marker);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(currentPolygon != null && !definingArea) {
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

        String deviceID = getArguments().getString("device");
        String deviceName = getArguments().getString("deviceName");
        Log.d("system out", "device: " + deviceID);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://devmobilem1-default-rtdb.europe-west1.firebasedatabase.app");
        String reference = "users/" + mAuth.getCurrentUser().getUid() + "/devicesData/" + deviceID + "/location";
        Log.d("DataFragment", reference);
        DatabaseReference myRef = database.getReference(reference);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message = dataSnapshot.getValue(String.class);
                Log.d("DataFragment", "Message: " + message);
                double latitude = Double.parseDouble(message.split(",")[0]);
                double longitude = Double.parseDouble(message.split(",")[1]);
                childPosition = new LatLng(latitude, longitude);
                checkChildLocation(childPosition, deviceName);

                if(!childMarkerCreated) {
                    MarkerOptions markerOptionsChildPos = new MarkerOptions()
                            .position(childPosition)
                            .draggable(false)
                            .icon(BitmapFromVector(getContext(), R.drawable.ic_baseline_point_24));
                    childPositionMarker = gMap.addMarker(markerOptionsChildPos);
                    childMarkerCreated = true;
                } else childPositionMarker.setPosition(childPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        childMarkerCreated = false;
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment

        fab = v.findViewById(R.id.fab);
        fabLocation = v.findViewById(R.id.fabLocation);
        definingArea = false;
        modifyingArea = false;

        fabLocation.setOnClickListener(view -> {
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(childPosition, 15));
        });

        fab.setOnClickListener(view -> {
            if(!definingArea && !modifyingArea) {
                customAlertDialogView = LayoutInflater.from(getContext())
                        .inflate(R.layout.new_zone_dialog, null, false);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        R.layout.sensibilite_zone, TAGS);
                AutoCompleteTextView textView = (AutoCompleteTextView) customAlertDialogView.findViewById(R.id.ZonesTags);
                textView.setAdapter(adapter);

                areaNameTv = customAlertDialogView.findViewById(R.id.areaName);
                areaTagTv = textView;

                new MaterialAlertDialogBuilder(getContext()).setView(customAlertDialogView)
                        .setTitle(getResources().getString(R.string.newZone))
                        .setNegativeButton(R.string.Annuler, (dialog, which) -> {})
                        .setPositiveButton(R.string.Ok, (dialog, which) -> {
                            fab.setIconResource(R.drawable.ic_baseline_check_24);
                            fab.setExtended(false);
                            definingArea = true;
                            areaName = areaNameTv.getText().toString();
                            areaTag = areaTagTv.getText().toString();
                        })
                        .show();

            } else if(definingArea) {
                if(!markerList.isEmpty()) {

                    Area area = new Area(new ArrayList(latLngList), new ArrayList(markerList), currentPolygon, areaTag, areaName);
                    areaList.add(area);

                    for (Marker m : markerList) {
                        m.setVisible(false);
                    }
                    latLngList.clear();
                    markerList.clear();
                    areaTag = null;
                    areaName = null;
                    definingArea = false;
                    currentPolygon = null;
                }
                definingArea = false;
                fab.setIconResource(R.drawable.ic_baseline_add_24);
                fab.setText(R.string.Zone);
                fab.setExtended(true);
                saveAreaListToPreferences(areaList, "areaList");

            } else if(modifyingArea) {
                if(currentPolygon != null) {
                    Area area = getZoneFromPolygon(currentPolygon);
                    area.hideMarkers();
                    currentPolygon = null;
                }
                modifyingArea = false;
                fab.setIconResource(R.drawable.ic_baseline_add_24);
                fab.setText(R.string.Zone);
                fab.setExtended(true);
                saveAreaListToPreferences(areaList, "areaList");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkChildLocation(LatLng position, String deviceName) {
        lastTimeCheck = LocalDateTime.now();
        for(Area area : areaList) {
            Log.d("system out", "polygon points : " + area.getPolygon().getPoints());
            Log.d("system out", "position : " + position);
            boolean isInPolygon = PolyUtil.containsLocation(position, area.getPolygon().getPoints(), true);
            Log.d("system out", "isInPolygon : " + isInPolygon);
            if(isInPolygon) {
                CustomNotification.createOutOfZoneNotification("0664757895", deviceName, area.getName(), "", getActivity(), getContext());
            }
            return;
        }
    }

    private void saveAreaListToPreferences(List<Area> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        GsonBuilder builder = new GsonBuilder();
        Type type = new TypeToken<ArrayList<Area>>() {}.getType();
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT);
        String json = builder.create().toJson(list, type);
        Log.d("system out", "json : " + json);
        editor.putString(key, json);
        editor.apply();
    }

    private ArrayList<Area> getAreaListFromPreferences(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Area>>() {}.getType();
        return gson.fromJson(json, type);
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

}

