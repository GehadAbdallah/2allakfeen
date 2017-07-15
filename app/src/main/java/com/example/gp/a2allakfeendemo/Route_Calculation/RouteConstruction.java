package com.example.gp.a2allakfeendemo.Route_Calculation;

import com.example.gp.a2allakfeendemo.GraphConstr.GraphConstruction;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphLink;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphNode;

import java.util.ArrayList;

/**
 * Created by Eman on 07/07/2017.
 */
public class RouteConstruction {
    ArrayList<ArrayList<GraphNode>> Paths;
    ArrayList<ArrayList<Node>> Lines;
    ArrayList<Integer> Transitions;
    ArrayList<Integer> Paths_Index;
    public ArrayList<Route> Routes;
    public RouteConstruction (ArrayList<ArrayList<GraphNode>> Paths, ArrayList<Integer> PathsIndex, ArrayList<ArrayList<Node>> Lines, ArrayList<Integer> Transitions){
        this.Paths = Paths;
        this.Lines = Lines;
        this.Transitions = Transitions;
        this.Paths_Index = PathsIndex;
        this.Routes = new ArrayList<>();
    }

    public void ConstructRoute(){
        if (Lines == null)
            return;
        int LineIndex = 0;
        for (int i = 0 ; i < Paths.size(); i++){
            if(Paths_Index.contains(i))
            {
                GetLink(Paths.get(i),LineIndex);
                LineIndex++;
            }
        }
    }

    public void GetLink(ArrayList<GraphNode> LineOfStations, int PathOrder){
        GraphLink TmpLink;
        Route route = new Route();
        for (int i = 0 ; i < LineOfStations.size()-1; i++){
            route.Stations.add(LineOfStations.get(i));

            GraphConstruction GC = new GraphConstruction();
            TmpLink = GC.GetLink(LineOfStations.get(i),LineOfStations.get(i+1));
            if (TmpLink.Lines != null){
                route.Lines.add(Lines.get(PathOrder).get(i));
            }
            else {
                route.Lines.add(null);
            }
        }
        route.Stations.add(LineOfStations.get(LineOfStations.size()-1));
        route.NoOfTransition = Transitions.get(PathOrder);
        Routes.add(route);
    }
}
