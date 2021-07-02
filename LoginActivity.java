package com.sislamrafi.corona;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.sislamrafi.corona.account.AccountInfo;
import com.sislamrafi.corona.firebase.FireBaseHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 46445;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);

        Log.d(TAG,"Starting Activity : "+TAG);
        if(account==null) {
            setContentView(R.layout.activity_login);
            signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(this);
            setGoogleSignInButtonText(signInButton, "Sign In With Google");
        }else {
            callNextActivity();
        }
    }

    private void callNextActivity(){
        FirebaseApp.initializeApp(this);
        new AccountInfo(account.getDisplayName(),account.getEmail());
        FireBaseHelper.loginOrCreateAccount(AccountInfo.getId());

        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        myIntent.putExtra("account", account); //Optional parameters
        LoginActivity.this.startActivity(myIntent);
        finish();
    }

    protected void setGoogleSignInButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"Catch a click!");
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                Log.d(TAG,"Sign In");
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            //account = GoogleSignIn.getLastSignedInAccount(this);
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show();
            this.account = account;
            callNextActivity();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
}
