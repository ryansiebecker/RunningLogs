package com.example.runninglogs;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.util.Date;

public class RunDetails extends AppCompatActivity {

    private String title;
    private String date;
    private String time;
    private String condition;
    private String humidity;
    private String temp;
    private String wind;
    private double distance;
    private String runLength;
    private String notesString;
    private String pace;

    private EditText weatherCondition;
    private EditText weatherTemp;
    private EditText humidityText;
    private EditText windText;
    private TextView dateText;
    private TextView timeText;
    private EditText mileText;
    private EditText paceText;
    private EditText totalTime;
    private EditText notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_details);
        //get info from the Intent
        Intent i = getIntent();
        title = i.getStringExtra("TITLE");
        date = i.getStringExtra("DATE");
        time = i.getStringExtra("TIME");
        condition = i.getStringExtra("CONDITION");
        humidity = i.getStringExtra("HUMIDITY");
        temp = i.getStringExtra("TEMP");
        wind = i.getStringExtra("WIND");
        distance = i.getDoubleExtra("MILEAGE", 0);
        runLength = i.getStringExtra("RUNTIME");
        notesString = i.getStringExtra("NOTES");
        pace = i.getStringExtra("PACE");

        weatherCondition = findViewById(R.id.conditionText);
        weatherTemp = findViewById(R.id.tempText);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windsText);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        mileText = findViewById(R.id.DistanceWidget);
        paceText = findViewById(R.id.paceWidget);
        totalTime = findViewById(R.id.TimeWidget);
        notes = findViewById(R.id.notesWidget);

        dateText.setText(date);
        timeText.setText(time);
        weatherCondition.setText(condition);
        weatherTemp.setText(temp);
        humidityText.setText(humidity);
        windText.setText(wind);
        DecimalFormat df = new DecimalFormat("0.00");
        String distanceString = df.format(distance);
        mileText.setText(distanceString + " miles");
        paceText.setText(pace);
        totalTime.setText(runLength);
        notes.setText(notesString);

        setTitle(title);
    }
}
