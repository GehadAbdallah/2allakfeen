package com.example.gp.a2allakfeendemo.Route_Calculation;

import android.util.Log;

import com.example.gp.a2allakfeendemo.GraphConstr.District;
import com.example.gp.a2allakfeendemo.GraphConstr.DistrictsList;
import com.example.gp.a2allakfeendemo.GraphConstr.Graph;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphConstruction;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphNode;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Eman on 30/06/2017.
 */
public class Get_Paths {
    public GraphNode Start;
    public GraphNode End;
    public GraphConstruction GC;
    public int smallestPath;
    public ArrayList<ArrayList<GraphNode>> Paths;

    public Integer numOfRoutes;

    public void Get_Paths(Graph Gr,GraphNode source, GraphNode destination)
    {
        ArrayList<GraphNode> visited = new ArrayList<>();

        numOfRoutes = 0;
        Paths = new ArrayList<>();
        GC = new GraphConstruction();
        GC.Gr = Gr;
        Start = source;
        End = destination;
        visited.add(Start);
        Depth_First(visited);
        ArrayList<ArrayList<GraphNode>> FinalPaths = new ArrayList<ArrayList<GraphNode>>();
        smallestPath = GetOptimalPath(Paths);
        Log.d("Smallest Path Size",Integer.toString(smallestPath));
        for(int i=0; i<Paths.size(); i++)
        {
            if(Paths.get(i).size() - smallestPath < 15) {
                FinalPaths.add(Paths.get(i));
            }
        }
        Paths.clear();
        Paths = (ArrayList<ArrayList<GraphNode>>) FinalPaths.clone();
        numOfRoutes = Paths.size();
    }

    boolean IsIn(ArrayList<GraphNode> Arr, GraphNode G)
    {
        if(Arr == null)
            return false;
        for(int i=0; i<Arr.size(); i++)
        {
            if(GC.SameNode(Arr.get(i),G))
                return true;
        }
        return false;
    }

//    boolean LineIn(ArrayList<GraphNode> Arr, GraphNode G)
//    {
//        for(int i=0; i<Arr.size(); i++)
//        {
//            int minSize = 0;
//            if(G.Lines.size() < Arr.get(i).Lines.size())
//                minSize = G.Lines.size();
//            else
//                minSize = Arr.get(i).Lines.size();
//            for(int j=0; j<minSize; j++)
//            {
//                if((G.Lines.get(j).line.equals(Arr.get(i).Lines.get(j).line)) || (G.Lines.get(j).type == 1 && Arr.get(i).Lines.get(j).type == 1))
//                    return true;
//            }
//            if(G.Lines.size() < Arr.get(i).Lines.size())
//            {
//                for(int j=minSize; j<Arr.get(i).Lines.size(); j++)
//                {
//                    if((G.Lines.get(j).line.equals(Arr.get(i).Lines.get(j).line)) || (G.Lines.get(j).type == 1 && Arr.get(i).Lines.get(j).type == 1))
//                        return true;
//                }
//            }
//            else
//            {
//                for(int j=minSize; j<G.Lines.size(); j++)
//                {
//                    if((G.Lines.get(j).line.equals(Arr.get(i).Lines.get(j).line)) || (G.Lines.get(j).type == 1 && Arr.get(i).Lines.get(j).type == 1))
//                        return true;
//                }
//            }
//        }
//        return false;
//    }

    int GetOptimalPath(ArrayList<ArrayList<GraphNode>> Arr)
    {
        if(Arr.size() == 0)
            return 0;
        int min = Arr.get(0).size();
        for(int i=1; i<Arr.size(); i++)
        {
            if(Arr.get(i).size() < min)
                min = Arr.get(i).size();
        }
        return min;
    }
    public void Depth_First(ArrayList<GraphNode> visited)
    {
        if(visited.size() == 1 && GC.SameNode(visited.get(0),End))
        {
            ArrayList<GraphNode> temp = new ArrayList<>();
            temp.add(visited.get(0));
            Paths.add(temp);
            return;
        }
        ArrayList<GraphNode> adj_nodes = new ArrayList<>();
        ArrayList<GraphNode> path;
        GraphNode LastVisited = visited.get(visited.size()-1);
        for(int i=0; i<LastVisited.Links.size(); i++)
        {
            adj_nodes.add(LastVisited.Links.get(i).Node);
        }
        for(GraphNode node : adj_nodes)
        {
            if(IsIn(visited,node))
                continue;
            if(GC.SameNode(node,End))
            {
                visited.add(node);
                path = (ArrayList<GraphNode>) visited.clone();
                if(Paths.size() > 0)
                {
                    if(Paths.size() == 3)
                        Log.d("Debug","here");
                    smallestPath = GetOptimalPath(Paths);
                    if(path.size() - smallestPath >= 15) {
                        visited.remove(visited.size() -1);
                        return;
                    }
                }
                Paths.add(path);
                numOfRoutes++;
                visited.remove(visited.size()-1);
                break;
            }
        }
        for(GraphNode node : adj_nodes)
        {
            if(IsIn(visited,node) || GC.SameNode(node,End))
                continue;

            visited.add(node);
            Depth_First(visited);
            visited.remove(visited.size()-1);
        }
    }

//    public void Depth_First(GraphNode st, GraphNode en)
//    {
//        ArrayList<GraphNode> s = new ArrayList<>();
//        ArrayList<GraphNode> visited = new ArrayList<>();
//        s.add(st);
//        while(s.size() > 0)
//        {
//            GraphNode current = s.get(s.size()-1);
//            s.remove(s.size()-1);
//            if(IsIn(visited,current)) {
//                continue;
//            }
//            else if(GC.SameNode(current,en))
//            {
//                ArrayList<GraphNode> path;
//                visited.add(current);
//                path = (ArrayList<GraphNode>) visited.clone();
//                Paths.add(path);
//                numOfRoutes++;
//                visited.remove(visited.size()-1);
//            }
//            else {
//                visited.add(current);
//                for (int i = 0; i < current.Links.size(); i++)
//                    s.add(current.Links.get(i).Node);
//            }
//        }
//    }
}
