package com.example.runninglogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class NewRun extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>>, View.OnClickListener {
    //Widgets
    private EditText title;
    private EditText weatherCondition;
    private EditText weatherTemp;
    private EditText humidityText;
    private EditText windText;
    private Button scanWatchButton;
    private TextView dateText;
    private TextView timeText;
    private Button cancelButton;
    private Button saveButton;
    private EditText mileText;
    private EditText paceText;
    private EditText totalTime;
    private EditText notes;
    private ImageButton calendar;
    private ImageButton editWeather;
    private ImageButton editRun;
    private MediaPlayer mMediaPlayer;

    //Global variables
    private String town;
    private double lat;
    private double lon;
    private static String REQUEST_URLBASE = "http://api.openweathermap.org/data/2.5/weather?lat=uuLATuu&lon=uuLONuu&units=imperial&APPID=4d98d64962f56589f02eb166b67439f1";
    public final int SCAN_REQUEST_CODE = 1;
    private int WEATHER_LOADER_ID = 0;
    private Date runDate;
    private final String accessKey = "RunsArrayList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);
        setTitle("New Run");

        //initialize widgets
        title = findViewById(R.id.titleEdit);
        weatherCondition = findViewById(R.id.conditionText);
        weatherTemp = findViewById(R.id.tempText);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windsText);
        scanWatchButton = findViewById(R.id.scanWatchButton);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.submitButton);
        mileText = findViewById(R.id.DistanceWidget);
        paceText = findViewById(R.id.paceWidget);
        totalTime = findViewById(R.id.TimeWidget);
        notes = findViewById(R.id.notesWidget);
        calendar = findViewById(R.id.calendarButton);
        editRun = findViewById(R.id.RunEditButton);
        editWeather = findViewById(R.id.weatherEditButton);
        mMediaPlayer = MediaPlayer.create(this, R.raw.slow_spring_board);

        SharedPreferences sharedPref = NewRun.this.getSharedPreferences(accessKey,Context.MODE_PRIVATE);
        String theme = sharedPref.getString("theme", "error");

        if(!theme.equals("error")){
            if(theme.equals("x")){
                saveButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedxc));
                cancelButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedxc));
                scanWatchButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedxc));
            }
            else if(theme.equals("t")){
                saveButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedtrack));
                cancelButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedtrack));
                scanWatchButton.setBackground(getDrawable(R.drawable.transparent_bg_borderedtrack));
            }

        }

        //setOnClickListeners
        scanWatchButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        calendar.setOnClickListener(this);
        editWeather.setOnClickListener(this);
        editRun.setOnClickListener(this);

        //set up date & time
        runDate = new Date();
        updateTimeDate();

        //MediaPlayer setup
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
            }
        });


        //get current location information
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, 0); //request permissions if they are not yet granted, because they are "dangerous"
        }
        //need to try both GPS and Network because sometimes one works but not the other. GPS = more accurate so check first
        Location location = null;
        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            town = getAddress(location.getLatitude(), location.getLongitude(), NewRun.this);
            lon = location.getLongitude();
            lat = location.getLatitude();
            title.setText(getAddress(lat, lon, NewRun.this) + " Running");
        }

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //replace lat and lon with actual lat and lon
        REQUEST_URLBASE = REQUEST_URLBASE.replaceAll("uuLATuu", "" + lat);
        REQUEST_URLBASE = REQUEST_URLBASE.replaceAll("uuLONuu", "" + lon);

        if (networkInfo != null && networkInfo.isConnected()) {
            getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);
        } else {
            //TODO: display a no internet AlertBox
        }
    }

    public static String getAddress(double lat, double lon, Context context) { //geocoder
        Geocoder gc = new Geocoder(context);
        String strAddress = "";
        if (gc.isPresent()) {
            List<Address> list;
            try {
                list = gc.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                list = null;
                return "Unknown";
            }
            Address address = list.get(0);
            StringBuffer str = new StringBuffer();
            str.append(address.getLocality());
            strAddress = str.toString();
        }
        return strAddress;
    }

    @NonNull
    @Override
    public Loader<ArrayList<String>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new WeatherLoader(this, REQUEST_URLBASE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<String>> loader, ArrayList<String> strings) {
        if (strings != null && !strings.isEmpty()) {
            weatherCondition.setText(strings.get(0) + "");
            String tempTemp = strings.get(1);
            double tempDouble = Double.parseDouble(tempTemp);
            tempDouble = Math.round(tempDouble);
            int tempInt = (int) tempDouble;
            weatherTemp.setText(tempInt + "˚");
            String humidityTemp = strings.get(2);
            double humidityDouble = Double.parseDouble(humidityTemp);
            humidityDouble = Math.round(humidityDouble);
            int humidityInt = (int) humidityDouble;
            humidityText.setText(humidityInt + "%");
            String windTemp = strings.get(3);
            double windDouble = Double.parseDouble(windTemp);
            windDouble = Math.round(windDouble);
            int windInt = (int) windDouble;
            windText.setText(windInt + " mph");

        } else {
            //TODO: Add alertbox to notify that there is no data
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<String>> loader) {

    }

    private void saveData() {
        SharedPreferences sharedPref = NewRun.this.getSharedPreferences(accessKey,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();

        String distanceString = mileText.getText().toString();
        String mileTempText = mileText.getText().toString();
        if(mileTempText.contains("miles"))
            mileTempText = mileTempText.replaceAll("miles", "");
        if(mileTempText.contains("mi"))
            mileTempText = mileTempText.replaceAll("mi", "");

        double distance = Double.parseDouble(mileTempText);

        RunLength temp = null;
        //TODO: set up this RunLength
        String textFromInput = totalTime.getText().toString();
        String[] elements = textFromInput.split(":");
        if(elements.length==3) {
            int hour = Integer.parseInt(elements[0]);
            int minute = Integer.parseInt(elements[1]);
            double seconds = Double.parseDouble(elements[2]);
            temp = new RunLength(hour,minute,seconds);
        }
        else if(elements.length==2){
            int minute = Integer.parseInt(elements[0]);
            double seconds = Double.parseDouble(elements[1]);
            temp = new RunLength(minute,seconds);
        }

        Run mRun = new Run(title.getText().toString(), runDate, weatherCondition.getText().toString(), humidityText.getText().toString(), weatherTemp.getText().toString(), windText.getText().toString(), distance, temp, paceText.getText().toString(), notes.getText().toString());

        SharedPreferences sharedPrefReader = NewRun.this.getSharedPreferences(accessKey,Context.MODE_PRIVATE);
        String RunListJSON = sharedPrefReader.getString(accessKey,"error");

        ArrayList<Run> savedRuns = gson.fromJson(RunListJSON, new TypeToken<ArrayList<Run>>(){}.getType());

        savedRuns.add(0,mRun);

        String json = gson.toJson(savedRuns);
        editor.putString(accessKey, json);
        editor.commit();

        mMediaPlayer.start();
        Intent i = new Intent(this, ViewSavedRuns.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanWatchButton) {
            Intent i = new Intent(this, ScanWatchActivity.class);
            startActivityForResult(i, SCAN_REQUEST_CODE);
        }
        if (v.getId() == R.id.cancelButton) {
            super.onBackPressed();
        }

        if(v.getId()==R.id.submitButton){
            saveData();
        }
        if (v.getId() == R.id.calendarButton) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(runDate);
            int mYear = cal.get(Calendar.YEAR);
            int mMonth = cal.get(Calendar.MONTH);
            final int mDay = cal.get(Calendar.DAY_OF_MONTH);
            final int mHour = cal.get(Calendar.HOUR);
            final int mMinute = cal.get(Calendar.MINUTE);
            final int selectedDate[] = {0, 0, 0, 0, 0};
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDate[0] = year;
                    selectedDate[1] = month;
                    selectedDate[2] = dayOfMonth;
                    TimePickerDialog timePickerDialog = new TimePickerDialog(NewRun.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            selectedDate[3] = hourOfDay;
                            selectedDate[4] = minute;
                            Calendar cal;
                            cal = new GregorianCalendar();
                            cal.set(selectedDate[0], selectedDate[1], selectedDate[2], selectedDate[3], selectedDate[4]);
                            runDate = cal.getTime();
                            updateTimeDate();
                        }
                    }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v.getId() == R.id.weatherEditButton) {
            if (weatherCondition.isFocusableInTouchMode()) {
                weatherCondition.setFocusableInTouchMode(false);
                weatherTemp.setFocusableInTouchMode(false);
                humidityText.setFocusableInTouchMode(false);
                windText.setFocusableInTouchMode(false);
                Toast.makeText(this, "Fields are now locked", Toast.LENGTH_SHORT).show();


            } else {
                weatherCondition.setFocusableInTouchMode(true);
                weatherTemp.setFocusableInTouchMode(true);
                humidityText.setFocusableInTouchMode(true);
                windText.setFocusableInTouchMode(true);
                Toast.makeText(this, "Fields are now editable", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.RunEditButton) {
            if (mileText.isFocusableInTouchMode()) {
                mileText.setFocusableInTouchMode(false);
                totalTime.setFocusableInTouchMode(false);
                paceText.setFocusableInTouchMode(false);
                Toast.makeText(this, "Fields are now locked", Toast.LENGTH_SHORT).show();
            } else {
                mileText.setFocusableInTouchMode(true);
                totalTime.setFocusableInTouchMode(true);
                paceText.setFocusableInTouchMode(true);
                Toast.makeText(this, "Fields are now editable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String textReturned = data.getStringExtra(ScanWatchActivity.TextBlockObject);
                    processScannableTextInput(textReturned);
                }

            }
        }
    }

    private void processScannableTextInput(String input) {
        //this method will try and process the input from the Scanner. If there is an issue parsing the data, it will let the user know and prompt manual input
        String lines[] = input.split("\\r?\\n");
        //in general remove characters, should only be numbers
        //possibly add places to replace semicolons or misread colons with colons
        boolean status = true;
        String totalTimeString = null;
        String distance = null;
        String pace = null;
        try {
            totalTimeString = lines[0];
            distance = lines[1];
            pace = lines[2];

        } catch (ArrayIndexOutOfBoundsException e) {
            status = false;
            Log.d(NewRun.class.getSimpleName(), "Error parsing Scannable Data");
            Toast.makeText(this, "Error parsing scan data", Toast.LENGTH_LONG).show();
        }
        if (status) {
            if (totalTimeString.contains("D")) {
                totalTimeString.replace('D', '0');
            }
            totalTime.setText(totalTimeString);
            mileText.setText(distance);
            paceText.setText(pace);
        }
    }

    private void updateTimeDate() {
        SimpleDateFormat Dateformatter = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat timeformatter = new SimpleDateFormat("h:mm a");
        String dateoutput = Dateformatter.format(runDate);
        String timeoutput = timeformatter.format(runDate);
        dateText.setText(dateoutput);
        timeText.setText(timeoutput);
    }

    private void releaseMediaPlayer(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
