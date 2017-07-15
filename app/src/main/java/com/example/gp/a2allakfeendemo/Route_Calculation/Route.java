package com.example.gp.a2allakfeendemo.Route_Calculation;

import com.example.gp.a2allakfeendemo.GraphConstr.GraphNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Eman on 07/07/2017.
 */
public class Route implements Comparator<Route>{
    @Override
    public int compare(Route lhs, Route rhs) {
        return Double.compare(lhs.Rank, rhs.Rank);
    }

    public ArrayList<GraphNode> Stations; //in rank: access latlng when sending to api
    public ArrayList<Node> Lines; //in rank: access line, from line access type
    public int NoOfTransition;
    public double Rank;

    public Route (){
        Stations = new ArrayList<>();
        Lines = new ArrayList<>();
        NoOfTransition = 0;
        Rank = 5;
    }
}
