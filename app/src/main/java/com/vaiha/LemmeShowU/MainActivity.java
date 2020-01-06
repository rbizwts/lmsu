package com.vaiha.LemmeShowU;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 20;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int DISPLAY_WIDTH;
    private int DISPLAY_HEIGHT;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static String path, output_path;
    private static MediaProjection mMediaProjection;
    private static VirtualDisplay mVirtualDisplay;
    private static MediaProjectionCallback mMediaProjectionCallback;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    SharedPrefManager sharedPrefManager;

    NotificationManager mNotificationManager;
    Notification notification;
    NotificationManager nmang;
    TextView msg, start, stop;
    String root = Environment.getExternalStorageDirectory().toString();
    ArrayList<Bitmap> screenshots = new ArrayList<>();
    Timer timer = new Timer();
    int hasPermissionStorage;
    Activity mActivity;
    List<String> permissions = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private MediaRecorder mMediaRecorder = null;
    private GoogleApiClient mGoogleApiClient;
    File imgf;
    Bitmap bmOriginal;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = MainActivity.this;
        //Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(MainActivity.this));
        setContentView(R.layout.activity_main);



        getIntent().setAction("Already created");
        msg = (TextView) findViewById(R.id.status);
        Button waitButton = (Button) findViewById(R.id.waitButton);
        //Button chnage_partner = (Button) findViewById(R.id.change_partner);
        SupportClass.t = msg;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        DISPLAY_HEIGHT = metrics.heightPixels;
        DISPLAY_WIDTH = metrics.widthPixels;
        Log.e(TAG,"width"+DISPLAY_WIDTH);
        Log.e(TAG,"Height"+DISPLAY_HEIGHT);
        sharedPrefManager = new SharedPrefManager(this);
        SupportClass.a = MainActivity.this;
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent modifySettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(modifySettings);
            }
        });*/
        request_screencast();
        waitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minimizeApp();
            }
        });

       /* chnage_partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Waiting.get_ip_timer.cancel();
                SupportClass.timer.cancel();
                mNotificationManager.cancelAll();
                Intent i = new Intent(MainActivity.this, HomePage.class);
                startActivity(i);
                finish();
            }
        });*/

    /*    if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
                //request_screencast();
                //output_path = "/storage/emulated/0/LemmeShowU";

                final File myDir = new File("/storage/emulated/0/TestingDirectory/images");
                myDir.mkdirs();

                Bitmap bbicon;

                bbicon=BitmapFactory.decodeResource(getResources(),R.drawable.icon);
//ByteArrayOutputStream baosicon = new ByteArrayOutputStream();
//bbicon.compress(Bitmap.CompressFormat.PNG,0, baosicon);
//bicon=baosicon.toByteArray();

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                OutputStream outStream = null;
                File file = new File(myDir, "er.PNG");
                try {
                    outStream = new FileOutputStream(file);
                    bbicon.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch(Exception e) {

                }


            }


        }*/

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
    }

    public static void delete() {
        File videoFile = new File(path);
        if (videoFile.exists()) {
            videoFile.delete();
        }
    }

    public static void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    @Override
    protected void onResume() {
         super.onResume();
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

      /*  if (id == R.id.logout) {
            return true;
        }*/

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
            Log.e("NNerer", "" + resultCode);
            Toast.makeText(this,
                    "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            SupportClass.a.finish();
            return;
        }

        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();

        mMediaRecorder.start();
        if (resultCode == RESULT_OK) {

            start_Notification();
            //minimizeApp();

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
            //Log.e("res 1", "request cancelled");
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

               // Log.e("LOOO", "looprunning");
                sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                String ot_path = sharedPreferences.getString("current_location", "");
                if (ot_path.equals("")) {
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    output_path = "/storage/emulated/0/LemmeShowU";
                    String folder_main = output_path + "/images";

                     imgf = new File(folder_main);
                    if (!imgf.exists()) {
                        imgf.mkdirs();
                    }

                   /* String fname = "Image-klg.jpg";
                    File file = new File(imgf, fname);*/

                    /*FileOutputStream fos;
                    try {
                        fos = openFileOutput("BITMAP_A", Context.MODE_PRIVATE);
                        bmOriginal.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    }catch(Exception e){

                    }*/



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
                    }
                });

                // Log.e("RRR ", "" + SupportClass.g_flag);
                if (temp2) {
                    //SupportClass.t.setText("Screen capture in Progress");
                   // Log.e("STATUS", "Video start");
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
                    //Log.e("STATUS", "Capturing Start");
                    if (SupportClass.check_recorder_create) {
                        mMediaRecorder = new MediaRecorder();
                        SupportClass.check_recorder_create = false;
                    }
                    mMediaRecorder = new MediaRecorder();
                    SupportClass.mediaRecorder = mMediaRecorder;
                    SupportClass.check_recorder_create = false;
                    initRecorder(i);
                    shareScreen();
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
                        }
                    });

                    try {
                        Thread.sleep(3000);
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

        private VirtualDisplay createVirtualDisplay () {

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
            mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            String bitRate = sharedPreferences.getString("bit_rate", "3000000");
           // Log.e("DANGER-rate",""+bitRate);
            //mMediaRecorder.setVideoEncodingBitRate(Integer.parseInt(bitRate));
            mMediaRecorder.setVideoEncodingBitRate(Integer.parseInt(bitRate));
            mMediaRecorder.setVideoFrameRate(16);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
           // Log.e("error=", "" + e);
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
       //Log.e(TAG, "Stopping Recording");
    }

    public void takeFrame() {

       // Log.e("res", "take frame called");
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
        } else if (frame_val == 1) {
            k = (long) (1000000 / SupportClass.g_frames);
        } else if (frame_val == 2) {
            k = (long) (1000000 / SupportClass.g_frames);
        }

        // long k = 1000000 / SupportClass.g_frames;
        for (long j = k; j <= length; j += k) {
            Bitmap b = mmr.getFrameAtTime(j, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);

            // frame at 2 seconds
            if (b != null) {
                int size = Util.sizeOf(b);
                if (size == 3686400)
                    bArray.add(b);
            }
        }

        screenshots = bArray;

        byte[] artwork = mmr.getEmbeddedPicture();
        mmr.release();

        for (Bitmap shot : screenshots) {
            //Log.e("aaa", "shot" + shot);
            SaveImage(shot);
        }
    }

   /* public void setFileNameAndPath(){
        int count = 0;
        File f;

        do{
            count++;

            mFileName = getString(R.string.default_file_name)
                    + " #" + (mDatabase.getCount() + count) + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/SoundRecorder/" + mFileName;

            f = new File(mFilePath);
        }while (f.exists() && !f.isDirectory());
    }*/


    private void SaveImage(Bitmap finalBitmap) {

        /*if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
                //request_screencast();
                final File myDir = new File(output_path + "/images");
                myDir.mkdirs();



                Date now = new Date();
                Calendar calendar = Calendar.getInstance();
                long mi = calendar.getTimeInMillis();
                String s = (String) android.text.format.DateFormat.format("ddMMyyhhmmssss", now);

                String fname = "Image-" + s + mi + ".jpg";
                //Toast.makeText(MainActivity.this,fname,Toast.LENGTH_LONG).show();

                // Log.e("img name",fname);

                File file = new File(myDir, fname);

                if (file.exists()) {
                    file.delete();
                }else{
                    //Toast.makeText(MainActivity.this," file not exist ",Toast.LENGTH_LONG).show();

                    // Log.e("File","file not exist");


                }
                try {

                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //request_screencast();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
                Log.e("testing", "Permission is revoked");

            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            final File myDir = new File(output_path + "/images");
            myDir.mkdirs();



            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            long mi = calendar.getTimeInMillis();
            String s = (String) android.text.format.DateFormat.format("ddMMyyhhmmssss", now);

            String fname = "Image-" + s + mi + ".jpg";
            //Toast.makeText(MainActivity.this,fname,Toast.LENGTH_LONG).show();

            // Log.e("img name",fname);

            File file = new File(imgf, fname);

            if (file.exists()) {
                file.delete();
            }else{
                //Toast.makeText(MainActivity.this," file not exist ",Toast.LENGTH_LONG).show();

                // Log.e("File","file not exist");


            }
            try {

                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
*/


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing image", "Permission is granted");
                //request_screencast();
                //output_path = "/storage/emulated/0/LemmeShowU";

                final File myDir = new File(output_path + "/images");
                myDir.mkdirs();



                Date now = new Date();
                Calendar calendar = Calendar.getInstance();
                long mi = calendar.getTimeInMillis();
                String s = (String) android.text.format.DateFormat.format("ddMMyyhhmmssss", now);

                String fname = "Image-" + s + mi + ".jpg";

                //Bitmap bbicon;

                //bbicon=BitmapFactory.decodeResource(getResources(),R.drawable.icon);
//ByteArrayOutputStream baosicon = new ByteArrayOutputStream();
//bbicon.compress(Bitmap.CompressFormat.PNG,0, baosicon);
//bicon=baosicon.toByteArray();

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                OutputStream outStream = null;
                File file = new File(myDir, fname);
                try {

                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }else {


            final File myDir = new File(output_path + "/images");
            myDir.mkdirs();


            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            long mi = calendar.getTimeInMillis();
            String s = (String) android.text.format.DateFormat.format("ddMMyyhhmmssss", now);

            String fname = "Image-" + s + mi + ".jpg";
            //Toast.makeText(MainActivity.this,fname,Toast.LENGTH_LONG).show();

            // Log.e("img name",fname);

            File file = new File(imgf, fname);

            if (file.exists()) {
                file.delete();
            } else {
                //Toast.makeText(MainActivity.this," file not exist ",Toast.LENGTH_LONG).show();

                // Log.e("File","file not exist");


            }


            try {

                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
////------not needed now

        //share image to other device
       /* runOnUiThread(new Runnable() {
            public void run() {
                String imgfile = myDir + "/" + fname;
                Log.e("iiii", "" + imgfile);
                new Waiting().share(imgfile);
            }
        });*/




    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            SupportClass.g_notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        destroyMediaProjection();
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
       /* //listener 1
        Intent start = new Intent(this, StartReceiver.class);
        start.putExtra("DO", "start");
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn1 = PendingIntent.getBroadcast(this, 0, start, 0);
        notification.contentIntent = btn1;
        view.setOnClickPendingIntent(R.id.startLayout, btn1);

        //listener 2
        *//*Intent rate1 = new Intent(this, BitRate.class);
        rate1.putExtra("DO", "movie");
        rate1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rate1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent btn2 = PendingIntent.getBroadcast(this, 1, rate1, 0);
        notification.contentIntent = btn2;
        view.setOnClickPendingIntent(R.id.mpLayout, btn2);*//*

        //listener 2
        Intent rate = new Intent(this, FrameRate.class);
        rate.putExtra("DO", "rate");
        PendingIntent btn3 = PendingIntent.getActivity(this, 1, rate, 0);
        notification.contentIntent = btn3;
        view.setOnClickPendingIntent(R.id.fpsLayout, btn3);

        //listener 2
        Intent bit_rate = new Intent(this, BitRate.class);
        bit_rate.putExtra("DO", "brate");
        PendingIntent btn5 = PendingIntent.getActivity(this, 1, bit_rate, 0);
        notification.contentIntent = btn5;
        view.setOnClickPendingIntent(R.id.mpLayout, btn5);

        //listener 3
        Intent exit = new Intent(this, StopReceiver.class);
        exit.putExtra("DO", "exit");
        PendingIntent btn4 = PendingIntent.getBroadcast(this, 1, exit, 0);
        notification.contentIntent = btn4;
        view.setOnClickPendingIntent(R.id.exitLayout, btn4);*/

//--------------------------------------

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
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

    void request_screencast() {

        if (!sharedPrefManager.isGrantPermission()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle("LemmeShowU");
            alertDialog.setCancelable(false);
            // Setting Dialog Message
            alertDialog.setMessage("LemmeShowU will need permission to capture everything displayed on your screen");

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke YES event
                    sharedPrefManager.setGrantPermission(true);
                    dialog.cancel();
                    start_process();
                }
            });


            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    Toast.makeText(MainActivity.this, "LemmeShowU requires your permission to capture a recording of your screen. Please restart and provide permission to unleash the power!", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    SupportClass.a.finish();
                   // minimizeApp();
                }
            });

            // Showing Alert Message
            alertDialog.show();

        }else{
            start_process();
        }

    }

    void start_process() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("testing", "Permission is granted");
                //request_screencast();
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
            // Log.e("mmm", "" + SupportClass.mediaRecorder);
            initRecorder(0);
            shareScreen();
        }

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
    public boolean saveImageToInternalStorage(Bitmap image) {

        try {
// Use the compress method on the Bitmap object to write image to
// the OutputStream
            FileOutputStream fos = MainActivity.this.openFileOutput("desiredFilename.png",MODE_WORLD_READABLE);

// Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;
    } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }



}