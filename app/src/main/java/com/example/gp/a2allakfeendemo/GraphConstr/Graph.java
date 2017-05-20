package com.example.gp.a2allakfeendemo.GraphConstr;

import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class Graph {
    ArrayList<GraphNode> Nodes;
    ArrayList<GraphLink> Links;

    public Graph(){
        Nodes = new ArrayList<GraphNode>();
        Links = new ArrayList<GraphLink>();
    }
}
