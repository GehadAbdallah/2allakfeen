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
import java.util.Timer;

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
    BusStation userNearestStation;
    ArrayList<TrackerJSON> Buses;
    ArrayList<BusStation> stations;
    ArrayList<TrackerJSON> busesDisplayed = new ArrayList<>();

    public Tracking(GoogleMap outMap, View view) {
        this.outMap = outMap;
        this.outView = view;
        this.dBmanager = new DBmanager();
    }


    private  TrackerJSON getBusInfo(int index){

        final String link = "https://maps.googleapis.com/maps/api/directions/json";
        final TrackerJSON currentBus = Buses.get(index);
        int step = currentBus.last_visited_station_order - currentBus.prev_to_last_station_order;
        int next_station_order = currentBus.last_visited_station_order + step;
        //for tests only//////////////////////////////////
//        userNearestStation.order = next_station_order + step;
//        for (int i=0 ; i<3 ; i++) {
//            if (userNearestStation.order != max_station_order && userNearestStation.order!= min_station_order)
//                userNearestStation.order = next_station_order + step;
//
//        }
//        for (BusStation stat : stations) {
//            if (stat.order == userNearestStation.order)
//                userNearestStation = stat;
//        }
        ////////////////////////////
        //parameters needed by api
        //origin=41.43206,-81.38992 (source location: bus location) , destination (nearest station to user's source location)
        //Key,waypoints preceded by via: (stations between Bus and nearest station),departure_time= now
        final ArrayList<Parameter> parameters = new ArrayList<>();
        String origin =Double.toString(currentBus.current_latitude)+","+Double.toString(currentBus.current_longitude);
        String destination = Double.toString(userNearestStation.latitude)+","+Double.toString(userNearestStation.longitude);
        String key = "AIzaSyDiDgy4erJKrAHGJJzYG_cFkh17qJjQiN8";
        String departure_time = "now";
        String waypoints="";
        //construct waypoints parameter
        int i=0;
        Log.e("step",Integer.toString(step));
        Log.e("next_station_order",Integer.toString(next_station_order));
        Log.e("nearestStatUser.order",Integer.toString(userNearestStation.order));
        while (next_station_order != userNearestStation.order){
            Log.e("inside loop","inside loop");
            for (int j=0;j<stations.size();j++) {
                BusStation stat = stations.get(j);
                Log.e("Tracking","stations:loop");
                Log.e("station_order",Integer.toString(stations.get(j).order));
                if (stat.order == next_station_order) {
                    Log.e("Tracking", "order=next_stat_order");
                    if (i == 0) {

                        waypoints += ("via:" + stat.latitude + "," + stat.longitude);
                        i++;
                        Log.e("Tracking_i", Integer.toString(i));
                        Log.e("Tracking_waypoints", waypoints);
                    } else {
                        Log.e("Tracking_i_else", Integer.toString(i));
                        waypoints += ("|via:" + stat.latitude + "," + stat.longitude);
                    }
                }
            }
            next_station_order = next_station_order+step;
            Log.e("waypoints",waypoints);
            Log.e("next_station_order",Integer.toString(next_station_order));
        }

        //add parameters
        parameters.add(new Parameter("origin",origin));
        parameters.add(new Parameter("destination",destination));
        parameters.add(new Parameter("key",key));
        parameters.add(new Parameter("departure_time",departure_time));
        parameters.add(new Parameter("waypoints",waypoints));
        String result = dBmanager.sendRequest("GET",false,link,parameters);
        try {
            //parse the response
            JSONObject json = new JSONObject(result);
            JSONObject legs = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            JSONArray steps = legs.getJSONArray("steps");
            ArrayList<LatLng> polylines = polyObject.getDirectionPolylines(steps);
            int dist = legs.getJSONObject("distance").getInt("value");
            String duration = legs.getJSONObject("duration_in_traffic").getString("text");
            currentBus.distance_to_nearest = dist;
            currentBus.duration_text_to_nearest = duration;
            currentBus.route_polylines = polylines;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentBus;



    }

    //get the nearest station to the user's source by getting the distances between the source and each station and get the min distance
    private BusStation getUserNearestStation_maps(LatLng user_source) {
        final String link = "https://maps.googleapis.com/maps/api/distancematrix/json";
        //parameters needed by api : origins=41.43206,-81.38992,destinations,key,departure_time=now,mode=walking
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
//        String origins = Double.toString(user_location.latitude)+","+Double.toString(user_location.longitude);
//        String origins = "29.980379,31.212999"; //hard coded for tests
        String origins = Double.toString(user_source.latitude)+","+Double.toString(user_source.longitude);
        String destination = "";
        for (int i = 0; i < stations.size(); i++) {
            if (i == 0)
                destination += Double.toString(stations.get(i).latitude) + "," + Double.toString(stations.get(i).longitude);
            else {
                destination += "|";
                destination += Double.toString(stations.get(i).latitude) + "," + Double.toString(stations.get(i).longitude);
            }
        }
        parameters.add(new Parameter("origins", origins));
        parameters.add(new Parameter("key", "AIzaSyDiDgy4erJKrAHGJJzYG_cFkh17qJjQiN8"));
        parameters.add(new Parameter("destinations", destination));
        String result = dBmanager.sendRequest("GET", false, link, parameters);
        BusStation stat_nearest_user = new BusStation();

        if (result != null) {
            Log.v("Distance_API_result:", result);
            //parse the result from distance matrix API and save it in user_stat_dists
            ArrayList<distance> user_stat_dists = new ArrayList<>();
            user_stat_dists = ParseJsonDist(result);
            if (user_stat_dists.size() > 0) {

                //get nearest station to source
                distance nearest_to_user_dist = new distance();
                nearest_to_user_dist.dist = 1000000;
                for (distance stat : user_stat_dists) {
                    if (stat.dist < nearest_to_user_dist.dist) {
                        nearest_to_user_dist.dist = stat.dist;
                        nearest_to_user_dist.id = stat.id;
                    }
                }


                for (BusStation stat : stations) {
                    if (stat.order == nearest_to_user_dist.id) {
                        stat_nearest_user.order = stat.order;
                        stat_nearest_user.longitude = stat.longitude;
                        stat_nearest_user.latitude = stat.latitude;
                        stat_nearest_user.distance = stat.distance;
                        break;
                    }
                }
            }
        }
        return stat_nearest_user;
    }
    private ArrayList<TrackerJSON> getMovingForwardBuses(){
                //discard the busses that doesn't move forward to the nearest station to the user
                int step_sign;
                ArrayList<TrackerJSON> movingForwardBuses = new ArrayList<TrackerJSON>();
                for (TrackerJSON bus : Buses) {
                    step_sign = bus.last_visited_station_order - bus.prev_to_last_station_order;
                    if ((step_sign == 1 && userNearestStation.order > bus.last_visited_station_order) || (step_sign == -1 && userNearestStation.order < bus.last_visited_station_order))
                        movingForwardBuses.add(bus);
                }
                return movingForwardBuses;
            }

    private ArrayList<distance> ParseJsonDist(String result){
        ArrayList<distance> user_stat_dists = new ArrayList<>();
        try {
            distance dist_temp;
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
                        dist_temp = new distance();
                        dist_temp.dist = distanceJson.getDouble("value");
                        dist_temp.id = stations.get(i).order;
                        user_stat_dists.add(i,dist_temp);
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
        return user_stat_dists;
    }
    private void DisplayBuses(ArrayList<TrackerJSON> busses,int length){
        ArrayList<LatLng> UpadatedLocation = new ArrayList<LatLng>();
        //draw the nearest BusStation
        outMap.addMarker(new MarkerOptions()
                        .position(new LatLng(userNearestStation.latitude,userNearestStation.longitude))
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
            TrackerJSON currentBus;
            JSONObject jsonObject = new JSONObject(json);
            JSONArray positions = jsonObject.getJSONArray("positions");
//            JSONArray old_positions = jsonObject.getJSONArray("old_positions");
            for (int i=0;i<positions.length();i++){
                currentBus = new TrackerJSON();
                JSONObject pos = positions.getJSONObject(i);
                currentBus.bus_id = pos.getInt("bus_id");
                currentBus.current_latitude = pos.getDouble("latitude");
                currentBus.current_longitude = pos.getDouble("longitude");
                currentBus.last_visited_station_order = pos.getInt("nearest_order");
                currentBus.prev_to_last_station_order = pos.getInt("previous_order");
                Buses.add(i,currentBus);
            }

            stations = new ArrayList<>();
            BusStation bus_station;
            JSONArray bus_stations = jsonObject.getJSONArray("stations");
            for (int i=0 ; i<bus_stations.length(); i++){
                bus_station = new BusStation();
                JSONObject station = bus_stations.getJSONObject(i);
                bus_station.order = station.getInt("order");
                bus_station.latitude = station.getDouble("latitude");
                bus_station.longitude = station.getDouble("longitude");
                //stations.add(bus_station);
                stations.add(i,bus_station);
                Log.e("ParseJSON,order",Integer.toString(bus_station.order));
            }
            for (int i=0 ; i<stations.size();i++){
                Log.e("stations_array_order",Integer.toString(stations.get(i).order));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void TrackBus(String busNumber, final LatLng SourceLocation, final Timer myTimer){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("bus_number",busNumber));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String response;
                String result = dBmanager.sendRequest("GET",true,"fetch_location.php",parameters);
                Log.v("result1","request done");
                if (result != null) {
                    Log.v("result1: ",result);
                    //parse the json and save it in 2 arrays , 1st for busses , 2nd for bus_stations.
                    ParseJSON(result);

                    if (Buses.size() > 0) {
                        //set max_station_order & min_station_order
                        max_station_order = -1;
                        min_station_order = 1000000;
                        for (BusStation stat : stations) {
                            if (stat.order < min_station_order)
                                min_station_order = stat.order;
                            if (stat.order > max_station_order)
                                max_station_order = stat.order;
                        }


                        //get the nearest station to the user and determine the busses direction
                        userNearestStation = getUserNearestStation_maps(SourceLocation);

                        //get Nearest moving forward busses
                        ArrayList<TrackerJSON> movingForwardBuses = getMovingForwardBuses();
                        if (movingForwardBuses.size() > 0) {
                            //nearest 5 busses to user nearest station
                            for (int i = 0; i < movingForwardBuses.size(); i++)
                                busesDisplayed.add(getBusInfo(i));
                            //sort the buses by the nearest
                            for (int i = 0; i < busesDisplayed.size(); i++) {
                                for (int j = i + 1; j < busesDisplayed.size(); j++)
                                    if (busesDisplayed.get(i).distance_to_nearest > busesDisplayed.get(j).distance_to_nearest) {
                                        TrackerJSON tempBus = busesDisplayed.get(i);
                                        busesDisplayed.set(i, busesDisplayed.get(j));
                                        busesDisplayed.set(j, tempBus);
                                    }
                            }

                            response = "success";
                        } else {
                            response = "mvp";
                            //for test only//////////////////////////////////////////////////////////////////////
//                            for (int i = 0; i < Buses.size(); i++)
//                                getBusInfo(i);
                            /////////////////////////////////////////////////////////////////////////////////////
                        }
                    }else{
                        response = "bnp";
                    }
                    return response;
                }
                return null;
            };


            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.v("result1","request done");
                if (result != null) {
                    Log.v("result1: ",result);
                    if(result.equals("success")) {
                        //display the busses
                        if (busesDisplayed.size() < 3)
                            DisplayBuses(busesDisplayed,busesDisplayed.size());
                         else
                            DisplayBuses(busesDisplayed,3);


                    }else if (result.equals("mvp")) {
                        myTimer.cancel();
                        Toast toast = Toast.makeText(outView.getContext(), "No Busses comming towards you at current time", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }

                }else {
                    myTimer.cancel();
                    Toast toast = Toast.makeText(outView.getContext(), "This Busline number is not tracked,it will be available soon.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

    }
}
