package com.example.runninglogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklySummary extends AppCompatActivity implements View.OnClickListener {


    //global variables
    private final String accessKey = "RunsArrayList";
    //global widgets
    private TextView beginningDate;
    private TextView endDate;
    private TextView numOfRuns;
    private TextView totalMileage;
    private Button viewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_summary);

        //set Up Widgets
        beginningDate = findViewById(R.id.dateBeginning);
        endDate = findViewById(R.id.dateEnding);
        numOfRuns = findViewById(R.id.numberOfRuns);
        totalMileage = findViewById(R.id.totalMileage);
        viewButton = findViewById(R.id.viewButton);

        viewButton.setOnClickListener(this);

        SharedPreferences sharedPref = WeeklySummary.this.getSharedPreferences(accessKey, Context.MODE_PRIVATE);
        String theme = sharedPref.getString("theme", "error");

        if (!theme.equals("error")) {
            if (theme.equals("x")) {
                viewButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedxc));
            } else if (theme.equals("t")) {
                viewButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedtrack));
            }

        }

        //Need to calculate weekly mileage based upon sharedPreferences Data that is loaded in
        String RunListJSON = sharedPref.getString(accessKey, "error");
        Gson gson = new Gson();
        ArrayList<Run> savedRuns = gson.fromJson(RunListJSON, new TypeToken<ArrayList<Run>>() {
        }.getType());

        Date currentDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date todate1 = cal.getTime();


        SimpleDateFormat Dateformatter = new SimpleDateFormat("MMMM d, yyyy");
        String startDate = Dateformatter.format(currentDate);
        String fromdate = Dateformatter.format(todate1);

        beginningDate.setText(startDate);
        endDate.setText(fromdate);


        ArrayList<Run> runsFromThisWeek = new ArrayList<>();
        //go through runs and select runs from the past week
        for (Run x : savedRuns) {
            if (x.getDate().compareTo(todate1) >= 0 && x.getDate().compareTo(currentDate) <= 0) {
                runsFromThisWeek.add(x);
            }
        }

        double totalDistance = 0;
        for (Run x : runsFromThisWeek) {
           totalDistance+=x.getDistance();
        }
        int runs = runsFromThisWeek.size();

        totalMileage.setText(totalDistance + " miles");
        numOfRuns.setText(runs + " runs");


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.viewButton) {
            Intent i = new Intent(this, ViewSavedRuns.class);
            startActivity(i);
        }
    }
}
