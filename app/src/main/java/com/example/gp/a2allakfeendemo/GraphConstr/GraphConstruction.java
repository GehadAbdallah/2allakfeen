package com.example.gp.a2allakfeendemo.GraphConstr;

/**
 * Created by shosho on 18/04/2017.
 */

public class GraphConstruction {

    public Graph Gr;

    public GraphConstruction(){
        Gr = new Graph();
        // Read row by row from database and create nodes accordingly
        float Latit;
        float Long;
        Integer BusLin;
        int order;

        //The next code should be inside a loop depending on the size of the database
        //assign the values of the above variables from the database
        //call ConstructNode

        //The next code should be inside a loop depending on the size of the array of nodes
        //adding links to the created nodes
        //Loop over array of nodes
        for (int i = 0; i < Gr.Nodes.size(); i++){
            GraphNode GN = Gr.Nodes.get(i);
            for (int j = 0; j < GN.Buslines.size(); j++){
                BusLine GN_BL = GN.Buslines.get(j);
                BusLine PredecessorBL = new BusLine();
                PredecessorBL.Busline = GN_BL.Busline;
                PredecessorBL.order = GN_BL.order - 1;
                GraphNode PredecessorNode = FindNodeByBusLine (PredecessorBL);
                BusLine SuccessorBL = new BusLine();
                SuccessorBL.Busline = GN_BL.Busline;
                SuccessorBL.order = GN_BL.order + 1;
                GraphNode SuccessorNode = FindNodeByBusLine (SuccessorBL);
                if(PredecessorNode != null && SuccessorNode != null){
                    if (!LinkExists(GN, PredecessorNode))
                        AddLink(GN, PredecessorNode);
                    if (!LinkExists(GN, SuccessorNode))
                        AddLink(GN, SuccessorNode);
                } else if (SuccessorNode == null && PredecessorNode != null){
                    if (!LinkExists(GN, PredecessorNode))
                        AddLink(GN, PredecessorNode);
                } else if (PredecessorNode == null && SuccessorNode != null){
                    if (!LinkExists(GN, SuccessorNode))
                        AddLink(GN, SuccessorNode);
                }
            }

        }

    }
    //Add node to Graph if it doesn't exist, or add bus pair to node if it does
    public void ConstructNode(float lat, float lon, Integer BuL, int order){
        GraphNode n1 = FindNodeByCoordinates(lat,lon);
        if(n1 == null){
            n1 = new GraphNode(BuL,order,lat,lon);
            this.Gr.Nodes.add(n1);
        }
        else
            AddBusLine(n1,BuL,order);
    }

    public void AddLink (GraphNode n1, GraphNode n2){
        GraphLink GL = new GraphLink();
        GL.Node1 = n1;
        GL.Node2 = n2;
        GL.Distance = CalculateDist(n1.Latit, n1.Longit, n2.Latit, n2.Longit);
        Gr.Links.add(GL);
    }

    public boolean LinkExists (GraphNode n1, GraphNode n2){
        GraphLink GL;
        for (int i = 0; i< this.Gr.Links.size(); i++){
            GL = Gr.Links.get(i);
            if ((SameNode(GL.Node1, n1) && SameNode(GL.Node2, n2))||((SameNode(GL.Node1, n2) && SameNode(GL.Node2, n1)))){
                return true;
            }
        }
        return false;
    }

    public boolean SameNode (GraphNode n1, GraphNode n2){
        if (n1.Longit == n2.Longit) {
            if (n1.Latit == n2.Latit)
                return true;
        }
        return  false;
    }
    //Find a node with certain Latitude and Longitude
    public GraphNode FindNodeByCoordinates(float Lat, float Lon){
        for(int i=0; i<this.Gr.Nodes.size(); i++)
        {
            if(CalculateDist(this.Gr.Nodes.get(i).Latit, this.Gr.Nodes.get(i).Longit, Lat, Lon) <= 5.0)
                return this.Gr.Nodes.get(i);
        }
        return null;
    }


    //Add Bus Line to already constructed Node
    public void AddBusLine(GraphNode n1, Integer Busline, int order)
    {
        BusLine B = new BusLine();
        B.Busline = Busline;
        B.order = order;
        n1.Buslines.add(B);
    }

    //return distance in meters
    public float CalculateDist(float lat1, float lng1, float lat2, float lng2){
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);
        return dist;
    }

    public GraphNode FindNodeByBusLine (BusLine BL){
        GraphNode CurrentNode;
        BusLine NodeBL;
        for (int i = 0; i<this.Gr.Nodes.size(); i++){
            CurrentNode = this.Gr.Nodes.get(i);
            for (int j = 0; j<CurrentNode.Buslines.size(); j++) {
                NodeBL = CurrentNode.Buslines.get(j);
                if (NodeBL.Busline == BL.Busline && NodeBL.order == BL.order) {
                    return CurrentNode;
                }
            }
        }
        return null;
    }
}