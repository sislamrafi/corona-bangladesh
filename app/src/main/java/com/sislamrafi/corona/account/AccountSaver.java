package com.sislamrafi.corona.account;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sislamrafi.corona.firebase.Covid19;
import com.sislamrafi.corona.firebase.NotificationPanel;

public class AccountSaver {
    private static final String TAG = "AccountSaver";

    private LatLng lastUserlocation;
    private LatLng lastGpslocation;
    private LatLng lastMarkerlocation;

    private Covid19 Bangladesh;
    private Covid19 World;
    private NotificationPanel notificationPanel;

    public AccountSaver(){
        this.lastGpslocation = AccountInfo.getLastGpslocation();
        this.lastUserlocation = AccountInfo.getLastUserlocation();
        this.lastMarkerlocation = AccountInfo.getLastMarkerlocation();
        this.Bangladesh = AccountInfo.getBangladesh();
        this.World = AccountInfo.getWorld();
        this.notificationPanel = AccountInfo.getNotificationPanel();
    }

    public void getSavedData(){
        //Log.d(TAG,"Prove that i got it "+World.getActive());
        AccountInfo.setLastGpslocation(lastGpslocation);
        AccountInfo.setLastMarkerlocation(lastMarkerlocation);
        AccountInfo.setLastUserlocation(lastUserlocation);
        AccountInfo.setNotificationPanel(notificationPanel);
        AccountInfo.setWorld(World);
        AccountInfo.setBangladesh(Bangladesh);
    }
}
