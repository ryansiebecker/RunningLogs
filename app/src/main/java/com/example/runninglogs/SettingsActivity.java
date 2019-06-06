package com.example.runninglogs;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    Switch themeSwitch;
    Button resetButton;
    private final String accessKey = "RunsArrayList";
    private int notificationID = 0;
    private String channelId = "notif";
    private Button testButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        createNotificationChannel();

        saveButton = findViewById(R.id.saveSettingsButton);
        themeSwitch = findViewById(R.id.themeToggle);
        resetButton = findViewById(R.id.resetButton);
        testButton = findViewById(R.id.testNotif);

        saveButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        testButton.setOnClickListener(this);

        SharedPreferences sharedPref = SettingsActivity.this.getSharedPreferences(accessKey, Context.MODE_PRIVATE);
        String theme = sharedPref.getString("theme", "error");

        if (!theme.equals("error")) {
            if (theme.equals("x")) {
                themeSwitch.setChecked(true);
                saveButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedxc));
            } else if (theme.equals("t")) {
                themeSwitch.setChecked(false);
                saveButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedtrack));
            }

        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.notificationChannel);
            NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveSettingsButton) {
            SharedPreferences sharedPrefToEdit = SettingsActivity.this.getSharedPreferences(accessKey, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefToEdit.edit();

            String themeToBeSaved = "";

            boolean checked = themeSwitch.isChecked();
            if (checked) {
                themeToBeSaved = "x";
            } else themeToBeSaved = "t";


            editor.putString("theme", themeToBeSaved);
            editor.commit();
            Toast.makeText(this, "Your settings have been saved.", Toast.LENGTH_LONG).show();
        }
        if (v.getId() == R.id.resetButton) {
            ArrayList<Run> runsTemp = new ArrayList<>();
            SharedPreferences sharedPrefToEdit = SettingsActivity.this.getSharedPreferences(accessKey, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefToEdit.edit();
            Gson gson = new Gson();
            String json = gson.toJson(runsTemp);
            editor.putString("RunsArrayList", json);
            editor.commit();
            Toast.makeText(this, "All run data has been erased", Toast.LENGTH_LONG).show();
        }
        if(v.getId() == R.id.testNotif){
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SettingsActivity.this, WeeklySummary.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(SettingsActivity.this, 0, intent, 0);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingsActivity.this, channelId)
                            .setSmallIcon(R.drawable.iconbase)
                            .setContentTitle("Weekly Summary")
                            .setContentText("Your Weekly summary is available! Tap to see details.")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SettingsActivity.this);
                    notificationManager.notify(notificationID, builder.build());
                }
            }, 5000);
        }
    }

}
