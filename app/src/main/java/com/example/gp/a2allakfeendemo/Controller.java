package com.example.gp.a2allakfeendemo;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by Gehad on 5/19/2017.
 */

public class Controller {
    protected DBmanager dBmanager;

    public Controller() {
        this.dBmanager = new DBmanager();
    }

    public boolean SignIn(String user_name, String password , View view, Context appContext){
        //connect to model and verify that user exists in database
        //TODO:use Needle "like in function TruckBus in Tracking class" , inside the doWork function use sendRequest function with the name of php signIn file
        //TODO: and in the Do ui related work , add the commented code in SignIn.java and edit it to render the ui
        return true;
    }

    public void SignUp (String user_name, String email, String password, View view, Context appContext){
        //connect to model and add user to database
        final ArrayList<Object> parameters = new ArrayList<>();
        parameters.add(user_name);
        parameters.add(email);
        //TODO: like in Sign in
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendPostRequest("signup.php",parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                //mSomeTextView.setText("result: " + result);
                Log.d("OnPOSTEXECUTE","Enter");
                if (result != null){
                    //TODO: if inserted succefully go to the maps activity
                    Log.v("result= ",result);
                }
            }
        });
    }
    public void Track(GoogleMap map,String busNumber){

        Tracking Tracker = new Tracking(map);
        Tracker.TrackBus(busNumber);
    }
}
