package com.example.gp.a2allakfeendemo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.gp.a2allakfeendemo.Data.Parameter;
import com.example.gp.a2allakfeendemo.GraphConstr.District;
import com.example.gp.a2allakfeendemo.GraphConstr.DistrictsList;
import com.example.gp.a2allakfeendemo.GraphConstr.Graph;
import com.example.gp.a2allakfeendemo.Route_Calculation.Route;
import com.example.gp.a2allakfeendemo.DBmanager;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by Eman on 12/07/2017.
 */
public class Suggestions {
    private DBmanager db;
    public Suggestions()
    {
        db = new DBmanager();
    }
    public void Get_Suggestions(final LatLng Src, final LatLng Dst, final View v,final Context c)
    {
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Graph cachedGraph = new Graph();
                ArrayList<District> cachedDistricts = new ArrayList<District>();
                try {
                    // Retrieve the list from internal storage
                    cachedGraph = (Graph) InternalStorage.readObject(c, "GraphFile");
                    cachedDistricts = (ArrayList<District>) InternalStorage.readObject(c, "DistrictsFile");
                    // Display the items from the list retrieved.
                } catch (IOException e) {
                    Log.e("ReadGraph", e.getMessage());
                } catch (ClassNotFoundException e) {
                    Log.e("ReadGraph", e.getMessage());
                }

                ArrayList<String> Routes;
                String Routes_String = "";
                try {
                    RC rc = new RC (cachedGraph,cachedDistricts,Src,Dst);
                    Routes = Format_Output(rc.RankedRoutes);
                    for(int i=0; i<Routes.size(); i++)
                    {
                        Routes_String += Routes.get(i);
                        if(i<Routes.size()-1)
                            Routes_String += ";";
                        Log.d("Routes",Routes.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return Routes_String;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.d("Final result",result);
                Intent intent= new Intent(c, CardViewActivity.class).putExtra("Result", result);
                v.getContext().startActivity(intent);
            }
        });
    }
    private ArrayList<String> Format_Output(ArrayList<Route> RankedRoutes) throws JSONException {
        ArrayList<String> FinalRoutes = new ArrayList<>();
        for(int i=0; i< RankedRoutes.size(); i++)
        {
            ArrayList<Parameter> Srcprams = new ArrayList<>();
            ArrayList<Parameter> Dstprams = new ArrayList<>();
            ArrayList<Parameter> prams = new ArrayList<>();
            String DstStat = "";
            String station = "";
            if(RankedRoutes.get(i).Lines.get(0).line.type == 1)
            {
                FinalRoutes.add("Use Metro line " + RankedRoutes.get(i).Lines.get(0).line.line);
                Srcprams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(0).Latit)));
                Srcprams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(0).Longit)));
                Srcprams.add(new Parameter("type",Integer.toString(1)));
            }
            else if(RankedRoutes.get(i).Lines.get(0).line.type == 2)
            {
                FinalRoutes.add("Use Bus line " + RankedRoutes.get(i).Lines.get(0).line.line);
                Srcprams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(0).Latit)));
                Srcprams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(0).Longit)));
                Srcprams.add(new Parameter("type",Integer.toString(2)));
            }
            Dstprams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(RankedRoutes.get(i).Stations.size()-1).Latit)));
            Dstprams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(RankedRoutes.get(i).Stations.size()-1).Longit)));
            Dstprams.add(new Parameter("type",Integer.toString(RankedRoutes.get(i).Lines.get(RankedRoutes.get(i).Lines.size()-1).line.type)));
            String result = db.sendRequest("POST",true,"Get_Station.php",Srcprams);
            JSONObject jsonObj = new JSONObject(result);
            String SrcStat  = jsonObj.getString("station_name");
            result = db.sendRequest("GET",true,"Get_Station.php",Dstprams);
            jsonObj = new JSONObject(result);
            DstStat  = jsonObj.getString("station_name");
            FinalRoutes.set(i,FinalRoutes.get(i) + " From " + SrcStat + " to ");
            boolean FirstTransitionFound = false;
            for(int j=0; j<RankedRoutes.get(i).Lines.size() - 1; j++)
            {
                if(RankedRoutes.get(i).Lines.get(j).line.type != RankedRoutes.get(i).Lines.get(j+1).line.type)
                {
                    if(!FirstTransitionFound)
                    {
                        prams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Latit)));
                        prams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Longit)));
                        prams.add(new Parameter("type",Integer.toString(RankedRoutes.get(i).Lines.get(j).line.type)));
                        result = db.sendRequest("POST",true,"Get_Station.php",prams);
                        jsonObj = new JSONObject(result);
                        station  = jsonObj.getString("station_name");
                        FinalRoutes.set(i,FinalRoutes.get(i) + station);
                        FirstTransitionFound = true;
                    }
                    else if(RankedRoutes.get(i).Lines.get(j).line.type == 2)
                    {
                        prams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Latit)));
                        prams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Longit)));
                        prams.add(new Parameter("type",Integer.toString(2)));
                        result = db.sendRequest("POST",true,"Get_Station.php",prams);
                        jsonObj = new JSONObject(result);
                        station  = jsonObj.getString("station_name");
                        FinalRoutes.set(i,FinalRoutes.get(i) + "\nBus line " + RankedRoutes.get(i).Lines.get(j).line.line + " to "+station);
                    }
                    else if(RankedRoutes.get(i).Lines.get(j).line.type == 0)
                    {
                        prams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Latit)));
                        prams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Longit)));
                        prams.add(new Parameter("type",Integer.toString(RankedRoutes.get(i).Lines.get(j+1).line.type)));
                        result = db.sendRequest("POST",true,"Get_Station.php",prams);
                        jsonObj = new JSONObject(result);
                        station  = jsonObj.getString("station_name");
                        FinalRoutes.set(i,FinalRoutes.get(i) + "\nWalk to " + station);
                    }
                    else if(RankedRoutes.get(i).Lines.get(j).line.type == 1)
                    {
                        prams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Latit)));
                        prams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Longit)));
                        prams.add(new Parameter("type",Integer.toString(1)));
                        result = db.sendRequest("POST",true,"Get_Station.php",prams);
                        jsonObj = new JSONObject(result);
                        station  = jsonObj.getString("station_name");
                        FinalRoutes.set(i,FinalRoutes.get(i) + "\nUse Metro line " + RankedRoutes.get(i).Lines.get(j).line.line + " to "+station);
                    }
                }
                else if(RankedRoutes.get(i).Lines.get(j).line.type == RankedRoutes.get(i).Lines.get(j+1).line.type)
                {
                    if(!RankedRoutes.get(i).Lines.get(j).line.line.equals(RankedRoutes.get(i).Lines.get(j+1).line.line))
                    {
                        if(!FirstTransitionFound)
                        {
                            prams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Latit)));
                            prams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Longit)));
                            prams.add(new Parameter("type",Integer.toString(RankedRoutes.get(i).Lines.get(j).line.type)));
                            result = db.sendRequest("POST",true,"Get_Station.php",prams);
                            jsonObj = new JSONObject(result);
                            station  = jsonObj.getString("station_name");
                            FinalRoutes.set(i,FinalRoutes.get(i) +station);
                            FirstTransitionFound = true;
                        }
                        else
                        {
                            prams.add(new Parameter("stationlat",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Latit)));
                            prams.add(new Parameter("stationlong",Double.toString(RankedRoutes.get(i).Stations.get(j+1).Longit)));
                            prams.add(new Parameter("type",Integer.toString(RankedRoutes.get(i).Lines.get(j+1).line.type)));
                            result = db.sendRequest("POST",true,"Get_Station.php",prams);
                            jsonObj = new JSONObject(result);
                            station  = jsonObj.getString("station_name");
                            if(RankedRoutes.get(i).Lines.get(j).line.type == 1)
                                FinalRoutes.set(i,FinalRoutes.get(i) + "\nUse Metro line " + RankedRoutes.get(i).Lines.get(j).line.line + " to "+station);
                            else if(RankedRoutes.get(i).Lines.get(j).line.type == 2)
                                FinalRoutes.set(i,FinalRoutes.get(i) + "\nUse Bus line " + RankedRoutes.get(i).Lines.get(j).line.line + " to "+station);
                        }
                    }
                }
            }
            if(RankedRoutes.get(i).Lines.get(RankedRoutes.get(i).Lines.size()-1).line.type == 1)
            {
                FinalRoutes.set(i,FinalRoutes.get(i) + "\nUse Metro line " + RankedRoutes.get(i).Lines.get(RankedRoutes.get(i).Lines.size()-1).line.line + " to "+DstStat);
            }
            else if(RankedRoutes.get(i).Lines.get(RankedRoutes.get(i).Lines.size()-1).line.type == 2)
            {
                FinalRoutes.set(i,FinalRoutes.get(i) + "\nUse Bus line " + RankedRoutes.get(i).Lines.get(RankedRoutes.get(i).Lines.size()-1).line.line + " to "+DstStat);
            }
        }
        return FinalRoutes;
    }
}
