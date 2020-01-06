package com.vaiha.LemmeShowU.LemmeShowUPrevious;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.SupportClass;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.ArrayList;

/**
 * Created by vaiha on 6/9/16.
 */
public class Setting extends Activity  implements DirectoryChooserFragment.OnFragmentInteractionListener  {

    LinearLayout outputPath, farmeRate;
    Button resetBtn;
    TextView output_location;
    SharedPreferences pref;
    String location, frRate, path;
    Spinner menu_list_spinner;
    ArrayList<String> menu_list = new ArrayList<>();
    public static int PICK_DIRECTORY = 0;
    private TextView mDirectoryTextView;
    private DirectoryChooserFragment mDialog;
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
        output_location = (TextView) findViewById(R.id.output_location);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String current_path = pref.getString("current_location", "");
        if (current_path.equals("")) {
            output_location.setText("Set storage path for the recording.currently allows paths that are in /storage/emulated/0/LemmeShowU");
        } else {
            output_location.setText("Set storage path for the recording.currently allows paths that are in " + current_path);

        }

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("LemmeShowU")
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
        if(SupportClass.g_frames<1){
            temp =SupportClass.g_frames+"F";
        }
        else{
            temp=(int)SupportClass.g_frames+"F";
        }

        Log.e("iiii", "" + SupportClass.g_frames);
        for(int i=0;i<menu_list.size();i++){
            if( menu_list.get(i).equals(temp)){
                menu_list_spinner.setSelection(i);
                break;
            }
        }

        menu_list_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                            SupportClass.g_frames = (double) 0.25;
                        break;
                    case 1:
                            SupportClass.g_frames = (double) 0.5;
                        break;
                    case 2:
                            SupportClass.g_frames =  1;
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

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportClass.g_frames=1;
                menu_list_spinner.setSelection(2);
                output_location.setText("Set storage path for the recording.currently allows paths that are in /storage/emulated/0/LemmeShowU");
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("current_location", "/storage/emulated/0/LemmeShowU");
                editor.apply();
                Toast.makeText(Setting.this, "Settings Resetted Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onSelectDirectory(@NonNull final String path) {
        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("current_location",path);
        editor.apply();
        Intent i = new Intent(Setting.this, Setting.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }
}
