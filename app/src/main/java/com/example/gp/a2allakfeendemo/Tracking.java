package com.example.gp.a2allakfeendemo;

import android.util.Log;

import com.example.gp.a2allakfeendemo.Data.TrackerJSON;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

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

        //TODO:should add the functionality to track a certain bus number
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendGetRequest("demo.php");
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                //mSomeTextView.setText("result: " + result);
                Log.d("OnPOSTEXECUTE","Enter");
                if (result != null){
                    final Gson gson = new Gson();
                    TrackerJSON tracker_json = gson.fromJson(result, TrackerJSON.class);
                    LatLng UpadatedLocation = new LatLng(tracker_json.latitude, tracker_json.longitude);
                    outMap.addMarker(new MarkerOptions().position(UpadatedLocation).title("You are here"));
                    outMap.moveCamera(CameraUpdateFactory.newLatLng(UpadatedLocation));
                }
            }
        });

    }
}
