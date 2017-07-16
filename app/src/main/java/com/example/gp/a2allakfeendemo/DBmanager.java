package com.example.gp.a2allakfeendemo;

import android.net.Uri;
import android.util.Log;

import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Gehad on 5/19/2017.
 */

public class DBmanager {

    String HOST_NAME = "http://ec2-54-200-210-102.us-west-2.compute.amazonaws.com/server_connect/";
    String DISTRICT_HOST_NAME = "https://maps.googleapis.com/maps/api/geocode/json?";
    String TIME_HOST_NAME = "https://maps.googleapis.com/maps/api/directions/json?";
    //To send get request ( parameters are not confidential )
    protected String sendRequest(String requestMethod,boolean ourServer,String Link,ArrayList<Parameter> parameters){
        String link;
        if (ourServer)
            link = HOST_NAME+Link;
        else
            link = Link;

        try {
                //add parameters to the link
                String url_str=link;
                if (parameters != null){
                    url_str += "?";
                    int i=0;
                    for (Parameter param:parameters) {
                        if (i==0)
                            url_str += (param.getName() + "=" + param.getValue());
                        else
                            url_str += ("&" + param.getName() + "=" + param.getValue());
                        i++;
                    }
                }
                //Connection
                Log.d("final_url",url_str);
                Uri builtUri = Uri.parse(url_str);
                URL url = new URL(builtUri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(requestMethod);
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                String result = buffer.toString();
                Log.d("DBManager1", result);
                return result;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
            }
        return null;
    }

    public String sendGetRequest(String fileName){
        String link = HOST_NAME+fileName;
        try {
            //Connection
            Uri builtUri = Uri.parse(link);
            URL url = new URL(builtUri.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            String result = buffer.toString();
            Log.d("DBManager1", result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }
        return null;
    }

    public String sendGetDistrict(LatLng latLng){
        Log.d("DBManager","IN sendGetDistrict");
        String lat = Double.toString(latLng.latitude);
        String lng = Double.toString(latLng.longitude);
        String latlong = "latlng="+lat+","+lng;
        String API_Key = "&key=AIzaSyBW3jV6YloLTAe2jHifD-ldieQSQEnIAN8";
        String link = DISTRICT_HOST_NAME+latlong+API_Key;
        Log.d("link",link);
        //String link = HOST_NAME+fileName;
        try {
            //Connection
            Uri builtUri = Uri.parse(link);
            URL url = new URL(builtUri.toString());
            Log.d("URL",url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("Connection",Integer.toString(urlConnection.getResponseCode()));
//            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            String result = buffer.toString();
            Log.d("DBManager1", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            String err = (e.getMessage()==null)?"map failed":e.getMessage();
            Log.e("map-err2:",err);
        }
        return null;
    }

    public String sendGetTime(String Origin,String dest,ArrayList<LatLng> WayPoints, boolean Bus){
        /*https://maps.googleapis.com/maps/api/directions/json?origin=29.9533078%2C31.2629646&destination=29.834789%2C31.298134&mode=walking&key=AIzaSyBW3jV6YloLTAe2jHifD-ldieQSQEnIAN8*/
        Log.d("DBManager","IN sendGetTime");
        String mode;
        String departure_time = "departure_time=now&";
        if (!Bus)
            mode = "walking";
        else
            mode = "driving";
        String wayPoints = "";
        if (WayPoints.size() > 0){
            wayPoints = "&waypoints=";
            String wayPoint;
            for (int i = 0 ; i < WayPoints.size(); i++){
                wayPoint = Double.toString(WayPoints.get(i).latitude)+"%2C"+Double.toString(WayPoints.get(i).longitude);
                wayPoints += "via:" +wayPoint;
                if(i != WayPoints.size()-1){
                    wayPoints += "|";
                }
            }
        }
        String API_Key = "&key=AIzaSyBW3jV6YloLTAe2jHifD-ldieQSQEnIAN8";
        String link="";
        if (WayPoints.size() == 0)
            link = TIME_HOST_NAME+departure_time+"mode="+mode+"&origin="+Origin+"&destination="+dest+API_Key;
        else
            link = TIME_HOST_NAME+departure_time+"mode="+mode+"&origin="+Origin+"&destination="+dest+wayPoints+API_Key;
        Log.d("link",link);
        //String link = HOST_NAME+fileName;
        try {
            //Connection
            Uri builtUri = Uri.parse(link);
            URL url = new URL(builtUri.toString());
            Log.d("URL",url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("Connection",Integer.toString(urlConnection.getResponseCode()));
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            String result = buffer.toString();
            Log.d("DBManager1", result);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String err = (e.getMessage()==null)?"map failed":e.getMessage();
            Log.e("map-err2:",err);
        }
        return null;
    }


}
