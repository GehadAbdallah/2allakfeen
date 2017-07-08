package com.example.gp.a2allakfeendemo;

import android.util.Log;

import com.example.gp.a2allakfeendemo.Data.BusStation;
import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.example.gp.a2allakfeendemo.Data.TrackerJSON;
import com.example.gp.a2allakfeendemo.Data.distance;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by Gehad on 5/20/2017.
 */

public class Tracking {
    GoogleMap outMap;
    DBmanager dBmanager;
    ArrayList<TrackerJSON> Buses;
    ArrayList<BusStation> stations;
    ArrayList<distance> user_stat_dists = new ArrayList<distance>();

    public Tracking(GoogleMap outMap) {
        this.outMap = outMap;
        this.dBmanager = new DBmanager();
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private void getDistance_maps(LatLng user_source){
        final String link = "https://maps.googleapis.com/maps/api/distancematrix/json";

        //parameters needed by api : origins=41.43206,-81.38992,destinations,key,departure_time=now,mode=walking
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
//        String origins = Double.toString(user_location.latitude)+","+Double.toString(user_location.longitude);
        String origins = "29.980379,31.212999";
        String destination = "";
        for (int i=0;i<stations.size();i++) {
            if(i==0)
                destination += Double.toString(stations.get(i).latitude)+","+Double.toString(stations.get(i).longitude);
            else
            {
                destination +="|";
                destination += Double.toString(stations.get(i).latitude)+","+Double.toString(stations.get(i).longitude);
            }
        }
        parameters.add(new Parameter("origins",origins ));
        parameters.add(new Parameter("key","AIzaSyDiDgy4erJKrAHGJJzYG_cFkh17qJjQiN8"));
        parameters.add(new Parameter("destinations",destination));
        Needle.onBackgroundThread().execute((new  UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Log.v("enter google","send request");
                String result = dBmanager.sendRequest("GET",false,link,parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.v("Tracking_API_result:", result);
                ParseJsonDist(result);
                //get nearest station to source
                distance nearest_to_user_dist = new distance();
                nearest_to_user_dist.dist = 1000000;
                for (distance stat:user_stat_dists) {
                    if (stat.dist < nearest_to_user_dist.dist) {
                        nearest_to_user_dist.dist = stat.dist;
                        nearest_to_user_dist.id = stat.id;
                    }
                }
                BusStation stat_nearest_user = new BusStation();
                for (BusStation stat:stations) {
                    if(stat.order == nearest_to_user_dist.id){
                        stat_nearest_user = stat;
                    }
                }
                //nearest 5 busses to user nearest station

            }
        }));
    }

    private void ParseJsonDist(String result){
        try {
            distance dist_temp = new distance();
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            if (status.equals("OK") ){
                JSONArray rows = jsonObject.getJSONArray("rows");
                JSONObject row = rows.getJSONObject(0);
                JSONArray elements = row.getJSONArray("elements");
                JSONObject element;
                String element_status;
                for (int i=0;i<stations.size();i++) {
                    element = elements.getJSONObject(i);
                    element_status = element.getString("status");
                    if (element_status.equals("OK")) {
                        JSONObject distanceJson = element.getJSONObject("distance");
                        dist_temp.dist = distanceJson.getDouble("value");
                        dist_temp.id = stations.get(i).order;
                        user_stat_dists.add(dist_temp);
                    } else {
                        Log.e(Tracking.class.getName(), element_status);
                    }
                }

            }else{
                Log.e(Tracking.class.getName(),status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void DisplayBuses(ArrayList<TrackerJSON> busses,int length){
        ArrayList<LatLng> UpadatedLocation = new ArrayList<LatLng>();
        for (int i=0; i<length; i++) {
            UpadatedLocation.add(new LatLng(busses.get(i).current_latitude, busses.get(i).current_longitude));
            outMap.addMarker(new MarkerOptions().position(UpadatedLocation.get(i)));
            outMap.moveCamera(CameraUpdateFactory.newLatLng(UpadatedLocation.get(i)));
        }

    }

    private void ParseJSON(String json){
        try {
            Buses = new ArrayList<>();
            TrackerJSON currentBus = new TrackerJSON();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray positions = jsonObject.getJSONArray("positions");
//            JSONArray old_positions = jsonObject.getJSONArray("old_positions");
            for (int i=0;i<positions.length();i++){
                JSONObject pos = positions.getJSONObject(i);
                currentBus.bus_id = pos.getInt("bus_id");
                currentBus.current_latitude = pos.getDouble("latitude");
                currentBus.current_longitude = pos.getDouble("longitude");
                currentBus.last_visited_station_order = pos.getInt("nearest_order");
                currentBus.prev_to_last_station_order = pos.getInt("previous_order");
                Buses.add(currentBus);
            }
//            for (int i=0 ; i<old_positions.length(); i++){
//                JSONObject old_pos = old_positions.getJSONObject(i);
//                for (int j=0 ; j<Buses.size() ; j++)
//                    if (old_pos.getInt("bus_id") == Buses.get(j).bus_id) {
//                        Buses.get(j).old_latitude = old_pos.getDouble("latitude");
//                        Buses.get(j).old_longitude = old_pos.getDouble("longitude");
//                        break;
//                    }
//            }
            stations = new ArrayList<>();
            BusStation bus_station = new BusStation();
            JSONArray bus_stations = jsonObject.getJSONArray("stations");
            for (int i=0 ; i<bus_stations.length(); i++){
                JSONObject station = bus_stations.getJSONObject(i);
                bus_station.order = station.getInt("order");
                bus_station.latitude = station.getDouble("latitude");
                bus_station.longitude = station.getDouble("longitude");
                stations.add(bus_station);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }
    // get bus location and display it on the map
    public void TrackBus(String busNumber, final LatLng user_location){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("bus_number",busNumber));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("GET",true,"fetch_location.php",parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.v("result1","request done");
                if (result != null) {
                    Log.v("result1: ",result);
                    //parse the json and save it in 2 arrays , 1st for busses , 2nd for bus_stations.
                    ParseJSON(result);

                    //get the nearest station to the user
                    getDistance_maps(user_location);
                    Log.v(Tracking.class.getName(),result);

                    //TODO: construct request to direction api with waypoints start from nearest stations.
                    //TODO: order the busses according to the nearest to the user station and display 5 busses only

                }
            }
        });

    }
}
