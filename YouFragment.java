package com.sislamrafi.corona.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.sislamrafi.corona.LoginActivity;
import com.sislamrafi.corona.MainActivity;
import com.sislamrafi.corona.R;
import com.sislamrafi.corona.account.AccountInfo;
import com.sislamrafi.corona.firebase.FireBaseHelper;
import com.sislamrafi.corona.firebase.FirebaseHelperListener;
import com.sislamrafi.corona.firebase.NotificationPanel;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YouFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String FRAGMENT_TAG = "YOU_FRAGMENT";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "YOU_FRAGMENT";

    private GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView userName;
    private TextView gmailId;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RadioGroup familyGroup;
    private RadioGroup neighbourGroup;

    private RadioGroup sanitGroup;
    private RadioGroup foodGroup;


    private TextView title;
    private TextView body;

    private View RootView;

    public YouFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YouFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YouFragment newInstance(String param1, String param2) {
        YouFragment fragment = new YouFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "Location to id = " + FireBaseHelper.locationToID(new LatLng(23.7262, 90.3951)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RootView = inflater.inflate(R.layout.fragment_you, container, false);

        userName = RootView.findViewById(R.id.name_text);
        gmailId = RootView.findViewById(R.id.gmail_text);

        title = RootView.findViewById(R.id.NFtitle_panel);
        body = RootView.findViewById(R.id.NFtext_panel);

        userName.setText(account.getDisplayName());
        gmailId.setText(account.getEmail());
        RootView.findViewById(R.id.logout_button).setOnClickListener(this);
        RootView.findViewById(R.id.Submit).setOnClickListener(this);
        RootView.findViewById(R.id.Submit1).setOnClickListener(this);

        familyGroup = RootView.findViewById(R.id.family_group);
        neighbourGroup = RootView.findViewById(R.id.neighbour_group);

        sanitGroup = RootView.findViewById(R.id.sanitizer_group);
        foodGroup = RootView.findViewById(R.id.food_group);

        updateFragment();
        return RootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_button: mGoogleSignInClient.signOut();
                callNextActivity();
                break;
            case R.id.Submit: Submit();
                break;
            case R.id.Submit1: Submit1();
                break;
        }
    }

    private void Submit(){
        int statusF = 0;
        int selectedIdF = familyGroup.getCheckedRadioButtonId();
        switch (selectedIdF){
            case R.id.radioButtonSick: statusF = 1;
                break;
            case R.id.radioButtonSymptom: statusF = 2;
                break;
            case R.id.radioButtonAffected: statusF = 3;
                break;
            case R.id.radioButtonFine: statusF = 0;
                break;
        }
        Log.d(TAG,"Submit Family : "+statusF);

        int statusN = 0;
        int selectedIdN = neighbourGroup.getCheckedRadioButtonId();
        switch (selectedIdN){
            case R.id.radioButtonSick1: statusN = 1;
                break;
            case R.id.radioButtonSymptom1:statusN = 2;
                break;
            case R.id.radioButtonAffected1: statusN = 3;
                break;
            case R.id.radioButtonFine1: statusN = 0;
                break;
        }
        Log.d(TAG,"Submit Neighbour : "+statusN);

        LatLng nn = AccountInfo.getLastUserlocation();
        if(nn == null){
            Toast.makeText(this.getActivity(),"Location not set yet\nSubmit unsuccessful",Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG,nn.toString());
        FireBaseHelper.update(statusF,statusN,nn);
        FireBaseHelper.updateToLocation(AccountInfo.getId(),nn);
        FireBaseHelper.getMarkerLocations(AccountInfo.getLastUserlocation());

        Toast.makeText(this.getActivity(),"Submitted Successfully",Toast.LENGTH_SHORT).show();
    }

    private void Submit1(){
        int statusS = 0;
        int selectedIdS = sanitGroup.getCheckedRadioButtonId();
        switch (selectedIdS){
            case R.id.radioButtonSanitYes: statusS = 1;
                break;
            case R.id.radioButtonSanitNo: statusS = 2;
                break;
        }
        Log.d(TAG,"Submit Sanit : "+statusS);

        int statusF = 0;
        int selectedIdF = foodGroup.getCheckedRadioButtonId();
        switch (selectedIdF){
            case R.id.radioButtonFoodYes: statusF = 1;
                break;
            case R.id.radioButtonFoodNo:statusF = 2;
                break;
        }
        Log.d(TAG,"Submit Neighbour : "+statusF);

        LatLng nn = AccountInfo.getLastUserlocation();
        if(nn == null){
            Toast.makeText(this.getActivity(),"Location not set yet\nSubmit unsuccessful",Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG,nn.toString());
        FireBaseHelper.updateNeeds(statusS,statusF,nn);
        FireBaseHelper.updateToLocation(AccountInfo.getId(),nn);
        FireBaseHelper.getMarkerLocations(AccountInfo.getLastUserlocation());

        Toast.makeText(this.getActivity(),"Submitted Successfully",Toast.LENGTH_SHORT).show();
    }

    private void callNextActivity(){
        Toast.makeText(getActivity(),"Logout Successful",Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(getActivity(), LoginActivity.class);
        myIntent.putExtra("account", account); //Optional parameters
        startActivity(myIntent);
        getActivity().finish();
    }

    public void updateFragment(){
        NotificationPanel panel = AccountInfo.getNotificationPanel();
        if(panel == null)return;
        if(panel.getToast() == 0){
            RootView.findViewById(R.id.NFPanel).setVisibility(View.GONE);
        }else {
            RootView.findViewById(R.id.NFPanel).setVisibility(View.VISIBLE);
            title.setText(panel.getTitle());
            body.setText(panel.getBody());
        }
    }
}
