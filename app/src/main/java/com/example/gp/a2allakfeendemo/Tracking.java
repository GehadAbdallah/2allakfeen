package com.example.gp.a2allakfeendemo;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.gp.a2allakfeendemo.Data.BusStation;
import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.example.gp.a2allakfeendemo.Data.Polylines;
import com.example.gp.a2allakfeendemo.Data.TrackerJSON;
import com.example.gp.a2allakfeendemo.Data.distance;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    View outView;
    Polylines polyObject = new Polylines();
    DBmanager dBmanager;
    int max_station_order;
    int min_station_order;
    ArrayList<TrackerJSON> Buses;
    ArrayList<BusStation> stations;
    ArrayList<TrackerJSON> movingForwardBuses;
    ArrayList<TrackerJSON> busesDisplayed = new ArrayList<>();
    ArrayList<distance> user_stat_dists = new ArrayList<distance>();

    public Tracking(GoogleMap outMap, View view) {
        this.outMap = outMap;
        this.outView = view;
        this.dBmanager = new DBmanager();
    }


    private void getNearestBusses(final int index,BusStation nearestStatUser){

        final String link = "https://maps.googleapis.com/maps/api/directions/json";
        final TrackerJSON currentBus = Buses.get(index);
        int step = currentBus.last_visited_station_order - currentBus.prev_to_last_station_order;
        int next_station_order = currentBus.last_visited_station_order + step;
        //for tests only//////////////////////////////////
        nearestStatUser.order = next_station_order + step;
        for (int i=0 ; i<3 ; i++) {
            if (nearestStatUser.order != max_station_order && nearestStatUser.order!= min_station_order)
                nearestStatUser.order = next_station_order + step;

        }
        for (BusStation stat : stations) {
            if (stat.order == nearestStatUser.order)
                nearestStatUser = stat;
        }
        final BusStation nearestStatFinal = nearestStatUser;
        ////////////////////////////
        //parameters needed by api
        //origin=41.43206,-81.38992 (source location: bus location) , destination (nearest station to user's source location)
        //Key,waypoints preceded by via: (stations between Bus and nearest station),departure_time= now
        final ArrayList<Parameter> parameters = new ArrayList<>();
        String origin =Double.toString(currentBus.current_latitude)+","+Double.toString(currentBus.current_longitude);
        String destination = Double.toString(nearestStatUser.latitude)+","+Double.toString(nearestStatUser.longitude);
        String key = "AIzaSyDiDgy4erJKrAHGJJzYG_cFkh17qJjQiN8";
        String departure_time = "now";


        String waypoints="";
        int i=0;
        while (next_station_order != nearestStatUser.order){
            Log.v("inside loop","inside loop");
            for (BusStation stat:stations) {
                Log.v("Tracking","stations:loop");
                if (stat.order == next_station_order)
                    Log.v("Tracking","order=next_stat_order");
                    if (i==0) {

                        waypoints += ("via:" + stat.latitude + "," + stat.longitude);
                        i++;
                        Log.v("Tracking_i",Integer.toString(i));
                        Log.v("Tracking_waypoints",waypoints);

                    }
                    else {
                        Log.v("Tracking_i_else",Integer.toString(i));
                        waypoints += ("|via:" + stat.latitude + "," + stat.longitude);
                    }
            }
            next_station_order = next_station_order+step;

        }
        parameters.add(new Parameter("origin",origin));
        parameters.add(new Parameter("destination",destination));
        parameters.add(new Parameter("key",key));
        parameters.add(new Parameter("departure_time",departure_time));
        parameters.add(new Parameter("waypoints",waypoints));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("GET",false,link,parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.v("Direction API result",result);
                try {
                    JSONObject json = new JSONObject(result);
                    JSONObject legs = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                    JSONArray steps = legs.getJSONArray("steps");
                    ArrayList<LatLng> polylines = polyObject.getDirectionPolylines(steps);
                    int dist = legs.getJSONObject("distance").getInt("value");
                    String duration = legs.getJSONObject("duration_in_traffic").getString("text");
                    currentBus.distance_to_nearest = dist;
                    currentBus.duration_text_to_nearest = duration;
                    currentBus.route_polylines = polylines;
                    busesDisplayed.add(currentBus);
//                    if (busesDisplayed.size() == movingForwardBuses.size()){
                        for (int i=0 ; i< busesDisplayed.size() ; i++) {
                            for (int j = i + 1; j < busesDisplayed.size(); j++)
                                if (busesDisplayed.get(i).distance_to_nearest > busesDisplayed.get(j).distance_to_nearest) {
                                    TrackerJSON tempBus = busesDisplayed.get(i);
                                    busesDisplayed.set(i, busesDisplayed.get(j));
                                    busesDisplayed.set(j, tempBus);
                                }
                        }
                        if (busesDisplayed.size() < 3)
                            DisplayBuses(busesDisplayed,busesDisplayed.size(),nearestStatFinal);
                         else
                            DisplayBuses(busesDisplayed,3,nearestStatFinal);

  //                  }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
                //discard the busses that doesn't move forward to the nearest station to the user
                int step_sign;
                movingForwardBuses = new ArrayList<TrackerJSON>();
                for (TrackerJSON bus:Buses) {
                    step_sign = bus.last_visited_station_order - bus.prev_to_last_station_order;
                    if ((step_sign == 1 && stat_nearest_user.order > bus.last_visited_station_order)||(step_sign == -1 && stat_nearest_user.order < bus.last_visited_station_order))
                        movingForwardBuses.add(bus);
                }
                if (movingForwardBuses.size() > 0) {
                    //nearest 5 busses to user nearest station
                    for (int i = 0; i < movingForwardBuses.size(); i++)
                        getNearestBusses(i, stat_nearest_user);
                }
                else
                {
                    Toast toast = Toast.makeText(outView.getContext(),"No Busses comming towards you at current time", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    for (int i = 0; i < Buses.size(); i++)
                        getNearestBusses(i, stat_nearest_user);
                }

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
    private void DisplayBuses(ArrayList<TrackerJSON> busses,int length,BusStation nearestStationUser){
        ArrayList<LatLng> UpadatedLocation = new ArrayList<LatLng>();
        //draw the nearest BusStation
        outMap.addMarker(new MarkerOptions()
                        .position(new LatLng(nearestStationUser.latitude,nearestStationUser.longitude))
                        .snippet("Nearest station").title("Nearest station to you"));
        //draw busses
        for (int i=0; i<length; i++) {
            UpadatedLocation.add(new LatLng(busses.get(i).current_latitude, busses.get(i).current_longitude));
            Log.v("bus duration",busses.get(i).duration_text_to_nearest);
            outMap.addMarker(new MarkerOptions()
                    .position(UpadatedLocation.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus2))
                    .title("Time to arrive to nearest station")
                    .snippet(busses.get(i).duration_text_to_nearest));
           // outMap.moveCamera(CameraUpdateFactory.newLatLng(UpadatedLocation.get(i)));
            polyObject.drawRouteOnMap(outMap,busses.get(i).route_polylines);
        }
        //outMap.animateCamera(CameraUpdateFactory.zoomTo(15));



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

                    //set max_station_order & min_station_order
                    max_station_order = -1;
                    min_station_order = 1000000;
                    for (BusStation stat:stations) {
                        if (stat.order < min_station_order)
                            min_station_order = stat.order;
                        if (stat.order > max_station_order)
                            max_station_order = stat.order;
                    }
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
