package com.sislamrafi.corona.firebase;

import com.google.firebase.database.DataSnapshot;

public interface FirebaseHelperListener {
    void onDataReady(DataSnapshot dataSnapshot, int type);
}
