package com.sislamrafi.corona.firebase;

public class LocationStructure {
    private String name;
    private int Family;
    private int Neighbour;
    private String lat;
    private String lon;

    public LocationStructure() {
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

    public String toString(){
        return "Lat = "+lat+" Lon = "+lon+" Family = "+Family+" Neighbour = "+Neighbour;
    }
}
