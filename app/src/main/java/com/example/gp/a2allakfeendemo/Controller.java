package com.example.gp.a2allakfeendemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.example.gp.a2allakfeendemo.Data.SignJSON;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import needle.Needle;
import needle.UiRelatedTask;

import static com.example.gp.a2allakfeendemo.WelcomeActivity.MyPREFERENCES;

/**
 * Created by Gehad on 5/19/2017.
 */

public class Controller {
    protected DBmanager dBmanager;
    public static int currentUser;
    public static String district;
    public static String bus_stations_result;
    public static String metro_stations_result;
    public static String time_result;
    public static String routes_result;
    public static String rate_result;
    SharedPreferences sharedpreferences;
    public Controller() {
        this.dBmanager = new DBmanager();
    }

    //This function makes sure the user trying to sign in is authorized
    public boolean SignIn(String user_name, String password ,final View view){
        //construct a list of parameters to be sent in the request to signin.php
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("user_name",user_name));
        parameters.add(new Parameter("password",password));

        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {

            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("GET",true,"signin.php",parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                if (result != null){
                    //parse the json result
                    final Gson gson = new Gson();
                    SignJSON signIn_success = gson.fromJson(result,SignJSON.class);
                    //if the user exists in the database with the password he provided
                    if(signIn_success.user_id != -1){
                        //save the current logged in user id in sharedprefrences.
                        sharedpreferences = view.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("CurrentUser",signIn_success.user_id);
                        editor.commit();
                        //redirect to MapsActivity
                        view.getContext().startActivity(new Intent(view.getContext(),MapsActivity.class));
                    }
                    else{
                        //alert the user that username or password are wrong.
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(view.getContext());
                        dlgAlert.setMessage("wrong password or username");
                        dlgAlert.setTitle("Error Message...");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                }
            }
        });
        return true;
    }

    //This function construct parameters to be sent in a request to signup.php to add new user
    //then renders the gui depending on the result
    public void SignUp (String user_name, String email, String password, final View view){

        //construct list of the parameters to be sent in the request
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("name",user_name));
        parameters.add(new Parameter("email",email));
        parameters.add(new Parameter("password",password));

        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"signup.php",parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                if (result != null){
                    try {


                        Log.v("result= ",result);
                        //parse the json result
                        final Gson gson = new Gson();
                        SignJSON signUp_success = gson.fromJson(result,SignJSON.class);
                        JSONObject signUpResponse = new JSONObject(result);
                        boolean UserNameExists = signUpResponse.getBoolean("UserNameExists");
                        if (UserNameExists == false) {
                            boolean emailExists = signUpResponse.getBoolean("emailExists");
                            if (emailExists == false) {
                                int user_id = signUpResponse.getInt("user_id");
                                if (user_id != -1) {
                                    //add the user id of the added user to the prefrences
                                    sharedpreferences = view.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putInt("CurrentUser", signUp_success.user_id);
                                    editor.commit();
                                    //redirect to maps Activity
                                    view.getContext().startActivity(new Intent(view.getContext(), MapsActivity.class));
                                } else {
                                    //if adding the new user failed , error message appear to the user
                                    Toast toast = Toast.makeText(view.getContext(), "Database error, try again.", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }else {
                                //if email existed before
                                Toast toast = Toast.makeText(view.getContext(), "Email is already signed up.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }else{
                            //if user name existed before
                            Toast toast = Toast.makeText(view.getContext(), "Username existed before, try different one or sign in.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void GetBusStations (){
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendGetRequest("busstations.php");
                //Log.d("doWork",result);
                bus_stations_result = result;
                return bus_stations_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                //mSomeTextView.setText("result: " + result);
                //Log.d("OnPOSTEXECUTE",result);
            }
        });
    }

    public void GetMetroStations (){
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendGetRequest("metrostations.php");
                Log.d("doWork",result);
                metro_stations_result = result;
                return metro_stations_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
            }
        });
    }

    public void GetRoutes (String src,String dst){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("src",src));
        parameters.add(new Parameter("dst",dst));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"getroutes.php",parameters);
                Log.d("doWork",result);
                routes_result = result;
                return routes_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
            }
        });
    }

    public void GetRates (int routeID){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("routeID",Integer.toString(routeID)));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"getrates.php",parameters);
                Log.d("doWork",result);
                rate_result = result;
                return rate_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
            }
        });
    }

    public void InsertRoute (String src,String dst,String routeTobeSaved){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("src",src));
        parameters.add(new Parameter("dst",dst));
        parameters.add(new Parameter("routeTobeSaved",routeTobeSaved));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"insertroute.php",parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.d("OnPOSTEXECUTE","Enter");
                if (result != null){
                    //TODO: if inserted succefully go to the maps activity
                    Log.v("result= ",result);
                }
            }
        });
    }

    public void Get_District (final LatLng latLng){
        Log.d("IN","Get_District CONTROLLER");
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Log.d("IN", "Get_District doWork");
                Log.e("doWork_district",Double.toString(latLng.latitude));
                String result = dBmanager.sendGetDistrict(latLng);
                Log.d("result", result);
                district = result;
                return district;
            }
            @Override
            protected void thenDoUiRelatedWork(String district) {
                Log.d("DISTRICT",district);
            }
        });
    }

    public void GetTime (final ArrayList<LatLng> Stations, final boolean Bus){
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String Origin, dest;
                ArrayList<LatLng> WayPoints = new ArrayList<LatLng>();
                Origin = Double.toString(Stations.get(0).latitude)+","+Double.toString(Stations.get(0).longitude);
                dest = Double.toString(Stations.get(Stations.size()-1).latitude)+","+Double.toString(Stations.get(Stations.size()-1).longitude);
                if (Stations.size() > 2){
                    for (int i = 1; i < Stations.size()-1; i++){
                        WayPoints.add(Stations.get(i));
                    }
                }
                String result = dBmanager.sendGetTime(Origin,dest,WayPoints,Bus);
                time_result = result;
                return time_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
            }
        });
    }

    public void GetSuggestions(LatLng Src, LatLng Dst, View v, Context c) {
        Suggestions sugObj = new Suggestions();
        sugObj.Get_Suggestions(Src,Dst,v,c);
    }
    //This function connect to Tracker model to track a certain bus number
    public void Track(GoogleMap map, final String busNumber, final LatLng user_location,View view){

        final Tracking Tracker = new Tracking(map,view);
        Tracker.TrackBus(busNumber,user_location);
//        Timer myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // call each second
//                Tracker.TrackBus(busNumber,user_location);
//            }
//        }, 0, 10000);

    }

}
