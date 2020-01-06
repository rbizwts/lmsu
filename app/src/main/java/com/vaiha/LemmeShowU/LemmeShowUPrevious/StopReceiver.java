package com.vaiha.LemmeShowU.LemmeShowUPrevious;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Log;
import android.widget.RemoteViews;


import com.vaiha.LemmeShowU.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by vaiha on 6/10/16.
 */

public class StopReceiver extends BroadcastReceiver {

    NotificationManager mNotificationManager;
    Notification notification;
    Context context;

    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String action = (String) intent.getExtras().get("DO");
        if (action.equals("stop")) {
            if(com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.g_flag) {
                com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.g_flag = false;
                com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.close = false;
                start_Notification();
            }
            if(com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.stopvid) {
                com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.g_flag = false;
                com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.close = false;
                try {
                    if (com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.mediaRecorder != null) {
                        Log.e("MMM",""+com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.mediaRecorder);
                        com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.mediaRecorder.stop();
                        com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.mediaRecorder.reset();
                        com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.mediaRecorder.release();
                    }
                } catch (NullPointerException e) {
                    e.getStackTrace();
                }
                com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.stopvid = false;
                com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.check_recorder_create=true;
                start_Notification();
            }
        }

        else if (action.equals("exit")) {
            com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.g_notificationManager.cancelAll();
            com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.close = true;
        }

    }

    private void start_Notification() {

        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.icon;
        //int icon = R.drawable.animationfile;
        long when = System.currentTimeMillis();

        notification = new Notification(icon, "LemmeShowU", when);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.start_notification);

        contentView.setImageViewResource(R.id.image11, R.drawable.icon);
        contentView.setImageViewResource(R.id.image21, R.mipmap.play);
        contentView.setImageViewResource(R.id.image, R.mipmap.movie);
        contentView.setImageViewResource(R.id.image31, R.mipmap.frames);
        contentView.setImageViewResource(R.id.image41, R.mipmap.exit);
        contentView.setTextViewText(R.id.stop, "Start");
        //contentView.setTextViewText(R.id.movie, "Movie");
        contentView.setTextViewText(R.id.fps, "FPS");
        contentView.setTextViewText(R.id.exit, "Exit");

        setListeners(contentView);
        notification.contentView = contentView;

        com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.g_notificationManager = mNotificationManager;

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the framerate
        notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        mNotificationManager.notify(0, notification);
        com.vaiha.LemmeShowU.LemmeShowUPrevious.SupportClass.g_flag = false;
    }

    public void setListeners(RemoteViews view) {
        //listener 1
        Intent start = new Intent(context, com.vaiha.LemmeShowU.LemmeShowUPrevious.StartReceiver.class);
        start.putExtra("DO", "start");
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn1 = PendingIntent.getBroadcast(context, 0, start, 0);
        notification.contentIntent = btn1;
        view.setOnClickPendingIntent(R.id.startLayout, btn1);

        //listener 2
        Intent rate1 = new Intent(context, com.vaiha.LemmeShowU.LemmeShowUPrevious.StartReceiver.class);
        rate1.putExtra("DO", "movie");
        rate1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rate1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn2 = PendingIntent.getBroadcast(context, 1, rate1, 0);
        notification.contentIntent = btn2;
        view.setOnClickPendingIntent(R.id.movieLayout, btn2);

        //listener 2
        Intent rate = new Intent(context, com.vaiha.LemmeShowU.LemmeShowUPrevious.FrameRate.class);
        rate.putExtra("DO", "rate");
        rate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn3 = PendingIntent.getActivity(context, 1, rate, 0);
        notification.contentIntent = btn3;
        view.setOnClickPendingIntent(R.id.fpsLayout, btn3);

        //listener 3
        Intent exit = new Intent(context,  StopReceiver.class);
        exit.putExtra("DO", "exit");
        PendingIntent btn4 = PendingIntent.getBroadcast(context, 1, exit, 0);
        notification.contentIntent = btn4;
        view.setOnClickPendingIntent(R.id.exitLayout, btn4);

    }
}
