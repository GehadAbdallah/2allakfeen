package com.example.gp.a2allakfeendemo;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.example.gp.a2allakfeendemo.Data.SignJSON;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;

import java.util.ArrayList;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by Gehad on 5/19/2017.
 */

public class Controller {
    protected DBmanager dBmanager;
    public static int currentUser;
    public Controller() {
        this.dBmanager = new DBmanager();
    }

    public boolean SignIn(String user_name, String password ,final View view){
        //connect to model and verify that user exists in database
        //TODO:use Needle "like in function TruckBus in Tracking class" , inside the doWork function use sendRequest function with the name of php signIn file
        //TODO: and in the Do ui related work , add the commented code in SignIn.java and edit it to render the ui
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
                    final Gson gson = new Gson();
                    SignJSON signIn_success = gson.fromJson(result,SignJSON.class);
                    if(signIn_success.user_id != -1){
                        currentUser = signIn_success.user_id;

                        view.getContext().startActivity(new Intent(view.getContext(),MapsActivity.class));
                    }
                    else{
//                        Toast toast = Toast.makeText(appContext,"username or password are wrong.", Toast.LENGTH_LONG);
//                        toast.setGravity(Gravity.CENTER, 0, 0);
//                        toast.show();
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

    public void SignUp (String user_name, String email, String password,final View view){
        //connect to model and add user to database
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        Log.v("Controller",user_name);
        parameters.add(new Parameter("name",user_name));
        parameters.add(new Parameter("email",email));
        parameters.add(new Parameter("password",password));
        //TODO: like in Sign in
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"signup.php",parameters);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                //mSomeTextView.setText("result: " + result);
                Log.d("OnPOSTEXECUTE","Enter");
                if (result != null){
                    //TODO: if inserted succefully go to the maps activity
                    Log.v("result= ",result);
                    final Gson gson = new Gson();
                    SignJSON signUp_success = gson.fromJson(result,SignJSON.class);
                    if(signUp_success.user_id != -1){
                        currentUser = signUp_success.user_id;
                        view.getContext().startActivity(new Intent(view.getContext(),MapsActivity.class));
                    }
                    else{
                        Toast toast = Toast.makeText(view.getContext(),"Database error, try again.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        });
    }
    public void Track(GoogleMap map,String busNumber){

        Tracking Tracker = new Tracking(map);
        Tracker.TrackBus(busNumber);
    }
}
