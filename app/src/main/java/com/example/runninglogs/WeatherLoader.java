package com.example.runninglogs;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

public class WeatherLoader extends AsyncTaskLoader<ArrayList<String>> {
    private String mURL;
    public WeatherLoader(Context context, String url){
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {
        if(mURL == null)
            return null;
        return QueryUtils.fetchWeatherData(mURL);
    }
}
