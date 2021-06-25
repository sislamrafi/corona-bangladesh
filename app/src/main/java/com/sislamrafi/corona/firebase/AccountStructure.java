package com.sislamrafi.corona.firebase;

import com.google.android.gms.maps.model.LatLng;

public class AccountStructure {
    private String name;
    private int Family;
    private int Neighbour;
    private String lat;
    private String lon;

    public AccountStructure(){}

    public AccountStructure(String name, int family, int neighbour, LatLng position) {
        this.name = name;
        Family = family;
        Neighbour = neighbour;
        lat = String.format("%.6f", position.latitude);
        lon = String.format("%.6f", position.longitude);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFamily() {
        return Family;
    }

    public void setFamily(int family) {
        Family = family;
    }

    public int getNeighbour() {
        return Neighbour;
    }

    public void setNeighbour(int neighbour) {
        Neighbour = neighbour;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
