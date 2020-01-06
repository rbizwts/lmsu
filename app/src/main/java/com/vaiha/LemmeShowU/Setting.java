package com.vaiha.LemmeShowU;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.ArrayList;

/**
 * Created by vaiha on 6/9/16.
 */
public class Setting extends Activity implements DirectoryChooserFragment.OnFragmentInteractionListener {

    public static int PICK_DIRECTORY = 0;
    LinearLayout outputPath, farmeRate;
    Button resetBtn;
    TextView output_location;
    SharedPreferences pref;
    String location, frRate, path;
    Spinner menu_list_spinner, capture_spinner,bitrate_spinner;
    ArrayList<String> menu_list = new ArrayList<>();
    ArrayList<String> mode_list = new ArrayList<>();
    ArrayList<String> bit_rate_list = new ArrayList<>();
    ArrayList<String> bit_rate_val_list = new ArrayList<>();
    private TextView mDirectoryTextView;
    private DirectoryChooserFragment mDialog;
    NotificationManager mNotificationManager;
    Notification notification;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        outputPath = (LinearLayout) findViewById(R.id.outputPath);
        ImageButton back = (ImageButton) findViewById(R.id.back_btn);
        farmeRate = (LinearLayout) findViewById(R.id.frameRate);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        menu_list_spinner = (Spinner) findViewById(R.id.spinner_menu);
        capture_spinner = (Spinner) findViewById(R.id.mode_spinner);
        bitrate_spinner = (Spinner) findViewById(R.id.bitrate_spinner);
        output_location = (TextView) findViewById(R.id.output_location);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String current_path = pref.getString("current_location", "");
        if (current_path.equals("")) {
            output_location.setText("Set storage path for the recording.currently allows paths that are in /storage/emulated/0/LemmeShowU");
        } else {
            output_location.setText("Set storage path for the recording.currently allows paths that are in " + current_path);
        }

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("").allowNewDirectoryNameModification(true)
                .build();

        mDialog = DirectoryChooserFragment.newInstance(config);
        mDirectoryTextView = (TextView) findViewById(R.id.textDirectory);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        outputPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show(getFragmentManager(), null);
            }
        });

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

        //capure mode
        mode_list.add("Video");
        mode_list.add("Image");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.list_item, mode_list);

        dataAdapter.setDropDownViewResource
                (R.layout.list_item);

        capture_spinner.setAdapter(adapter);

        String checck = pref.getString("capture_mode", "Video");
        Log.e("ffff", "" + checck);
        if (checck.equals("Video")) {
            capture_spinner.setSelection(0,false);
        } else {
            capture_spinner.setSelection(1,false);
        }

        //capture_spinner.setOnItemSelectedListener(this);
       /* try{
        capture_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String val = mode_list.get(position);
                Log.e("dddd", "" + val);
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("capture_mode", val);
                editor.apply();
                SupportClass.g_notificationManager.cancelAll();
                start_Notification();
            }
        });
        }
        catch (Exception e){
            Log.e("LEMMexx",""+e);
        }*/

        capture_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String val = mode_list.get(position);
                Log.e("dddd", "" + val);
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("capture_mode", val);
                editor.apply();
                if(SupportClass.g_notificationManager!=null) {
                    SupportClass.g_notificationManager.cancelAll();
                }
                start_Notification();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //capure mode
        bit_rate_list.add("Low");
        bit_rate_list.add("Medium");
        bit_rate_list.add("High");

        bit_rate_val_list.add("500000");
        bit_rate_val_list.add("1000000");
        bit_rate_val_list.add("3000000");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (this, R.layout.list_item, bit_rate_list);

        dataAdapter.setDropDownViewResource
                (R.layout.list_item);

        bitrate_spinner.setAdapter(adapter2);

        String check = pref.getString("bit_rate", "3000000");
        //String rate = pref.getString("bit_rate", "High");
        Log.e("ffff", "" + check);
        if (check.equals("500000")) {
            bitrate_spinner.setSelection(0);
        } else if(check.equals("1000000")){
            bitrate_spinner.setSelection(1);
        }
        else {
            bitrate_spinner.setSelection(2);
        }



        bitrate_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String val = bit_rate_list.get(position);
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Log.e("dddd", "" + val);

                if (val.equals("Low")) {
                    editor.putString("bit_rate", bit_rate_val_list.get(0));
                } else if(val.equals("Medium")){

                    editor.putString("bit_rate", bit_rate_val_list.get(1));
                }
                else {
                    editor.putString("bit_rate", bit_rate_val_list.get(2));
                }

                editor.apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportClass.g_frames = 1;
                menu_list_spinner.setSelection(2);
                capture_spinner.setSelection(0);
                bitrate_spinner.setSelection(2);
                output_location.setText("Set storage path for the recording.currently allows paths that are in /storage/emulated/0/LemmeShowU");
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("current_location", "/storage/emulated/0/LemmeShowU");
                editor.putString("capture_mode", "Video");
                editor.putString("bit_rate", "3000000");
                editor.apply();
                Toast.makeText(Setting.this, "Settings Resetted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSelectDirectory(@NonNull final String path) {
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current_location", path);
        editor.apply();
        Intent i = new Intent(Setting.this, Setting.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
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
        Log.e("ffff",""+mode);
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
        view.setOnClickPendingIntent(R.id.exitLayout, btn4);

    }

}
