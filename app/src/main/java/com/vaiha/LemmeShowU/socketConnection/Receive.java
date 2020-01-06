package com.vaiha.LemmeShowU.socketConnection;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.RequestParams;
import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.SupportClass;
import com.vaiha.LemmeShowU.Util;
import com.vaiha.LemmeShowU.utilities.Api;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

/**
 * Created by vaiha on 22/11/16.
 */

public class Receive extends Activity {
    public static GoogleApiClient mGoogleApiClient;
    private final int SERVER_PORT = 8080;
    ImageView image;
    TextView txt;
    Button disconnect;
    Bitmap bitmapimage, temp;
    Timer t = new Timer();
    Socket mySocket;
    Util util;
    String user_id, ip_addr = "", user_name, email, email_login;
    RequestQueue queue;
    SharedPreferences sharedpreference;
    boolean isInternetPresent;
    private TextView tvServerIP, tvServerPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_layout);
        image = (ImageView) findViewById(R.id.image);
        txt = (TextView) findViewById(R.id.txt);
        disconnect = (Button) findViewById(R.id.denyBtn);
        sharedpreference = getApplicationContext().getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
        user_id = sharedpreference.getString("user_id", "");
        user_name = sharedpreference.getString("user_name", "");
        email_login = sharedpreference.getString("email_login", "");
        email = sharedpreference.getString("email", "");

        // Button logout = (Button) findViewById(R.id.btn_sign_out);
        //receive = (Button) findViewById(R.id.receive);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allow_deny();

            }
        });

        //Receiving image through socket
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ServerSocket socServer = new ServerSocket(SERVER_PORT);
                    Socket socClient = null;
                    while (true) {
                        socClient = socServer.accept();
                        ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
                        serverAsyncTask.execute(new Socket[]{socClient});

                    }

                } catch (IOException e) {
                    Log.e("android-ex",""+e);
                }
            }
        }).start();

        /****** Connecton Type Finding*****//*
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

    }

    public void allow_deny() {

        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(Receive.this);
        pDialog.setMessage("loading");
        pDialog.show();
        RequestParams param = new RequestParams();
        param.put("id", user_id);
        param.put("action", "deny");
        String api = Api.request + param;

        try {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("Rec-res", "" + response);
                            pDialog.hide();
                            if (response != null) {

                                try {
                                    String status = response.getString("status");
                                    String msg = response.getString("msg");
                                   // SupportClass.imgTimer.cancel();
                                    if (status.equals("success") && msg.equals("deny")) {
                                        HomePage.ip_update_timer.cancel();
                                        Intent i = new Intent(Receive.this, HomePage.class);
                                        startActivity(i);
                                        finish();
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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mySocket != null)
                mySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * AsyncTask which handles the commiunication with clients
     */
    class ServerAsyncTask extends AsyncTask<Socket, Void, String> {


        @Override
        protected String doInBackground(Socket... params) {
            String result = null;
            mySocket = params[0];


            /******first read image*********/
            int filesize = 6022386; // filesize temporary hardcoded
            long start = System.currentTimeMillis();
            int bytesRead;
            int current = 0;

            String fname = "share_img.jpg";
            String fol = "/storage/emulated/0/LemmeShowU/Received";
            File f = new File(fol);
            if (!f.exists()) {
                f.mkdirs();
            }
            File myDir = new File("/storage/emulated/0/LemmeShowU/Received");
            File file = new File(myDir, fname);
            // home_layout file
            byte[] mybytearray = new byte[filesize];

            try {
                InputStream is = mySocket.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bytesRead = is.read(mybytearray, 0, mybytearray.length);
                current = bytesRead;
                do {
                    bytesRead =
                            is.read(mybytearray, current, (mybytearray.length - current));
                    if (bytesRead >= 0) current += bytesRead;
                } while (bytesRead > -1);
                //Log.e("execpt", "" + bytesRead);
                // count = current;
                //Copy = mybytearray.clone();
                bos.write(mybytearray, 0, current);
                bos.flush();
                bitmapimage = BitmapFactory.decodeByteArray(mybytearray, 0, mybytearray.length);
                Log.e("IIIII", "image received....");
                long end = System.currentTimeMillis();
                System.out.println(end - start);
                bos.close();
                fos.close();
                mySocket.close();

            } catch (Exception e) {
                Log.e("execpt", "" + e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (txt.getVisibility() == View.VISIBLE) {
                txt.setVisibility(View.GONE);
            }
            image.setImageBitmap(bitmapimage);
           /* //image.setImageBitmap(bitmapimage);
            String path = "/storage/emulated/0/LemmeShowU/Received/venki.jpg";
            File imgFile = new File(path);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image.setImageBitmap(myBitmap);
            } else {
                Log.d("Ciaren", "File doesn't exist");
            }*/
        }
    }

    /*void start_process() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
                //request_screencast();

            } else {
                //request_screencast();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
                Log.e("testing", "Permission is revoked");

            }
        } else {

        }
    }*/
}