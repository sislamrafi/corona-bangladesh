package com.sislamrafi.corona.account;

import android.location.LocationListener;

import com.google.android.gms.maps.model.LatLng;
import com.sislamrafi.corona.firebase.Covid19;
import com.sislamrafi.corona.firebase.NotificationPanel;

public class AccountInfo {
    private static String id = "test";
    private static String name;
    private static String gmail;

    private static LatLng lastUserlocation;
    private static LatLng lastGpslocation;
    private static LatLng lastMarkerlocation;

    private static Covid19 Bangladesh;
    private static Covid19 World;

    private static NotificationPanel notificationPanel;

    private static LocationChangeListener mlocationChange;

    public AccountInfo(String name, String gmail) {
        this.name = name;
        this.gmail = gmail;
        this.id = generateId();
    }

    public void setAccountInfo(String name, String gmail){
        this.name = name;
        this.gmail = gmail;
        this.id = generateId();
    }

    public static void setListenerX(LocationChangeListener lcl){
        AccountInfo.mlocationChange = lcl;
    }

    private String generateId(){
        String tmp = this.gmail;
        tmp = tmp.replace(".com","");
        tmp = tmp.replace(".","%20");
        return tmp;
    }

    public static LatLng getLastUserlocation() {
        return lastUserlocation;
    }

    public static void setLastUserlocation(LatLng lastUserlocation) {
        if(mlocationChange!=null)
            mlocationChange.onChangeUserLocation(lastUserlocation);
        AccountInfo.lastUserlocation = lastUserlocation;
    }

    public static void setLastMarkerlocation(LatLng lastMarkerlocation) {
        AccountInfo.lastMarkerlocation = lastMarkerlocation;
    }

    public static LatLng getLastMarkerlocation(){
        return AccountInfo.lastMarkerlocation;
    }

    public static LatLng getLastGpslocation() {
        return lastGpslocation;
    }

    public static void setLastGpslocation(LatLng location) {
        if(mlocationChange!=null)
            mlocationChange.onChangeGpsLocation(location);;
        AccountInfo.lastGpslocation = location;
    }

    public static String getId() {
        return id;
    }

    public static String getName() {
        return name;
    }

    public static String getGmail() {
        return gmail;
    }

    public static Covid19 getBangladesh() {
        return Bangladesh;
    }

    public static void setBangladesh(Covid19 bangladesh) {
        Bangladesh = bangladesh;
    }

    public static Covid19 getWorld() {
        return World;
    }

    public static void setWorld(Covid19 world) {
        World = world;
    }

    public static NotificationPanel getNotificationPanel() {
        return notificationPanel;
    }

    public static void setNotificationPanel(NotificationPanel notificationPanel) {
        AccountInfo.notificationPanel = notificationPanel;
    }
}
