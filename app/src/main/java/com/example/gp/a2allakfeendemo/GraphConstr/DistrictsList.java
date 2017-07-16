package com.example.gp.a2allakfeendemo.GraphConstr;

import android.util.Log;

import com.example.gp.a2allakfeendemo.DBmanager;
import com.example.gp.a2allakfeendemo.Data.GeoCodingJSON;
import com.example.gp.a2allakfeendemo.Data.address_components;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by shosho on 18/04/2017.
 */

public class DistrictsList implements Serializable {
    public ArrayList<District> DList;

    protected DBmanager dBmanager;
    public static String district;

    public DistrictsList(){
        this.dBmanager = new DBmanager();
    }

    public ArrayList<District> ConstructDistricList(Graph Gr){
        DList = new ArrayList<>();
        for (int i = 0; i < Gr.Nodes.size(); i++){
            // get lat and long of each node
            // get district of each node using google maps API
            // put nodes of same district in an object of district(create this object or check if its already created)
            // put this object in DList
            // we will need a function to find if the district is already created and added to DList
            double NodeLat = Gr.Nodes.get(i).Latit;
            double NodeLon = Gr.Nodes.get(i).Longit;
            LatLng ltln = new LatLng(NodeLat,NodeLon);
            String D1 = GetDistrict(ltln);  //Get district name using the API
            District FoundDist = FindDistrict(D1);
            if(FoundDist == null){
                District newDist  = new District();
                newDist.DName = D1;
                newDist.DistrictNodes = new ArrayList<>();
                newDist.DistrictNodes.add(Gr.Nodes.get(i));
                DList.add(newDist);
            }
            else
            {
                FoundDist.DistrictNodes.add(Gr.Nodes.get(i));
            }
        }
        return DList;
    }

    public String GetDistrict(LatLng latLng) {
        String district_name = "";
        Get_District(latLng);
        while(district == null)
            continue;

        if (district != null) {
            Log.d("Result in controller", district);
            final Gson gson = new Gson();
            GeoCodingJSON geocoding_json = gson.fromJson(district, GeoCodingJSON.class);
            boolean FoundDistrict = false;
            for (int i = 0 ; i < geocoding_json.results.size(); i++){
                ArrayList<address_components> AddComp = geocoding_json.results.get(i).address_components;
                for (int j = 0; j < AddComp.size(); j++) {
                    if (AddComp.get(j).types.contains("administrative_area_level_2")) {
                        district_name = AddComp.get(j).long_name;
                        FoundDistrict = true;
                        break;
                    }
                }
                if(FoundDistrict == true)
                    break;
            }

        }
        district = null;
        return district_name;
    }

    public District FindDistrict(String D1){
        for(int i=0; i<DList.size(); i++)
        {
            if(DList.get(i).DName.equals(D1))
                return DList.get(i);
        }
        return null;
    }

    public void Get_District (final LatLng latLng){
        Log.d("IN","Get_District CONTROLLER");
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Log.d("IN", "Get_District doWork");
                String result = dBmanager.sendGetDistrict(latLng);
                district = result;
                return district;
            }
            @Override
            protected void thenDoUiRelatedWork(String district) {
                Log.d("DISTRICT",district);
            }
        });
    }

}
