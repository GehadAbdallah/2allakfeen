package com.example.gp.a2allakfeendemo.GraphConstr;

import android.util.Log;

import com.example.gp.a2allakfeendemo.Controller;
import com.example.gp.a2allakfeendemo.Data.GeoCodingJSON;
import com.example.gp.a2allakfeendemo.Data.address_components;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class DistrictsList implements Serializable {
    ArrayList<District> DList;

    public DistrictsList(Graph Gr){
        DList = new ArrayList<District>();
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
    }

    public String GetDistrict(LatLng latLng) {
        Controller c1 = new Controller();
        String district = "";
        c1.Get_District(latLng);
        while(c1.district == null)
            continue;

        if (c1.district != null) {
            Log.d("Result in controller", c1.district);
            final Gson gson = new Gson();
            GeoCodingJSON geocoding_json = gson.fromJson(c1.district, GeoCodingJSON.class);
            boolean FoundDistrict = false;
            for (int i = 0 ; i < geocoding_json.results.size(); i++){
                ArrayList<address_components> AddComp = geocoding_json.results.get(i).address_components;
                for (int j = 0; j < AddComp.size(); j++) {
                    if (AddComp.get(j).types.contains("administrative_area_level_2")) {
                        district = AddComp.get(j).long_name;
                        FoundDistrict = true;
                        break;
                    }
                }
                if(FoundDistrict == true)
                    break;
            }

        }
        c1.district = null;
        return district;
    }

    public District FindDistrict(String D1){
        for(int i=0; i<DList.size(); i++)
        {
            if(DList.get(i).DName.equals(D1))
                return DList.get(i);
        }
        return null;
    }

}
