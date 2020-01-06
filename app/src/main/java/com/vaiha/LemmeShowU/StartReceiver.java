package com.vaiha.LemmeShowU;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by vaiha on 6/10/16.
 */

public class StartReceiver extends BroadcastReceiver {
    NotificationManager nmang;
    NotificationManager mNotificationManager;
    Notification notification;
    Context context;
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String action = (String) intent.getExtras().get("DO");

        //before movie removed

        /*if (action.equals("start")) {
            if (SupportClass.timer != null) {
                SupportClass.g_flag = true;
                Notification();
            }
        }
        else if (action.equals("movie")) {
            SupportClass.movie=true;
            SupportClass.g_flag=false;
            Notification();
        }
        else if (action.equals("exit")) {
            SupportClass.g_notificationManager.cancelAll();
            SupportClass.close = true;

        }*/
        /******************************************/

        //after movie removed

        sharedPreferences = context.getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sharedPreferences.getString("capture_mode", "Video");


        if (action.equals("start")) {

            if (mode.equals("Video")) {
                SupportClass.movie = true;
                SupportClass.g_flag = false;
                Notification();
            } else {
                if (SupportClass.timer != null) {
                    SupportClass.g_flag = true;
                    Notification();
                }
            }
        } else if (action.equals("exit")) {
            SupportClass.g_notificationManager.cancelAll();
            SupportClass.close = true;

        }
        /******************************************/

    }

    public void Notification() {

        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int icon;
        icon = R.drawable.animationfile;
        long when = System.currentTimeMillis();
        notification = new Notification(icon, "LemmeShowU", when);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.stop_notification);
        setListeners(contentView);
        notification.contentView = contentView;
        SupportClass.g_notificationManager = mNotificationManager;

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the framerate
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        //notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        notification.defaults |= 0; // Sound

        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        mNotificationManager.notify(0, notification);
    }

    public void setListeners(RemoteViews view) {
        //listener 1
        Intent stop = new Intent(context, StopReceiver.class);
        stop.putExtra("DO", "stop");
        stop.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        stop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn1 = PendingIntent.getBroadcast(context, 0, stop, 0);
        notification.contentIntent = btn1;
        view.setOnClickPendingIntent(R.id.stopLayout, btn1);

        //listener 2
        Intent exit = new Intent(context, StopReceiver.class);
        exit.putExtra("DO", "exit");
        exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn2 = PendingIntent.getBroadcast(context, 1, exit, 0);
        notification.contentIntent = btn2;
        view.setOnClickPendingIntent(R.id.exitLayout, btn2);
    }
}
