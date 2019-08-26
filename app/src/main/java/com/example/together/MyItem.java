package com.example.together;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    public int bg;

    public MyItem(double lat, double lng, int bg) {
        mPosition = new LatLng(lat, lng);
        this.bg = bg;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

//    private LatLng mPosition;
//    private String mTitle;
//    private String mSnippet;
//
//    public MyItem(double lat, double lng) {
//        mPosition = new LatLng(lat, lng);
//    }
//
//    public MyItem(double lat, double lng, String title, String snippet) {
//        mPosition = new LatLng(lat, lng);
//        mTitle = title;
//        mSnippet = snippet;
//    }
//
//    @Override
//    public LatLng getPosition() {
//        return mPosition;
//    }
//
//    @Override
//    public String getTitle() {
//        return mTitle;
//    }
//
//    @Override
//    public String getSnippet() {
//        return mSnippet;
//    }
}