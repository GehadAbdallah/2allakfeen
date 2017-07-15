package com.example.gp.a2allakfeendemo.GraphConstr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class GraphLink implements Serializable {
    public ArrayList<Line> Lines;
    public GraphNode Node;
    public double Distance;

    public GraphLink()
    {
        Lines = new ArrayList<>();
    }
}
