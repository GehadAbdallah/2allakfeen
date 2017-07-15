package com.example.gp.a2allakfeendemo.GraphConstr;

/**
 * Created by shosho on 18/04/2017.
 */
import android.util.Log;

import com.example.gp.a2allakfeendemo.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GraphConstruction {

    public Graph Gr;

    public GraphConstruction(){
        Gr = new Graph();
    }

    public void Construct_Graph() throws JSONException {
        double Latit = 0;
        double Long = 0;
        String Lin = "";
        int order = 0;
        int type = 0;


        Controller BMC = new Controller();

        BMC.GetBusStations();
        BMC.GetMetroStations();
        //BMC.bus_stations_result == null ||
        while ( BMC.bus_stations_result == null ||BMC.metro_stations_result == null)
            continue;

        JSONArray bus_stations = new JSONArray();
        JSONArray metro_stations = new JSONArray();
        try {
            if (BMC.bus_stations_result != null) {
                JSONObject jsonObj = new JSONObject(BMC.bus_stations_result);
                bus_stations = jsonObj.getJSONArray("busstations");
                Log.d("busstations","line"+bus_stations.get(0));
            }

            if (BMC.metro_stations_result != null) {
                JSONObject jsonObj = new JSONObject(BMC.metro_stations_result);
                metro_stations = jsonObj.getJSONArray("metrostations");
                Log.d("metrostations","line"+metro_stations.get(0));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < bus_stations.length(); i++) {
                JSONObject Bstations = bus_stations.getJSONObject(i);
                Lin = Bstations.getString("bus_line");
                Latit = Bstations.getDouble("bus_stat_lat");
                Long = Bstations.getDouble("bus_stat_long");
                order = Bstations.getInt("bus_stat_order");
                type = 2;
                ConstructNode(Latit, Long, Lin, order, type);
            }

            for (int i = 0; i < metro_stations.length(); i++) {
                JSONObject Mstations = metro_stations.getJSONObject(i);
                Lin = Mstations.getString("metro_line");
                Latit = Mstations.getDouble("metro_stat_lat");
                Long = Mstations.getDouble("metro_stat_long");
                order = Mstations.getInt("metro_stat_order");
                type = 1;
                ConstructNode(Latit, Long, Lin, order, type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //The next code should be inside a loop depending on the size of the database

        //assign the values of the above variables from the database

        //call ConstructNode
        //The next code should be inside a loop depending on the size of the array of nodes
        //adding links to the created nodes
        //Loop over array of nodes
        for (int i = 0; i < Gr.Nodes.size(); i++) {
            GraphNode GN = Gr.Nodes.get(i);

            if (SameNode(GN,FindNodeByCoordinates(29.8694521,31.3200045)))
                Log.d("Debug","here");

            for (int j = 0; j < GN.Lines.size(); j++) {
                if(Gr.Nodes.get(i).Lines.get(j).type == 1)
                    Log.d("Debug","Here");
                Line GN_L = GN.Lines.get(j);
                Line PredecessorL = new Line();
                PredecessorL.line = GN_L.line;
                PredecessorL.order = GN_L.order - 1;
                PredecessorL.type = GN_L.type;
                GraphNode PredecessorNode = FindNodeByLine(PredecessorL);
                Line SuccessorL = new Line();
                SuccessorL.line = GN_L.line;
                SuccessorL.order = GN_L.order + 1;
                SuccessorL.type = GN_L.type;
                GraphNode SuccessorNode = FindNodeByLine(SuccessorL);
                if (PredecessorNode != null && SuccessorNode != null) {
                    if (!LinkExists(GN, PredecessorNode, PredecessorL))
                        AddLink(GN, PredecessorNode, PredecessorL);
                    if (!LinkExists(GN, SuccessorNode, SuccessorL))
                        AddLink(GN, SuccessorNode, SuccessorL);
                } else if (SuccessorNode == null && PredecessorNode != null) {
                    if (!LinkExists(GN, PredecessorNode, PredecessorL))
                        AddLink(GN, PredecessorNode, PredecessorL);
                } else if (PredecessorNode == null && SuccessorNode != null) {
                    if (!LinkExists(GN, SuccessorNode, SuccessorL))
                        AddLink(GN, SuccessorNode, SuccessorL);
                }
            }

        }
        ConnectFinal();
    }

    //Walking link
    public void ConnectFinal()
    {
        for(int i=0; i< Gr.Nodes.size(); i++)
        {
            for(int j=0; j< Gr.Nodes.size(); j++)
            {
                if (Gr.Nodes.get(i).Lines.get(0).order == 5 && Gr.Nodes.get(i).Lines.get(0).line.equals("1") && Gr.Nodes.get(j).Lines.get(0).line.equals("800"))
                    Log.d("Debug","Here");
                if(SameNode(Gr.Nodes.get(i),Gr.Nodes.get(j)))
                    continue;
                else
                {
                    if(CalculateDist(Gr.Nodes.get(i).Latit,Gr.Nodes.get(i).Longit,Gr.Nodes.get(j).Latit,Gr.Nodes.get(j).Longit)<= 300)
                    {
                        Line walk = new Line();
                        walk.type = 0;
                        walk.line = "0";
                        walk.order = 0;
                        if(LinkExists(Gr.Nodes.get(i),Gr.Nodes.get(j),walk) == false)
                            AddLink(Gr.Nodes.get(i),Gr.Nodes.get(j),walk);
                    }
                }
            }
        }
    }
    //Add node to Graph if it doesn't exist, or add bus pair to node if it does
    public void ConstructNode(double lat, double lon, String L, int order, int type){
        GraphNode n1 = FindNodeByCoordinates(lat,lon);
        if(n1 == null){
            n1 = new GraphNode(L,order,lat,lon, type);
            this.Gr.Nodes.add(n1);
        }
        else
            AddLine(n1,L,order,type);
    }

    public boolean SameLine(Line ln1, Line ln2)
    {
        if(ln1.line.equals(ln2.line))
        {
            if(ln1.type == ln2.type)
                return true;
        }
        return false;
    }

    public boolean LineIsOnLink(Line ln, GraphLink lnk)
    {
        for(int i=0; i < lnk.Lines.size(); i++)
        {
            if(SameLine(ln,lnk.Lines.get(i)))
                return true;
        }
        return false;
    }

    public void AddLink (GraphNode n1, GraphNode n2, Line ln) {
        if(SameNode(n1,n2))
            return;
        for(int i=0; i<n1.Links.size(); i++)
        {
            if(SameNode(n1.Links.get(i).Node,n2))
            {
                n1.Links.get(i).Lines.add(ln);
            }
        }
        for(int i=0; i<n2.Links.size(); i++)
        {
            if(SameNode(n2.Links.get(i).Node,n1))
            {
                n2.Links.get(i).Lines.add(ln);
                return;
            }
        }
        GraphLink GL1 = new GraphLink();
        GraphLink GL2 = new GraphLink();
        //Add lines, second node and distance to link
        GL1.Lines.add(ln);
        GL2.Lines.add(ln);

        GL1.Node = n2;
//        GL1.Node.Latit = n2.Longit;
//        GL1.Node.Longit = n2.Longit;
//        for(int i=0; i<n2.Lines.size(); i++)
//            GL1.Node.Lines.add(n2.Lines.get(i));
//        for(int i=0; i<n2.Links.size(); i++)
//            GL1.Node.Links.add(n2.Links.get(i));
        GL1.Distance = CalculateDist(n1.Latit, n1.Longit, n2.Latit, n2.Longit);
//        //Add link to first node
        n1.Links.add(GL1);
        //Add link to second node

        GL2.Node = n1;
//        GL2.Node.Latit = n1.Longit;
//        GL2.Node.Longit = n1.Longit;
//        for(int i=0; i<n1.Lines.size(); i++)
//            GL2.Node.Lines.add(n1.Lines.get(i));
//        for(int i=0; i<n1.Links.size(); i++)
//            GL2.Node.Links.add(n1.Links.get(i));GL2.Node.Latit = n1.Longit;
//        GL2.Node.Longit = n1.Longit;
//        for(int i=0; i<n1.Lines.size(); i++)
//            GL2.Node.Lines.add(n1.Lines.get(i));
//        for(int i=0; i<n1.Links.size(); i++)
//            GL2.Node.Links.add(n1.Links.get(i));
        GL2.Distance = CalculateDist(n1.Latit, n1.Longit, n2.Latit, n2.Longit);
        //Add link to first node
        n2.Links.add(GL2);
    }

    public boolean LinkExists (GraphNode n1, GraphNode n2, Line ln) {
        for(int i=0; i<n1.Links.size(); i++)
        {
            if(SameNode(n1.Links.get(i).Node,n2))
            {
                if(LineIsOnLink(ln,n1.Links.get(i)))
                    return true;
            }
        }
//        int n1LinksSize = n1.Links.size();
//        int n2LinksSize = n2.Links.size();
//        if (n1LinksSize <= n2LinksSize) {
//            for (int i = 0; i < n1LinksSize; i++) {
//                GL1 = n1.Links.get(i);
////                GL2 = n2.Links.get(i);
//                if (SameNode(GL1.Node, n2)) {
//                    for (int j = 0; j < GL1.Lines.size(); j++) {
//                            if (GL1.Lines.get(j).line.equals(ln.line) && GL1.Lines.get(j).type == ln.type)
//                                return true;
//                    }
//                }
////                else if (SameNode(GL2.Node, n1)) {
////                    for (int j = 0; j < GL2.Lines.size(); j++) {
////                            if (GL2.Lines.get(j).line.equals(ln.line) && GL2.Lines.get(j).type == ln.type)
////                                return true;
////                    }
////                }
//            }
//            for(int i = n1LinksSize; i < n2LinksSize; i++)
//            {
//                GL2 = n2.Links.get(i);
//                if (SameNode(GL2.Node, n1)) {
//                    for (int j = 0; j < GL2.Lines.size(); j++) {
//                            if (GL2.Lines.get(j).line.equals(ln.line) && GL2.Lines.get(j).type == ln.type)
//                                return true;
//                    }
//                }
//            }
//        }
//        else if (n2LinksSize < n1LinksSize)
//        {
//            for (int i = 0; i < n2LinksSize; i++) {
//                GL1 = n1.Links.get(i);
//                GL2 = n2.Links.get(i);
//                if (SameNode(GL1.Node, n2)) {
//                    for (int j = 0; j < GL1.Lines.size(); j++) {
//                            if (GL1.Lines.get(j).line.equals(ln.line) && GL1.Lines.get(j).type == ln.type)
//                                return true;
//                    }
//                }
////                else if (SameNode(GL2.Node, n1)) {
////                    for (int j = 0; j < GL2.Lines.size(); j++) {
////                            if (GL2.Lines.get(j).line.equals(ln.line) && GL2.Lines.get(j).type == ln.type)
////                                return true;
////                    }
////                }
//            }
//            for(int i = n2LinksSize; i < n1LinksSize; i++)
//            {
//                GL1 = n1.Links.get(i);
//                if (SameNode(GL1.Node, n1)) {
//                    for (int j = 0; j < GL1.Lines.size(); j++) {
//                            if (GL1.Lines.get(j).line.equals(ln.line) && GL1.Lines.get(j).type == ln.type)
//                                return true;
//                    }
//                }
//            }
//        }
//            return false;
        return false;
    }

    public GraphLink GetLink(GraphNode n1, GraphNode n2)
    {
        for(int i=0; i<n1.Links.size(); i++)
        {
            if(SameNode(n1.Links.get(i).Node,n2))
                return n1.Links.get(i);
        }
        return null;
    }

    public boolean SameNode (GraphNode n1, GraphNode n2){
        //if(Double.doubleToLongBits(n1.Latit) == Double.doubleToLongBits(n2.Latit))
        if(Double.compare(n1.Latit,n2.Latit) == 0)
        {
            //if (Double.doubleToLongBits(n1.Longit) == Double.doubleToLongBits(n2.Longit))
            if(Double.compare(n1.Longit,n2.Longit) == 0)
            {
                return  true;
            }
        }
        return false;
//        if (n1.Longit == n2.Longit) {
//            if (n1.Latit == n2.Latit)
//                return true;
//        }
//        return  false;
    }
    //Find a node with certain Latitude and Longitude
    public GraphNode FindNodeByCoordinates(double Lat, double Lon){
        for(int i=0; i<this.Gr.Nodes.size(); i++)
        {
            //if(Double.doubleToLongBits(this.Gr.Nodes.get(i).Latit) == Lat)
            if(Double.compare(this.Gr.Nodes.get(i).Latit,Lat) == 0)
            {
                //if (Double.doubleToLongBits(this.Gr.Nodes.get(i).Longit) == Lon)
                if(Double.compare(this.Gr.Nodes.get(i).Longit,Lon) == 0)
                {
                    return  this.Gr.Nodes.get(i);
                }
            }
//            if(this.Gr.Nodes.get(i).Latit == Lat && this.Gr.Nodes.get(i).Longit == Lon)
//                return this.Gr.Nodes.get(i);
        }
        return null;
    }


    //Add Bus Line to already constructed Node
    public void AddLine(GraphNode n1, String ln, int order, int type)
    {
        Line l = new Line();
        l.line = ln;
        l.type = type;
        l.order = order;
        n1.Lines.add(l);
    }

    //return distance in meters
    public double CalculateDist(double lat1, double lng1, double lat2, double lng2){
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);
        return dist;
    }

    public GraphNode FindNodeByLine (Line L){
        if(L.order <= 0 && L.type != 0)
            return null;
        GraphNode CurrentNode;
        Line NodeL;
        for (int i = 0; i<this.Gr.Nodes.size(); i++){
            CurrentNode = this.Gr.Nodes.get(i);
            for (int j = 0; j<CurrentNode.Lines.size(); j++) {
                NodeL = CurrentNode.Lines.get(j);
                if (NodeL.line.equals(L.line) && NodeL.order == L.order && NodeL.type == L.type) {
                    return CurrentNode;
                }
            }
        }
        return null;
    }
}