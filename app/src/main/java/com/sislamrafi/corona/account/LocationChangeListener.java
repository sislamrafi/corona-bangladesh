package com.sislamrafi.corona.account;

import com.google.android.gms.maps.model.LatLng;

public interface LocationChangeListener {
    public void onChangeGpsLocation(LatLng location);
    public void onChangeUserLocation(LatLng location);
}
