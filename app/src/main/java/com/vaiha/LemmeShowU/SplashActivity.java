package com.vaiha.LemmeShowU;
/**
 * Created by vaiha on 6/9/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {
Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Thread background = new Thread() {
            public void run() {

                try {
                    //Log.e("thread loop","r");
                    sleep(3 * 900);
                  /*  if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                        Intent i = new Intent(SplashActivity.this, com.vaiha.LemmeShowU.MainActivity4.class);
                        startActivity(i);

                        SharedPreferences prefs = getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
                        String loggedinText = prefs.getString("logged", "");
                        finish();
                    }else{
                        Intent i = new Intent(SplashActivity.this, com.vaiha.LemmeShowU.LemmeShowUPrevious.MainActivity.class);
                        startActivity(i);

                        SharedPreferences prefs = getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
                        String loggedinText = prefs.getString("logged", "");
                        finish();
                    }*/
                    Intent i = new Intent(SplashActivity.this, com.vaiha.LemmeShowU.LemmeShowUPrevious.MainActivity.class);
                    startActivity(i);

                    SharedPreferences prefs = getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
                    String loggedinText = prefs.getString("logged", "");
                    finish();
                    /*if (loggedinText.equals("yes")) {
                        Intent i = new Intent(SplashActivity.this, HomePage.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                        startActivity(i);
                        finish();
                    }*/
                } catch (Exception e) {
                    Toast.makeText(SplashActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        };

        // start thread
        background.start();
    }
}