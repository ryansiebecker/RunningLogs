package com.example.runninglogs;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView dateView;
    private Button newRunButton;
    private Button viewRunButton;
    private ImageButton settingsButton;
    private RelativeLayout mainLayout;

    private final String accessKey = "RunsArrayList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //set up widgets
        newRunButton = findViewById(R.id.newRunButton);
        viewRunButton = findViewById(R.id.viewRunsButton);
        settingsButton = findViewById(R.id.settingsButton);
        mainLayout = findViewById(R.id.mainView);

        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(accessKey,Context.MODE_PRIVATE);
        String RunListGSON = sharedPref.getString(accessKey,"error");
        String theme = sharedPref.getString("theme", "error");

        if(!theme.equals("error")){
            if(theme.equals("x")){
                mainLayout.setBackground(getDrawable(R.drawable.xcbackground));
            }
            else if(theme.equals("t")){
                mainLayout.setBackground(getDrawable(R.drawable.trackbackground));
            }

        }

        if(RunListGSON.equals("error")) {
            ArrayList<Run> runsTemp = new ArrayList<>();
            SharedPreferences sharedPrefToEdit = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(runsTemp);
            editor.putString("RunsArrayList", json);
            editor.commit();
        }




        viewRunButton.setOnClickListener(this);
        newRunButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);

        //set up textView with Date
        dateView = findViewById(R.id.dateTextField);
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d");
        String output = formatter.format(currentDate);
        dateView.setText(output);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewRunsButton) {
            Intent i = new Intent(this, ViewSavedRuns.class);
            startActivity(i);
        }
        if (v.getId() == R.id.newRunButton) {
            Intent i = new Intent(this, NewRun.class);
            startActivity(i);
        }
        if (v.getId() == R.id.settingsButton) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
    }
}
