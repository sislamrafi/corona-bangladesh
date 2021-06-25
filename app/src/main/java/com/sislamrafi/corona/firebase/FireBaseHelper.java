package com.sislamrafi.corona.firebase;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sislamrafi.corona.account.AccountInfo;

import java.util.ArrayList;

public class FireBaseHelper {
    private final static String TAG = "FirebaseHelper";

    public static final int BANGLADESH = 166;
    public static final int WORLD = 170;
    public static final int ACCOUNT = 548;
    public static final int MAP = 787;
    public static final int NOTIFICATION = 4538;

    private static AccountStructure account;
    private static String id;
    private DatabaseReference mDatabase;
    private static FirebaseDatabase database;

    private static final String USER_TAG = "users";
    private static final String COVID19_TAG = "covid19";
    private static final String LOCATION_TAG = "location";
    private static final String NOTIFICATION_PANEL = "status";

    public static ArrayList<LocationStructure> allLocations;

    private static FirebaseHelperListener firebaseHelperListener;

    private static LatLng CenterLocation = new LatLng(23.807566,90.362541);
    private static Double LocationFactor = 0.02;

    private static int receivedChild;
    private static int totalLoop;


    public FireBaseHelper(){

    }

    public static void setListenerX(FirebaseHelperListener fbh){
        FireBaseHelper.firebaseHelperListener = fbh;
    }

