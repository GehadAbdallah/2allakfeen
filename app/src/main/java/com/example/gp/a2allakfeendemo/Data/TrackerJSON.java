package com.example.gp.a2allakfeendemo.Data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Eman on 24/04/2017.
 */
public class TrackerJSON {
    public int bus_id;
    public double current_latitude;
    public double current_longitude;
    public int last_visited_station_order;
    public int prev_to_last_station_order;
    public int distance_to_nearest;
    public String duration_text_to_nearest;
    public ArrayList<LatLng> route_polylines;

}
