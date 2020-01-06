package com.vaiha.LemmeShowU.socketConnection;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.RequestParams;
import com.vaiha.LemmeShowU.FrameRate;
import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.Setting;
import com.vaiha.LemmeShowU.SettingsActivity;
import com.vaiha.LemmeShowU.StartReceiver;
import com.vaiha.LemmeShowU.StopReceiver;
import com.vaiha.LemmeShowU.SupportClass;
import com.vaiha.LemmeShowU.Util;
import com.vaiha.LemmeShowU.signup.WelcomeActivity;
import com.vaiha.LemmeShowU.utilities.Api;

import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

public class HomePage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    //public TextView tvServerIP;
    private final int SERVER_PORT = 8080;
    ImageView image;
    Bitmap bitmapimage, temp;
    Timer imgTimer = new Timer();
    Util util;
    String user_id, ip_addr = "", user_name, email, email_login, req_by;
    RequestQueue queue;
    SharedPreferences sharedpreference;
    boolean isInternetPresent;
    private TextView tvServerPort;
    private GoogleApiClient mGoogleApiClient;
    public static Timer ip_update_timer = null;
    boolean popup = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        util = new Util(getApplicationContext());
        isInternetPresent = util.isConnectingToInternet();
        image = (ImageView) findViewById(R.id.image);
        tvServerPort = (TextView) findViewById(R.id.textViewServerPort);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        sharedpreference = getApplicationContext().getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
        user_id = sharedpreference.getString("user_id", "");
        user_name = sharedpreference.getString("user_name", "");
        email_login = sharedpreference.getString("email_login", "");
        email = sharedpreference.getString("email", "");

       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent modifySettings = new Intent(HomePage.this, SettingsActivity.class);
                startActivity(modifySettings);
            }
        });*/

        /*********************************/
        //shared preference value from setting page
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        StringBuilder builder = new StringBuilder();

        builder.append("\n" + "sharing:\t" + sharedPrefs.getBoolean("sharing_sync", false));
        builder.append("\n" + "Sync Intervals:\t" + sharedPrefs.getString("sync_interval", "-1"));
        builder.append("\n" + "Capture Mode:\t" + sharedPrefs.getString("sync_mode", "Video"));
        builder.append("\n" + "Language:\t" + sharedPrefs.getString("sync_language", "English"));
        builder.append("\n" + "Customized Notification Ringtone:\t" + sharedPrefs.getString("notification_ringtone", ""));
        //Log.e("Lemme-pref", "" + builder.toString());
        /************************************/

        tvServerPort.setText(user_name);

        Button find_partner = (Button) findViewById(R.id.find_partner);

        find_partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetPresent = util.isConnectingToInternet()) {
                    Intent i = new Intent(HomePage.this, Sharing.class);
                    startActivity(i);
                    //finish();
                } else {
                    Toast.makeText(HomePage.this, "Check Your Internet Connection...!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //ip update when ip get differ
        ip_update_timer = new Timer();
        ip_update_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Log.e("Ip-TIMER","run"+ip_update_timer);
                isInternetPresent = util.isConnectingToInternet();
                String ip = getDeviceIpAddress();

                if (isInternetPresent) {
                    if (!popup) {
                        request_check();
                    }

                    if (ip_addr.equals("")) {
                        ip_addr = ip;
                        updateDeviceIp();
                    }
                    if (!ip_addr.equals(ip)) {
                        ip_addr = ip;
                        updateDeviceIp();
                    }

                } else {
                    ip_addr = "";
                    Log.e("Connection", "Internet Not Connected");
                }
            }
        },0,1000);
        /*************************************/
        /*if (SupportClass.imgTimer == null) {
            imgTimer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    if (isInternetPresent = util.isConnectingToInternet()) {
                        //do your thing

                        runOnUiThread(new Runnable() {
                            public void run() {

                                isInternetPresent = util.isConnectingToInternet();
                                String ip = getDeviceIpAddress();
                                //Toast.makeText(HomePage.this, ""+ip, Toast.LENGTH_SHORT).show();

                                if (isInternetPresent) {
                                    if (!SupportClass.popup) {
                                        request_check();
                                    }

                                    if (ip_addr.equals("")) {
                                        ip_addr = ip;
                                        update();
                                    }
                                    if (!ip_addr.equals(ip)) {
                                        Log.e("sssssss", "" + ip);
                                        ip_addr = ip;
                                        update();
                                    }
                                    // image.setImageBitmap(bitmapimage);

                                } else {
                                    ip_addr = "";
                                    //update();
                                    Log.e("sss", "Internet Not Connected");
                                }
                            }
                        });
                        //Called each time when 1000 milliseconds (1 second) (the period parameter)
                        // receiveMyMessage();
                    }
                }
            }, 0, 1000);
        }
        SupportClass.imgTimer = imgTimer;*/
    }

    /**
     * Get ip address of the device
     */
    public String getDeviceIpAddress() {
        String ip = null;
        try {

            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = enumeration.nextElement();
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface
                        .getInetAddresses(); enumerationIpAddr
                             .hasMoreElements(); ) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress.getAddress().length == 4) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
        return ip;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       *//* if (id == R.id.action_settings) {
           *//**//* Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
            return true;*//**//*
        }*//*
        if (id == R.id.logout) {
            imgTimer.cancel();
            if (email_login.equals("yes")) {

                SharedPreferences.Editor editor = sharedpreference.edit();
                editor.putString("logged", "no");
                editor.clear();
                editor.apply();
                signOut();

            } else {
                SharedPreferences.Editor editor = sharedpreference.edit();
                editor.putString("logged", "no");
                editor.clear();
                editor.apply();

                Intent i = new Intent(HomePage.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void updateDeviceIp() {
        RequestParams param = new RequestParams();
        param.put("user_id", user_id);
        param.put("ip_address", ip_addr);
        String api = Api.update_ip + param;

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("home-res", "" + response);
                            if (response != null) {

                                try {
                                    String status = response.getString("status");
                                    if (status.equals("success")) {
                                        //Log.e("ssss", "updated");
                                    } else {
                                        Toast.makeText(HomePage.this, "Failed...!", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
            Log.e("Exeption", "" + e);
        }
    }


    public void request_check() {
        RequestParams param = new RequestParams();
        param.put("user_id", user_id);
        String api = Api.request_check + param;

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("home-res", "" + response);
                            if (response != null) {

                                try {
                                    String msg = response.getString("msg");
                                    if (msg.equals("success")) {
                                        String req_status = response.getString("req_status");
                                        if (req_status.equals("1")) {
                                            popup = true;
                                            popup();
                                        }
                                    } else {
                                        Log.e("Exeption", "fail");
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
        } catch (Exception e) {
            Log.e("Exeption", "" + e);
        }
    }

    void popup() {

        popup = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomePage.this);
        // Setting Dialog Title
        alertDialog.setTitle("LemmeShowU");
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage("Somebody ready to share their screen to you.Are ou want to allow?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                // SupportClass.imgTimer.cancel();
                String doThis = "accept";
                allow_deny(doThis);

            }
        });


        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("DENY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //  SupportClass.imgTimer.cancel();
                String doThis = "deny";
                allow_deny(doThis);
                Toast.makeText(HomePage.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    public void allow_deny(String doThis) {

        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(HomePage.this);
        pDialog.setMessage("loading");
        pDialog.show();
        RequestParams param = new RequestParams();
        param.put("id", user_id);
        param.put("action", doThis);
        String api = Api.request + param;

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("ho,e-res", "" + response);
                            pDialog.hide();
                            if (response != null) {

                                try {
                                    String status = response.getString("status");
                                    String msg = response.getString("msg");
                                    if (status.equals("success") && msg.equals("accept")) {
                                        popup = true;
                                        Intent i = new Intent(HomePage.this, Receive.class);
                                        startActivity(i);
                                        finish();
                                    } else if (status.equals("success") && msg.equals("deny")) {
                                        popup = false;
                                        return;
                                    } else {
                                        Log.e("Exeption", "fail");
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
                    pDialog.hide();
                }
            });
            jsonObjReq.setShouldCache(false);
            queue = Volley.newRequestQueue(this);
            queue.add(jsonObjReq);
        } catch (Exception e) {
            Log.e("Exeption", "" + e);
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e("res", "logout");
                        Intent i = new Intent(HomePage.this, WelcomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
