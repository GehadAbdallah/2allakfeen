package com.example.gp.a2allakfeendemo.GraphConstr;
import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class GraphNode {
    ArrayList<BusLine> Buslines;
    float Latit;
    float Longit;

    public GraphNode(Integer BusLine, int order, float Lat, float Lon){
        Buslines = new ArrayList<BusLine>();
        BusLine BL = new BusLine();
        BL.Busline = BusLine;
        BL.order = order;
        Buslines.add(BL);
        Latit = Lat;
        Longit = Lon;
    }
}
