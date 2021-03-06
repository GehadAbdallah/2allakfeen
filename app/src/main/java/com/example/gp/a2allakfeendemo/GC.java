package com.example.gp.a2allakfeendemo;

import android.content.Context;
import android.util.Log;

import com.example.gp.a2allakfeendemo.GraphConstr.District;
import com.example.gp.a2allakfeendemo.GraphConstr.DistrictsList;
import com.example.gp.a2allakfeendemo.GraphConstr.Graph;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphConstruction;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Eman on 02/07/2017.
 */
public class GC {
    // variable to hold context
    private Context context;
    public Graph TransportationGraph;
    public ArrayList<District> Dlist;
    public GC (Context context) throws JSONException {
        this.context=context;
        GraphConstruction graph_construction = new GraphConstruction();
        graph_construction.Construct_Graph();
        TransportationGraph = graph_construction.Gr;
        DistrictsList DL = new DistrictsList();
        Dlist = DL.ConstructDistricList(TransportationGraph);
        //Store graph
        try {
            // Save the list of entries to internal storage
            InternalStorage.writeObject(this.context, "GraphFile", TransportationGraph);
            InternalStorage.writeObject(this.context, "DistrictsFile", Dlist);
        } catch (IOException e) {
            Log.e("SaveGraph", e.getMessage());
        }
    }
}