    public static void loginOrCreateAccount(String id){
        database = FirebaseDatabase.getInstance();
        FireBaseHelper.id = id;
        Log.d(TAG, "Id = "+id);
        DatabaseReference ref = database.getReference(USER_TAG);
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    create();
                    Log.d(TAG, "Create Id");
                }else {
                    account = dataSnapshot.getValue(AccountStructure.class);
                    if(firebaseHelperListener!=null)
                        firebaseHelperListener.onDataReady(dataSnapshot,ACCOUNT);
                    Log.d(TAG, "Login successful");
                }
                Log.d(TAG, "Value for "+ dataSnapshot.getKey() +" from firebase " + dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void create(){
        DatabaseReference ref = database.getReference(USER_TAG);
        account = new AccountStructure(AccountInfo.getName(),0,0, new LatLng(23.8068,90.3636));
        ref.child(id).setValue(account);
    }

    public static void update(int family, int neighbour, LatLng position){
        if(account == null||position == null)return;
        account.setFamily(family);
        account.setNeighbour(neighbour);
        account.setLat(String.format("%.6f",position.latitude));
        account.setLon(String.format("%.6f",position.longitude));
        DatabaseReference ref = database.getReference("users");
        ref.child(id).child("family").setValue(family);
        ref.child(id).child("neighbour").setValue(neighbour);
        ref.child(id).child("lat").setValue(account.getLat());
        ref.child(id).child("lon").setValue(account.getLon());
    }

    public static void updateNeeds(int sanit, int food, LatLng position){
        if(account == null||position == null)return;
        account.setLat(String.format("%.6f",position.latitude));
        account.setLon(String.format("%.6f",position.longitude));
        DatabaseReference ref = database.getReference("users");
        ref.child(id).child("sanitizer").setValue(sanit);
        ref.child(id).child("food").setValue(food);
        ref.child(id).child("lat").setValue(account.getLat());
        ref.child(id).child("lon").setValue(account.getLon());
    }

    public static void updateFamily(int val,LatLng position){
        if(account == null)return;
        DatabaseReference ref = database.getReference("users");
        ref.child(id).child("family").setValue(val);
        updateLocation(position);
    }

    public static void updateNeighbour(int val,LatLng position){
        if(account == null||position == null)return;
        DatabaseReference ref = database.getReference("users");
        ref.child(id).child("neighbour").setValue(val);
        updateLocation(position);
    }

    private static void updateLocation(LatLng position){
        DatabaseReference ref = database.getReference("users");
        ref.child(id).child("lat").setValue(position.latitude);
        ref.child(id).child("lon").setValue(position.longitude);
    }

    public static void getCovid19Update(final int Place){
        final String place;
        final int type = Place;
        if(Place == BANGLADESH){
            place = "Bangladesh";
        }else{
            place = "World";
        }
        DatabaseReference ref = database.getReference(COVID19_TAG);
        ref.child(place).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Log.d(TAG, "Database Error:at@"+place);
                }else {
                    if(firebaseHelperListener!=null)
                        firebaseHelperListener.onDataReady(dataSnapshot,type);
                    Log.d(TAG, "Value for "+ dataSnapshot.getKey() +" from firebase " + dataSnapshot.getValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void getNotificationPanel(){
        DatabaseReference ref = database.getReference(NOTIFICATION_PANEL);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Log.d(TAG, "Database Error:at@"+NOTIFICATION_PANEL);
                }else {
                    if(firebaseHelperListener!=null)
                        firebaseHelperListener.onDataReady(dataSnapshot,NOTIFICATION);
                    Log.d(TAG, "Value for "+ dataSnapshot.getKey() +" from firebase " + dataSnapshot.getValue());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void getMarkerLocations(LatLng location){
        if(location == null)return;
        double distance = locationToDistanceFactor(location);
        double tmp = distance + 0.5;

        int idX = (int) distance;
        int supportId = (int) tmp;

        Log.d(TAG,"Location index : "+idX);

        totalLoop = 0;
        receivedChild = 0;

        if ( supportId - idX == 0 )supportId = idX - 1;
        if(allLocations!=null)
            allLocations.clear();
        else allLocations = new ArrayList<>();
        if(supportId<0) {
            getCovid19Locations(0 + "", false);
            getCovid19Locations(1+"",true);
        }

        if(supportId>=0) {
            getCovid19Locations(idX+"",false);
            getCovid19Locations(supportId + "", true);
        }
    }

    public static void getCovid19Locations(final String Area, final Boolean isEnd){
        DatabaseReference ref = database.getReference(LOCATION_TAG);
        ref.child(Area).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalLoop+=1;
                if(dataSnapshot.getValue() == null){
                    Log.d(TAG, "Database Error:at@Location"+Area);
                }else {
                    receivedChild += dataSnapshot.getChildrenCount();
                    Log.d(TAG, "Value for "+ dataSnapshot.getKey() +" from firebase " + dataSnapshot.getValue());
                    int i = 1;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        getUserData(ds.getKey());
                        i++;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void getUserData(final String uid){
        DatabaseReference ref = database.getReference(USER_TAG);
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Log.d(TAG, "Database Error:at@"+uid);
                }else {
                    //firebaseHelperListener.onDataReady(dataSnapshot,MAP);
                    LocationStructure lS = dataSnapshot.getValue(LocationStructure.class);
                    allLocations.add(lS);
                    Log.d(TAG, "Value for "+ dataSnapshot.getKey() +" from firebase " + lS.toString());
                    if(firebaseHelperListener!=null&&totalLoop==2&&receivedChild==allLocations.size()) {
                        Log.d(TAG,"Num of Locations : "+allLocations.size());
                        firebaseHelperListener.onDataReady(null, MAP);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void deleteLocation(String accountId, LatLng location){
        if(accountId == null || location == null)return;
        DatabaseReference ref = database.getReference(LOCATION_TAG);
        ref.child(locationToID(location)).child(accountId).removeValue();
    }

    public static void updateToLocation(String accountId, LatLng location){
        if(accountId == null || location == null)return;
        DatabaseReference ref = database.getReference(LOCATION_TAG);
        ref.child(locationToID(location)).child(accountId).setValue(1);
    }

    public static String locationToID(LatLng location){
        if(location==null)return "";
        double result = Math.sqrt(Math.pow(CenterLocation.latitude-location.latitude,2)+Math.pow(CenterLocation.longitude-location.longitude,2))/LocationFactor;
        int r = (int) Math.round(result);
        return ("" +  r);
    }

    public static double locationToDistanceFactor(LatLng location){
        if(location==null)return 0;
        double result = Math.sqrt(Math.pow(CenterLocation.latitude-location.latitude,2)+Math.pow(CenterLocation.longitude-location.longitude,2))/LocationFactor;
        return result;
    }
}
