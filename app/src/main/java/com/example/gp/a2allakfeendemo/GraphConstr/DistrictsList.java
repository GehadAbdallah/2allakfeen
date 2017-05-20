package com.example.gp.a2allakfeendemo.GraphConstr;

import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class DistrictsList {
    ArrayList<District> DList;

    public DistrictsList(Graph Gr){
        DList = new ArrayList<District>();
        for (int i = 0; i < Gr.Nodes.size(); i++){
            // get lat and long of each node
            float NodeLat = Gr.Nodes.get(i).Latit;
            float NodeLon = Gr.Nodes.get(i).Longit;
            // get district of each node using google maps api
            // put nodes of same district in an object of district(create this object or check if its already created)
            // put this object in DList
            // we will need a function to find if the district is already created and added to DList
        }
    }

    public District FindDistrict(District D1){
        for(int i=0; i<DList.size(); i++)
        {
            //if(DList.get(i) == D1)

        }
        return null;
    }

}
