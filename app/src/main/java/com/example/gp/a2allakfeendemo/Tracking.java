package com.example.gp.a2allakfeendemo;

import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.example.gp.a2allakfeendemo.Data.TrackerJSON;
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

    public Tracking(GoogleMap outMap) {
        this.outMap = outMap;
        this.dBmanager = new DBmanager();
    }

    // get bus location and display it on the map
    public void TrackBus(String busNumber){
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
                //TODO:Select the nearest two buses
                try {
                    if (result != null) {
                        TrackerJSON tracker_json = new TrackerJSON();

                        JSONObject jsonObj = new JSONObject(result);
                        JSONArray positions = jsonObj.getJSONArray("positions");
                        ArrayList<LatLng> UpadatedLocation = new ArrayList<LatLng>();

                        for(int i=0; i<positions.length(); i++){
                            JSONObject pos = positions.getJSONObject(i);
                            tracker_json.latitude = pos.getDouble("latitude");
                            tracker_json.longitude = pos.getDouble("longitude");
                            UpadatedLocation.add(new LatLng(tracker_json.latitude, tracker_json.longitude));
                            outMap.addMarker(new MarkerOptions().position(UpadatedLocation.get(i)));
                            outMap.moveCamera(CameraUpdateFactory.newLatLng(UpadatedLocation.get(i)));
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
