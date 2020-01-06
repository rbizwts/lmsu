package com.vaiha.LemmeShowU;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by a on 8/9/2016.
 */
public class FrameRate extends Activity {

    NotificationManager mNotificationManager;
    Notification notification;
    NotificationManager nmang;
    Spinner menu_list_spinner;
    RelativeLayout menu_layout;
    ArrayList<String> menu_list = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_rate);

        menu_list_spinner = (Spinner) findViewById(R.id.spinner_menu);
        menu_layout = (RelativeLayout) findViewById(R.id.spinnerLayout);
        Button waitButton = (Button) findViewById(R.id.waitButton);

        String action = (String) getIntent().getExtras().get("DO");
        if (action.equals("rate")) {
            //Your code
            start_Notification();
            try {
                selectItem();
            } catch (NullPointerException e) {
                e.getStackTrace();
            }
        }

        waitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizeApp();
            }
        });
    }

    public void selectItem() {


        menu_list.add("0.25F");
        menu_list.add("0.5F");
        menu_list.add("1F");
        menu_list.add("2F");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, menu_list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        menu_list_spinner.setAdapter(dataAdapter);
        String temp;
        if (SupportClass.g_frames < 1) {
            temp = SupportClass.g_frames + "F";
        } else {
            temp = (int) SupportClass.g_frames + "F";
        }

        Log.e("iiii", "" + SupportClass.g_frames);

        for (int i = 0; i < menu_list.size(); i++) {
            if (menu_list.get(i).equals(temp)) {
                menu_list_spinner.setSelection(i);
                break;
            }
        }

        menu_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        SupportClass.g_frames = (double) 0.25;

                        break;

                    case 1:

                        SupportClass.g_frames = (double) 0.5;

                        break;

                    case 2:

                        SupportClass.g_frames = 1;

                        break;

                    case 3:

                        SupportClass.g_frames = 2;

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

    }

    public void start_Notification() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.icon;
        //int icon = R.drawable.animationfile;
        long when = System.currentTimeMillis();

        notification = new Notification(icon, "LemmeShowU", when);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.start_notification);

        contentView.setImageViewResource(R.id.image11, R.drawable.icon);
        contentView.setImageViewResource(R.id.image21, R.mipmap.play);
        contentView.setImageViewResource(R.id.image, R.mipmap.movie);
        contentView.setImageViewResource(R.id.image31, R.mipmap.frames);
        contentView.setImageViewResource(R.id.image41, R.mipmap.exit);
        contentView.setTextViewText(R.id.stop, "Start");
        // contentView.setTextViewText(R.id.movie, "Movie");
        contentView.setTextViewText(R.id.fps, "FPS");
        contentView.setTextViewText(R.id.exit, "Exit");

        setListeners(contentView);
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sharedPreferences.getString("capture_mode", "Video");
        if (mode.equals("Video")) {
            contentView.setViewVisibility(R.id.fpsLayout, View.GONE);
            contentView.setViewVisibility(R.id.mpLayout, View.VISIBLE);
        } else {
            contentView.setViewVisibility(R.id.fpsLayout, View.VISIBLE);
            contentView.setViewVisibility(R.id.mpLayout, View.GONE);
        }

        notification.contentView = contentView;

        SupportClass.g_notificationManager = mNotificationManager;

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the framerate
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        //notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        mNotificationManager.notify(0, notification);
        SupportClass.g_flag = false;
    }

    public void setListeners(RemoteViews view) {
        //listener 1
        Intent start = new Intent(this, StartReceiver.class);
        start.putExtra("DO", "start");
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn1 = PendingIntent.getBroadcast(this, 0, start, 0);
        notification.contentIntent = btn1;
        view.setOnClickPendingIntent(R.id.startLayout, btn1);

        //listener 2
        Intent rate1 = new Intent(this, StartReceiver.class);
        rate1.putExtra("DO", "movie");
        rate1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rate1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn2 = PendingIntent.getBroadcast(this, 1, rate1, 0);
        notification.contentIntent = btn2;
        view.setOnClickPendingIntent(R.id.movieLayout, btn2);

        //listener 2
        Intent rate = new Intent(this, FrameRate.class);
        rate.putExtra("DO", "rate");
        rate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn3 = PendingIntent.getActivity(this, 1, rate, 0);
        notification.contentIntent = btn3;
        view.setOnClickPendingIntent(R.id.fpsLayout, btn3);

        //listener 3
        Intent exit = new Intent(this, StopReceiver.class);
        exit.putExtra("DO", "exit");
        PendingIntent btn4 = PendingIntent.getBroadcast(this, 1, exit, 0);
        notification.contentIntent = btn4;
        view.setOnClickPendingIntent(R.id.exitLayout, btn4);

    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(this, Setting.class);
        startActivity(intent1);
    }
}
