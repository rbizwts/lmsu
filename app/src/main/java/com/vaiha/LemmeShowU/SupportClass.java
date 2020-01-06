package com.vaiha.LemmeShowU;

import android.app.Activity;
import android.app.NotificationManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.widget.TextView;

import java.util.Timer;

/**
 * Created by a on 8/10/2016.
 */
public class SupportClass {

    public static String MY_PREFS_NAME = "lemmeshowu";
    public static String partner_ip;
    public static NotificationManager g_notificationManager = null;
    public static double g_frames = 1;
    public static boolean g_flag;
    public static TextView t;
    public static boolean close = false;
    public static Activity a;
    public static MediaRecorder mediaRecorder = null;
    public static VirtualDisplay virtualDisplay = null;
    public static Timer timer = null;
    public static boolean stopvid;
    public static boolean movie;
    public static boolean check_recorder_create = false;
    //public static Timer imgTimer = null;
    public static Timer getip_timer = null;
    public static Timer check_timer = null;

}
