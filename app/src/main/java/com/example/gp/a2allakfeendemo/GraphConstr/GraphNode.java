package com.example.gp.a2allakfeendemo.GraphConstr;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class GraphNode implements Serializable {
    public ArrayList<Line> Lines;
    public ArrayList<GraphLink> Links;
    public double Latit;
    public double Longit;

    public GraphNode(String Ln, int order, double Lat, double Lon, int type){
        Lines = new ArrayList<>();
        Links = new ArrayList<>();
        Line L = new Line();
        L.line = Ln;
        L.order = order;
        L.type = type;
        Lines.add(L);
        Latit = Lat;
        Longit = Lon;
    }
}
