package com.vaiha.LemmeShowU.LemmeShowUPrevious;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.vaiha.LemmeShowU.BitRate;
import com.vaiha.LemmeShowU.FrameRate;
import com.vaiha.LemmeShowU.MainActivity4;
import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.Setting;
import com.vaiha.LemmeShowU.StartReceiver;
import com.vaiha.LemmeShowU.StopReceiver;
import com.vaiha.LemmeShowU.SupportClass;
import com.vaiha.LemmeShowU.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {
    NotificationManager mNotificationManager;
    Notification notification;
    NotificationManager nmang;
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 20;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    /*private static  int DISPLAY_WIDTH = 720;
    private static  int DISPLAY_HEIGHT = 1280;*/
    private static  int DISPLAY_WIDTH = 0;
    private static  int DISPLAY_HEIGHT = 0;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    TextView msg, start, stop;
    static String path, output_path;
    String root = Environment.getExternalStorageDirectory().toString();
    ArrayList<Bitmap> screenshots = new ArrayList<>();
    Timer timer = new Timer();
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private static MediaProjection mMediaProjection;
    private static VirtualDisplay mVirtualDisplay;
    private static MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder = null;
    int hasPermissionStorage;
    List<String> permissions = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MainActivity.this));
        setContentView(R.layout.activity_main);
        /*int Measuredwidth = 0;
        int Measuredheight = 0;
        Point size = new Point();
        WindowManager w = getWindowManager();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
           *//* Measuredwidth = size.x;
            Measuredheight = size.y;*//*
            DISPLAY_WIDTH=size.x;
            DISPLAY_HEIGHT=size.y;
        }else{
            Display d = w.getDefaultDisplay();
            *//*Measuredwidth = d.getWidth();
            Measuredheight = d.getHeight();*//*
            DISPLAY_WIDTH=d.getWidth();
            DISPLAY_HEIGHT=d.getHeight();

        }*/
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        DISPLAY_HEIGHT = metrics.heightPixels;
        DISPLAY_WIDTH = metrics.widthPixels;
        Log.e(TAG,"width"+DISPLAY_WIDTH);
        Log.e(TAG,"Height"+DISPLAY_HEIGHT);
       // Toast.makeText(getApplicationContext(),"width--"+DISPLAY_WIDTH+"width--"+DISPLAY_HEIGHT,Toast.LENGTH_LONG).show();
        mScreenDensity = metrics.densityDpi;

        msg = (TextView) findViewById(R.id.status);
        Button waitButton = (Button) findViewById(R.id.waitButton);
        SupportClass.t = msg;
        /*DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);*/


        SupportClass.a = com.vaiha.LemmeShowU.LemmeShowUPrevious.MainActivity.this;
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        request_screencast();
        waitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizeApp();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, Setting.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.exit) {
            close();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        Log.e("NN", "" + resultCode);
        if (resultCode != RESULT_OK) {
            //Log.e("NN", "" + resultCode);
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            return;
        }

        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();

        mMediaRecorder.start();
        if (resultCode == RESULT_OK) {
            start_Notification();
           // minimizeApp();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    /*//Do something after 100ms

                    if (mMediaProjection != null) {
                        SupportClass.mediaRecorder.stop();
                        SupportClass.mediaRecorder.reset();
                    }
                    takeFrame();*/

                    lets_start();
                }
            }, 1000);

        } else {
            Log.e("res 1", "request cancelled");
            return;
        }

    }

    public void lets_start() {


        final long period = 1000;

        //final Timer timer = new Timer();
        SupportClass.timer = timer;
        //Log.e("TTT1", "" + SupportClass.timer);
        timer.schedule(new TimerTask() {
            int i = 1;

            @Override
            public void run() {

                sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                String ot_path = sharedPreferences.getString("current_location", "");
                if (ot_path.equals("")) {
                    output_path = "/storage/emulated/0/LemmeShowU";
                } else {
                    output_path = ot_path;
                }

                boolean temp = SupportClass.g_flag;
                boolean temp1 = SupportClass.close;
                boolean temp2 = SupportClass.movie;

                runOnUiThread(new Runnable()
                        //run on ui thread
                {
                    public void run() {
                        //Log.e("test","thread");

                        boolean temp = SupportClass.g_flag;
                        boolean temp2 = SupportClass.movie;
                        if (temp2) {
                            //  Log.e("test","1");
                            msg.setText("Screen capture in Progress");
                        } else if (temp) {
                            // Log.e("test","2");
                            msg.setText("Screen capture in Progress");
                        } else {
                            //  Log.e("test","3");
                            msg.setText("No screen capture in Progress");
                        }
                        if (SupportClass.movie){
                            msg.setText("Screen capture in Progress");

                        }
                    }
                });

                // Log.e("RRR ", "" + SupportClass.g_flag);
                if (temp2) {
                    //SupportClass.t.setText("Screen capture in Progress");
                    Log.e("STATUS", "Video start");
                    mMediaRecorder = new MediaRecorder();
                    SupportClass.mediaRecorder = mMediaRecorder;
                    initRecorder(0);
                    shareScreen();
                    if (mMediaProjection != null) {
                        SupportClass.movie = false;
                        SupportClass.stopvid = true;
                    }
                    SupportClass.movie = false;
                }
                //Log.e("jjjj", "" + SupportClass.g_frames);
                if (temp) {

                    //SupportClass.t.setText("Screen capture in Progress");
                    Log.e("STATUS", "Capturing Start");
                    if (SupportClass.check_recorder_create) {
                        mMediaRecorder = new MediaRecorder();
                        SupportClass.check_recorder_create = false;
                    }
                    mMediaRecorder = new MediaRecorder();
                    SupportClass.mediaRecorder = mMediaRecorder;
                    SupportClass.check_recorder_create = false;
                    initRecorder(i);
                    shareScreen();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mMediaProjection != null) {
                        stoped();
                    }
                    if (timer != null) {
                        takeFrame();
                        delete();
                    }

                    i++;
                }
                if (temp1) {
                    SupportClass.close = false;
                    SupportClass.timer.cancel();
                    destroyMediaProjection();
                    SupportClass.g_notificationManager.cancelAll();
                    close();
                }
            }
        }, 0, period);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void initRecorder(int videoName) {
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        if (SupportClass.movie) {
            Date now = new Date();
            String s = (String) android.text.format.DateFormat.format("ddMMyyhhmmssss", now);
            String folder_main = output_path + "/Video";

            File f = new File(folder_main);
            if (!f.exists()) {
                f.mkdirs();
            }
            mMediaRecorder.setOutputFile(folder_main + "/Video-" + s + ".mp4");
        } else {
            String folder_main = "Android";
            File f = new File(Environment.getExternalStorageDirectory(), folder_main);
            if (!f.exists()) {
                f.mkdirs();
            }
            path = root + "/Android/Video_" + videoName + ".mp4";
            mMediaRecorder.setOutputFile(path);
        }
        try {
          //  mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);

            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoEncodingBitRate(3000000);
            mMediaRecorder.setVideoFrameRate(24);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("error=", "" + e);
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void shareScreen() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        SupportClass.virtualDisplay = mVirtualDisplay;
        mMediaRecorder.start();
    }

    public void stoped() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }
        stopScreenSharing();
        Log.e(TAG, "Stopping Recording");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
    }


    public void takeFrame() {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);

        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (time == null) {
            return;
        }
        int timeInmillisec = Integer.parseInt(time);
        double duration = timeInmillisec / 1000;
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long length = (timeInmillisec * 1000);
        ArrayList<Bitmap> bArray = new ArrayList<Bitmap>();
        bArray.clear();

        double frame_val = SupportClass.g_frames;
        //Log.e("frame value =", "" + frame_val);
        long k = 0;
        if (frame_val == 0.5) {
            k = 1000000 * 2;
        } else if (frame_val == 0.25) {
            k = 1000000 * 4;
        } else if(frame_val == 1){
            k = (long) (1000000 / SupportClass.g_frames);
        }
        else if(frame_val == 2){
            k = (long) (1000000 / SupportClass.g_frames);
        }

       // long k = 1000000 / SupportClass.g_frames;
        for (long j = k; j <= length; j += k) {
            Bitmap b = mmr.getFrameAtTime(j, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds

            // frame at 2 seconds
            if (b != null) {
                int size = com.vaiha.LemmeShowU.LemmeShowUPrevious.Util.sizeOf(b);
                if (size == 3686400)
                    bArray.add(b);
            }
        }

        screenshots = bArray;

        byte[] artwork = mmr.getEmbeddedPicture();
        mmr.release();

        for (Bitmap shot : screenshots) {
            SaveImage(shot);
        }
    }

    private void SaveImage(Bitmap finalBitmap) {
        File myDir = new File(output_path + "/images");
        myDir.mkdirs();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        long mi = calendar.getTimeInMillis();
        String s = (String) android.text.format.DateFormat.format("ddMMyyhhmmssss", now);

        String fname = "Image-" + s + mi + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete() {
        File videoFile = new File(path);
        if (videoFile.exists()) {
            videoFile.delete();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
       try {
           SupportClass.g_notificationManager.cancelAll();
           destroyMediaProjection();
           super.onDestroy();
       }catch(Exception e){

       }

       }

   /* @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        Log.e("fff","destroy");
        super.onDestroy();
    }*/


    public static void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaProjection = null;
            stopScreenSharing();
        }
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
       //contentView.setTextViewText(R.id.movie, "Movie");
        contentView.setTextViewText(R.id.fps, "FPS");
        contentView.setTextViewText(R.id.exit, "Exit");

        setListeners(contentView);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String mode = sharedPreferences.getString("capture_mode", "Video");
        //Log.e("ffff",""+mode);
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
       // notification.defaults |= Notification.DEFAULT_SOUND; // Sound
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
        PendingIntent btn3 = PendingIntent.getActivity(this, 1, rate, 0);
        notification.contentIntent = btn3;
        view.setOnClickPendingIntent(R.id.fpsLayout, btn3);

        //listener 3
        Intent exit = new Intent(this, StopReceiver.class);
        exit.putExtra("DO", "exit");
        PendingIntent btn4 = PendingIntent.getBroadcast(this, 1, exit, 0);
        notification.contentIntent = btn4;
        view.setOnClickPendingIntent(R.id.exitLayout, btn4);


        //listener 2
        Intent bit_rate = new Intent(this, BitRate.class);
        bit_rate.putExtra("DO", "brate");
        PendingIntent btn5 = PendingIntent.getActivity(this, 1, bit_rate, 0);
        notification.contentIntent = btn5;
        view.setOnClickPendingIntent(R.id.mpLayout, btn5);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMediaRecorder = new MediaRecorder();
                    SupportClass.mediaRecorder = mMediaRecorder;
                    initRecorder(0);
                    shareScreen();
                } else {
                    Toast.makeText(com.vaiha.LemmeShowU.LemmeShowUPrevious.MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void close() {
        SupportClass.a.finish();
    }

    void request_screencast()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(com.vaiha.LemmeShowU.LemmeShowUPrevious.MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("LemmeShowU");
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage("LemmeShowU will need permission to capture everything displayed on your screen");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                start_process();
            }
        });


        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(com.vaiha.LemmeShowU.LemmeShowUPrevious.MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    void start_process(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mMediaRecorder = new MediaRecorder();
                SupportClass.mediaRecorder = mMediaRecorder;
                initRecorder(0);
                shareScreen();
            } else {
                //request_screencast();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
                Log.e("testing", "Permission is revoked");

            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            mMediaRecorder = new MediaRecorder();
            SupportClass.mediaRecorder = mMediaRecorder;
            initRecorder(0);
            shareScreen();
        }
    }

}