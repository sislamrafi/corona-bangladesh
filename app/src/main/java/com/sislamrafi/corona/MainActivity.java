package com.sislamrafi.corona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sislamrafi.corona.account.AccountInfo;
import com.sislamrafi.corona.account.AccountSaver;
import com.sislamrafi.corona.account.LocationChangeListener;
import com.sislamrafi.corona.firebase.Covid19;
import com.sislamrafi.corona.firebase.FireBaseHelper;
import com.sislamrafi.corona.firebase.FirebaseHelperListener;
import com.sislamrafi.corona.firebase.LocationStructure;
import com.sislamrafi.corona.firebase.NotificationPanel;
import com.sislamrafi.corona.fragments.MapFragment;
import com.sislamrafi.corona.fragments.StatisticFragment;
import com.sislamrafi.corona.fragments.YouFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, FirebaseHelperListener, LocationChangeListener {

    private static final int UPDATE_VERSION = 2;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 54386;
    private final static int REQUEST_CHECK_SETTINGS = 2560;

    private GoogleSignInAccount account;
    private int fragmentNow = 0;

    private MapFragment mapFragment;
    private StatisticFragment statisticFragment;
    private YouFragment youFragment;
    private Fragment currentfragment;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    protected LocationManager locationManager;

    private SharedPreferences mPrefs;

    private long startTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            //get update
            Log.d(TAG,"Timer Called");
            FireBaseHelper.getNotificationPanel();
            FireBaseHelper.getCovid19Update(FireBaseHelper.BANGLADESH);
            FireBaseHelper.getCovid19Update(FireBaseHelper.WORLD);
            timerHandler.postDelayed(this, 60000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = GoogleSignIn.getLastSignedInAccount(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mPrefs = this.getPreferences(MODE_PRIVATE);

        Log.d(TAG,account.getDisplayName());
        initFragments();
        getAllsavedDatas();
        checkPermissions();
        setOnClickListenerX();
        FireBaseHelper.setListenerX(this);
        AccountInfo.setListenerX(this);
        timerHandler.postDelayed(timerRunnable, 0);
        //update map here
        FireBaseHelper.getMarkerLocations(AccountInfo.getLastUserlocation());
        //check day to call you fragment;
        if(getTheDayGap()!=0&&AccountInfo.getLastUserlocation()!=null){
            clickOnYouNav();
        }
        saveTheday();
    }

    @SuppressLint("MissingPermission")
    private void setLocationListenerX(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
    }

    private void initFragments(){
        mapFragment = new MapFragment();
        statisticFragment = new StatisticFragment();
        youFragment = new YouFragment();
        int container_id = R.id.frame_layout;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(container_id, youFragment, "3").hide(youFragment).commit();
        fragmentManager.beginTransaction().add(container_id, statisticFragment, "2").hide(statisticFragment).commit();
        fragmentManager.beginTransaction().add(container_id, mapFragment, "1").commit();
        currentfragment = mapFragment;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,v.getId()+"");
        switch (v.getId()) {
            case R.id.nav_map:
                clickOnMapNav();
                break;
            case R.id.nav_statistics:
                clickOnStatNav();
                break;
            case R.id.nav_you:
                clickOnYouNav();
                break;
            // ...
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveAllDatas();
        Log.d(TAG,"APP IS CLOSING");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveAllDatas();
        Log.d(TAG,"APP IS Pausing");
    }

    private void clickOnYouNav() {
        LinearLayout mL , sL, yL;
        mL = findViewById(R.id.maps_bar);
        sL = findViewById(R.id.statistics_bar);
        yL = findViewById(R.id.you_bar);
        mL.setVisibility(View.GONE);
        sL.setVisibility(View.GONE);
        yL.setVisibility(View.VISIBLE);

        if(fragmentNow!=2){
            fragmentManager.beginTransaction().
                    setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).
                    hide(currentfragment).show(youFragment)
                    .commit();
            currentfragment = youFragment;
        }
        fragmentNow = 2;
    }

    private void clickOnStatNav() {
        LinearLayout mL , sL, yL;
        mL = findViewById(R.id.maps_bar);
        sL = findViewById(R.id.statistics_bar);
        yL = findViewById(R.id.you_bar);
        mL.setVisibility(View.GONE);
        yL.setVisibility(View.GONE);
        sL.setVisibility(View.VISIBLE);

        if(fragmentNow!=1){
            if(fragmentNow == 0)
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).
                        hide(currentfragment)
                        .show(statisticFragment)
                        .commit();
            else if(fragmentNow == 2)
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).
                        hide(currentfragment)
                        .show(statisticFragment)
                        .commit();
            currentfragment = statisticFragment;
        }
        fragmentNow = 1;
    }

    private void clickOnMapNav() {
        LinearLayout mL , sL, yL;
        mL = findViewById(R.id.maps_bar);
        sL = findViewById(R.id.statistics_bar);
        yL = findViewById(R.id.you_bar);
        sL.setVisibility(View.GONE);
        yL.setVisibility(View.GONE);
        mL.setVisibility(View.VISIBLE);

        //fragmentTransaction = fragmentManager.beginTransaction();

        if(fragmentNow!=0){
            fragmentManager.beginTransaction().
                    setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).
                    hide(currentfragment).show(mapFragment).
                    commit();
            currentfragment = mapFragment;
        }
        fragmentNow = 0;
    }

    public void setOnClickListenerX(){
        this.findViewById(R.id.nav_map).setOnClickListener(this);
        this.findViewById(R.id.nav_statistics).setOnClickListener(this);
        this.findViewById(R.id.nav_you).setOnClickListener(this);
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            //getMyLocation();
            createLocationRequest();
            setLocationListenerX();
            //dataUpdateSchedule();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            //getMyLocation();
            createLocationRequest();
            setLocationListenerX();
        }else{
            Toast.makeText(this,"APP will not work without location permission",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG,"Location service : addOnFailureListener");
                //Toast.makeText(MainActivity.this, "addOnSuccessListener", Toast.LENGTH_SHORT).show();
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                toastHealthMsg();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(MainActivity.this, "addOnFailureListener", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Location service : addOnFailureListener");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    public void toastHealthMsg(){
        if(currentfragment == youFragment) Toast.makeText(this,"Please submit your health report",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"In Decision");
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                toastHealthMsg();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //getMyLocation();
                        Log.d(TAG,"get ok ..........................");
                        break;
                    case Activity.RESULT_CANCELED:
                        //finish();
                        Log.d(TAG,"Cancelled .........................");
                        break;
                }
                break;
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //getMyLocation();
                        Log.d(TAG,"get ok");
                        break;
                    case Activity.RESULT_CANCELED:
                        //finish();
                        Log.d(TAG,"Cancelled");
                        break;
                }
                break;
        }
    }

    public LatLng getLastBestLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        AccountInfo.setLastGpslocation(new LatLng(location.getLatitude(),location.getLongitude()));
        Log.d(TAG,"Provider : "+location.getProvider()+" Lat : "+location.getLatitude()+"\t Lan : "+location.getLongitude());
    }

    ///////////////////////////////

    private void saveAllDatas(){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        AccountSaver accountSaver = new AccountSaver();
        String json = gson.toJson(accountSaver);
        prefsEditor.putString("AccountInfo", json).commit();
        Log.d(TAG,json);
        saveMarkersToPref();
    }

    private void saveMarkersToPref(){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json;
        if(FireBaseHelper.allLocations!=null)
            json = gson.toJson(FireBaseHelper.allLocations);
        else
            json = gson.toJson(MapFragment.backUpMarkers);
        prefsEditor.remove("Locations").commit();
        prefsEditor.putString("Locations", json);
        prefsEditor.commit();
    }

    private void saveTheday(){
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK); // Or other Calendar value

        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("DAY_NO", day);
        editor.commit(); // commit is important here.
    }

    private int getTheDayGap(){
        Calendar c = Calendar.getInstance();
        int day = mPrefs.getInt("DAY_NO",-100);
        int today = c.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG,today+" DAY NO ");
        return today-day;
    }

    private void getAllsavedDatas(){
        Gson gson = new Gson();
        String json = mPrefs.getString("AccountInfo", null);
        //Log.d(TAG,json);
        AccountSaver accountSaver = gson.fromJson(json, AccountSaver.class);
        if(accountSaver!=null){
            accountSaver.getSavedData();
            Log.d(TAG,"Saved data is not null");
        }
        //Log.d(TAG,AccountInfo.getBangladesh().getActive()+"");
        getsavedMarkers();
    }

    private void getsavedMarkers(){
        Gson gson = new Gson();
        String response=mPrefs.getString("Locations" , "");
        MapFragment.backUpMarkers = gson.fromJson(response,
                new TypeToken<List<LocationStructure>>(){}.getType());
        //FireBaseHelper.allLocations = backUpMarkers;
        if(MapFragment.backUpMarkers==null)return;
        Log.d(TAG,"SIZE RAW : "+MapFragment.backUpMarkers);
        Log.d(TAG,"SIZE RAW : "+MapFragment.backUpMarkers);
        //Log.d(TAG,"SIZE RAW : "+FireBaseHelper.allLocations.size());
    }

    ////////////////////////////

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDataReady(DataSnapshot dataSnapshot, int type) {
        if(type == FireBaseHelper.BANGLADESH){
            Covid19 BD = dataSnapshot.getValue(Covid19.class);
            AccountInfo.setBangladesh(BD);
            mapFragment.updateBarState();
            statisticFragment.updateFragment();
            Log.d(TAG,"Load Bd update...");
        }
        if(type == FireBaseHelper.WORLD){
            Covid19 World = dataSnapshot.getValue(Covid19.class);
            AccountInfo.setWorld(World);
            statisticFragment.updateFragment();
            Log.d(TAG,"Load world update...");
        }
        if(type == FireBaseHelper.MAP){
            mapFragment.updateFragment();
            Log.d(TAG,"Load map geo locations update...");
        }
        if(type == FireBaseHelper.NOTIFICATION){
            NotificationPanel panel = dataSnapshot.getValue(NotificationPanel.class);
            AccountInfo.setNotificationPanel(panel);
            youFragment.updateFragment();
            Log.d(TAG,"Load notification update...");
        }
    }

    @Override
    public void onChangeGpsLocation(LatLng location) {

    }

    @Override
    public void onChangeUserLocation(LatLng location) {
        mapFragment.updateMarker(location);
        FireBaseHelper.getMarkerLocations(location);
    }
}
