package com.vaiha.LemmeShowU.signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.RequestParams;
import com.vaiha.LemmeShowU.MainActivity;
import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.SupportClass;
import com.vaiha.LemmeShowU.Util;
import com.vaiha.LemmeShowU.socketConnection.HomePage;
import com.vaiha.LemmeShowU.utilities.Api;

import org.json.JSONObject;

/**
 * Created by vaiha on 21/11/16.
 */

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    public static GoogleApiClient mGoogleApiClient;
    RequestQueue queue;
    Button welcomeGoogleSignUpButton, welcomeSignUpButton, welcomeSignInButton;
    SharedPreferences sharedpreference;
    boolean isInternetPresent;
    Util util;
    private ProgressDialog mProgressDialog;
    private TextView txtName, txtEmail;

    public static void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        welcomeGoogleSignUpButton = (Button) findViewById(R.id.welcomeGoogleSignUpButton);
        welcomeSignUpButton = (Button) findViewById(R.id.welcomeSignUpButton);
        welcomeSignInButton = (Button) findViewById(R.id.welcomeSignInButton);
        util = new Util(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        welcomeGoogleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetPresent = util.isConnectingToInternet()) {
                    signIn();
                } else {
                    Toast.makeText(WelcomeActivity.this, "Check Your Internet Connection...!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        welcomeSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
        welcomeSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LogInActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

   /* private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }*/

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());
            ;
            String email = acct.getEmail();
            Log.e("eee", "" + email);
            String personName = removeLastChar(email);

            emailRegister(personName, email);

            /*txtName.setText(personName);

            *//****** Connecton Type Finding*****//*
            ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            //mobile
            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();

            Log.d("divider","----------------");
            //wifi
            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                //mobile
                //txtEmail.setText("mobile");
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("wifiInfo", wifiInfo.toString());
                Log.d("Network State : ","mobile");
                Log.d("SSID : ",wifiInfo.getSSID());
            } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                //wifi
               // txtEmail.setText("wifi");
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("wifiInfo", wifiInfo.toString());
                Log.d("Network State : ","wifi");
                Log.d("SSID",wifiInfo.getSSID());
            }
            *//****** Connecton Type Finding*****//*
            String model        = Build.MODEL;
            Log.d("Device Model : ",""+model);
            String version= Util.getAndroidVersion();
            Log.d("Android version : ",""+version);
            String ip = Util.getIPAddress(true);
            Log.d("ip",""+ip);
            //txtEmail.setText(ip);
            //txtEmail.setText(email);
            // Long tsLong = System.currentTimeMillis()/1000;

            //String ts = Util.getCurrentTimeStamp();
            //String ts = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
            // long ts = System.currentTimeMillis() / 1000L;
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            Log.d("Time Stamp : ",""+ts);
            Log.d("divider","----------------");*/

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void emailRegister(final String name, final String email) {

        RequestParams param = new RequestParams();
        param.put("user_name", name);
        param.put("email", email);
        String api = Api.email_register + param;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("wel-res", "" + response);
                        if (response != null) {

                            try {
                                String status = response.getString("status");

                                if (status.equals("success")) {
                                    String user_id = response.getString("user_id");
                                    sharedpreference = getApplicationContext().getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreference.edit();
                                    editor.putString("logged", "yes");
                                    editor.putString("user_id", user_id);
                                    editor.putString("user_name", name);
                                    editor.putString("email", email);
                                    editor.putString("email_login", "yes");
                                    editor.apply();
                                    Toast.makeText(WelcomeActivity.this, "Successfully Logged in...!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(WelcomeActivity.this, HomePage.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(WelcomeActivity.this, "failed...!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                Log.e("Exeption", "" + ex);
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Errrror: ", "" + error.getMessage());
            }
        });
        jsonObjReq.setShouldCache(false);
        queue = Volley.newRequestQueue(this);
        queue.add(jsonObjReq);
    }
}
