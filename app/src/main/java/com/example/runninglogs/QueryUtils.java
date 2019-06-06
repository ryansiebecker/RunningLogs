package com.example.runninglogs;

import android.support.annotation.ArrayRes;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    private static URL createURL(String stringURL){
        URL url = null;
        try{
            url = new URL(stringURL);
        }
        catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL", e);
            return null;
        }
        return url;
    }

    public static ArrayList<String> fetchWeatherData(String requestUrl) {
        URL url = createURL(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        return extractWeatherData(jsonResponse);
    }

    public static ArrayList<String> extractWeatherData(String JSONResponse){
        ArrayList<String> weatherData = new ArrayList<>();
        try {
            JSONObject baseJSONobject = new JSONObject(JSONResponse);
            JSONArray weatherArray = baseJSONobject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String condition = weather.getString("main");
            JSONObject mainObject = baseJSONobject.getJSONObject("main");
            JSONObject windObject = baseJSONobject.getJSONObject("wind");
            Double temp = mainObject.getDouble("temp");
            int humidity = mainObject.getInt("humidity");
            double wind = windObject.getDouble("speed");
            weatherData.add(condition);
            weatherData.add(""+temp);
            weatherData.add(""+humidity);
            weatherData.add(""+wind);
        }
        catch (JSONException e){
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        return weatherData;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try{
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.connect();
            if(connection.getResponseCode() == 200){
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else{
                Log.e(LOG_TAG, "Error response code: " + connection.getResponseCode());
            }

        }
        catch(IOException e){
            Log.e(LOG_TAG, "Problem retreiving book JSON results", e);
        }
        finally{
            if(connection!=null){
                connection.disconnect();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

}
