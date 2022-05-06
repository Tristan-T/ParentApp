package com.bcttgd.parentapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.List;

public class Area {

    private List<LatLng> latLngList;
    private List<Marker> markerList;
    private Polygon polygon;
    private String tag;
    private String name;

    public Area(List<LatLng> latLngList, List<Marker> markerList, Polygon polygon, String tag, String name) {
        this.latLngList = latLngList;
        this.markerList = markerList;
        this.polygon = polygon;
        this.tag = tag;
        this.name = name;
    }

    public void drawMarkers() {
        for (Marker m: markerList) {
            m.setVisible(true);
        }
    }

    public void hideMarkers() {
        for (Marker m: markerList) {
            m.setVisible(false);
        }
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }
}
