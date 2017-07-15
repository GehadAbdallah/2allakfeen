package com.example.gp.a2allakfeendemo;

import com.example.gp.a2allakfeendemo.GraphConstr.District;
import com.example.gp.a2allakfeendemo.GraphConstr.DistrictsList;
import com.example.gp.a2allakfeendemo.GraphConstr.Graph;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphConstruction;
import com.example.gp.a2allakfeendemo.GraphConstr.GraphNode;
import com.example.gp.a2allakfeendemo.Route_Calculation.Get_Paths;
import com.example.gp.a2allakfeendemo.Route_Calculation.Get_Routes;
import com.example.gp.a2allakfeendemo.Route_Calculation.Route;
import com.example.gp.a2allakfeendemo.Route_Calculation.RouteConstruction;
import com.example.gp.a2allakfeendemo.Route_Ranking.Rank;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Eman on 02/07/2017.
 */
public class RC{
    public ArrayList<Route> RankedRoutes;
    public RC (Graph Gr, DistrictsList DL, LatLng st, LatLng en) throws JSONException{

        //Get nearest stations to source and destination
        Controller control = new Controller();
        GraphNode srcStation = GetNearestStation(DL, st);
        GraphNode dstStation = GetNearestStation(DL, en);

        LatLng srcLatlng = new LatLng(srcStation.Latit,srcStation.Longit);
        LatLng dstLatlng = new LatLng(dstStation.Latit,dstStation.Longit);
        final Gson gson = new Gson();
        String src = gson.toJson(srcLatlng, LatLng.class);
        String dst = gson.toJson(dstLatlng, LatLng.class);

        //Check whether Source and destination exist in database and retrieve their routes if exists
        control.GetRoutes(src,dst);
        while (control.routes_result == null)
            continue;

        JSONObject jsonObj = new JSONObject(control.routes_result);
        JSONArray routes = jsonObj.getJSONArray("routes");
        if (routes.length() != 0) { //if exists

            String route = "";
            Route routeObj;
            ArrayList<Route> routesList = new ArrayList<>();
            //Get routes
            for (int i = 0; i < routes.length(); i++) {
                route = routes.getJSONObject(i).getString("route");
                routeObj = gson.fromJson(route, Route.class);
                routesList.add(routeObj);
            }
            //Get rate
            ArrayList<Integer> Rates = new ArrayList<>();
            int routeID;
            for (int i = 0; i < routes.length(); i++) {
                routeID = routes.getJSONObject(i).getInt("route_id");
                control.GetRates(routeID);
                while (control.rate_result == null)
                    continue;
                JSONObject jsonRate = new JSONObject(control.rate_result);
                int rate = jsonObj.getJSONArray("rates").getJSONObject(0).getInt("rate");
                Rates.add(rate);
            }

            //Rank routes
            Rank RR;
            for (int i = 0 ; i < routesList.size(); i++){
                RR = new Rank (routesList.get(i),Rates.get(i));
            }
            RankRoutes(routesList);
        }
        else {
            //Get stations
            Get_Paths GP = new Get_Paths();
            GP.Get_Paths(Gr,DL,srcStation,dstStation);

            //Get lines
            Get_Routes GR = new Get_Routes();
            GR.Get_Routes(GP.Paths);

            //Construct routes
            RouteConstruction RC = new RouteConstruction(GP.Paths,GR.PathIndex,GR.All_Paths,GR.NoOfTransitions);
            RC.ConstructRoute();

            RankedRoutes = new ArrayList<>();

            //Save routes to database
            for (int i = 0; i < RC.Routes.size(); i++){
                String routeTobeSaved = gson.toJson(RC.Routes.get(i),Route.class);
                control.InsertRoute(src,dst,routeTobeSaved);
            }

            //Rank routes
            Rank RR;
            for (int i = 0 ; i < RC.Routes.size(); i++){
                RR = new Rank (RC.Routes.get(i),0);
            }
            RankedRoutes = RankRoutes(RC.Routes);
        }
    }

    public ArrayList<Route> RankRoutes(ArrayList<Route> rankedRoutes)
    {
        //remove routes with rank = 5
        for (int i = 0 ; i < rankedRoutes.size(); i++){
            if (rankedRoutes.get(i).Rank == 5)
                rankedRoutes.remove(i);
        }
        //sort routes acc to rank
        Collections.sort(rankedRoutes, new Route());
        return rankedRoutes;
    }

    public GraphNode GetNearestStation( DistrictsList Dl, LatLng Loc)
    {
        GraphConstruction GC = new GraphConstruction();
        //get district name of the location from google maps API
        String D = Dl.GetDistrict(Loc);
        //get the district in the district list with that name
        District Dnodes = Dl.FindDistrict(D);
        //array to save the distance between the location and each station in the district
        ArrayList<Double> Distance = new ArrayList<>();
        for(int i=0; i<Dnodes.DistrictNodes.size(); i++)
        {
            double d = GC.CalculateDist(Loc.latitude, Loc.longitude, Dnodes.DistrictNodes.get(i).Latit, Dnodes.DistrictNodes.get(i).Longit);
            Distance.add(d);
        }
        double min_distance = Distance.get(0);
        int nearest_Index = 0;
        for(int i=1; i<Distance.size(); i++)
        {
            if(Distance.get(i) < min_distance)
            {
                min_distance = Distance.get(i);
                nearest_Index = i;
            }
        }
        return Dnodes.DistrictNodes.get(nearest_Index);
    }
}
