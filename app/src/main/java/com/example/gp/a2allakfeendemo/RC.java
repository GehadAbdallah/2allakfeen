package com.example.gp.a2allakfeendemo;

import android.util.Log;

import com.example.gp.a2allakfeendemo.Data.Parameter;
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
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Collections;

import needle.Needle;
import needle.UiRelatedTask;

/**
 * Created by Eman on 02/07/2017.
 */
public class RC{
    public ArrayList<Route> RankedRoutes;
    protected DBmanager dBmanager;
    public static String routes_result;
    public static String rate_result;

    public RC (Graph Gr, ArrayList<District> DL, LatLng st, LatLng en) throws JSONException{

        //Get nearest stations to source and destination
        this.dBmanager = new DBmanager();
        GraphNode srcStation = GetNearestStation(DL, st);
        GraphNode dstStation = GetNearestStation(DL, en);

        LatLng srcLatlng = new LatLng(srcStation.Latit,srcStation.Longit);
        LatLng dstLatlng = new LatLng(dstStation.Latit,dstStation.Longit);
        final Gson gson = new Gson();
        String src = gson.toJson(srcLatlng, LatLng.class);
        String dst = gson.toJson(dstLatlng, LatLng.class);

        //Check whether Source and destination exist in database and retrieve their routes if exists
        GetRoutes(src,dst);
        while (routes_result == null)
            continue;

        JSONObject jsonObj = new JSONObject(routes_result);
        JSONArray routes = jsonObj.getJSONArray("routes");
        if (routes.length() != 0) { //if exists

            String route = "";
            LatLng StationLatLng;
            GraphConstruction GC = new GraphConstruction();
            GC.Gr = Gr;
            ArrayList<ArrayList<GraphNode>> paths = new ArrayList<>();
            ArrayList<GraphNode> path = new ArrayList<>();
            //Get routes
            for (int i = 0; i < routes.length(); i++) {
                route = routes.getJSONObject(i).getString("route");
                String[] stations = route.split(";");
                path = new ArrayList<>();
                for(int j = 0 ; j < stations.length; j++){
                    GraphNode stationNode;
                    LatLng StLatLng = gson.fromJson(stations[j],LatLng.class);
                    stationNode = GC.FindNodeByCoordinates(StLatLng.latitude, StLatLng.longitude);
                    path.add(stationNode);
                }
                paths.add(path);
            }

            //Get lines
            Get_Routes GR = new Get_Routes();
            GR.Get_Routes(paths);

            //Construct routes
            RouteConstruction RC = new RouteConstruction(paths,GR.PathIndex,GR.All_Paths,GR.NoOfTransitions);
            RC.ConstructRoute();

            RankedRoutes = new ArrayList<>();

            //Get rate
            ArrayList<Integer> Rates = new ArrayList<>();
            int routeID;
            int rate = 0;
            for (int i = 0; i < routes.length(); i++) {
                routeID = routes.getJSONObject(i).getInt("route_id");
                GetRates(routeID);
                int count = 0;
                while (rate_result == null)
                    continue;
                JSONObject jsonRate = new JSONObject(rate_result);
                JSONArray rates = jsonRate.getJSONArray("rates");
                for (int j = 0 ; j < rates.length(); j++) {
                    rate += rates.getJSONObject(j).getInt("rate");
                    count++;
                }
                if (count > 0)
                    rate = rate/count;
            }
            Rates.add(rate);

            //Rank routes
            Rank RR;
            for (int i = 0 ; i < RC.Routes.size(); i++){
                RR = new Rank (RC.Routes.get(i),Rates.get(i));
            }
            RankedRoutes = RankRoutes(RC.Routes);
        }
        else {  //if route doesn't exist
            //Get stations
            Get_Paths GP = new Get_Paths();
            GP.Get_Paths(Gr,srcStation,dstStation);

            //Save route
            for (int i = 0 ; i < GP.Paths.size(); i++){
                SaveStations(src,dst,GP.Paths.get(i));
            }

            //Get lines
            Get_Routes GR = new Get_Routes();
            GR.Get_Routes(GP.Paths);

            //Construct routes
            RouteConstruction RC = new RouteConstruction(GP.Paths,GR.PathIndex,GR.All_Paths,GR.NoOfTransitions);
            RC.ConstructRoute();

            RankedRoutes = new ArrayList<>();

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

    public GraphNode GetNearestStation( ArrayList<District> Dl, LatLng Loc)
    {
        GraphConstruction GC = new GraphConstruction();
        DistrictsList D = new DistrictsList();
        D.DList = Dl;
        //get district name of the location from google maps API
        String DS = D.GetDistrict(Loc);
        //get the district in the district list with that name
        District Dnodes = D.FindDistrict(DS);
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
    public void SaveStations(String src,String dst,ArrayList<GraphNode> Stations){
        final Gson gson = new Gson();
        String routeTobeSaved = "";
        for (int i = 0 ; i < Stations.size(); i++){
            LatLng StLatLng = new LatLng(Stations.get(i).Latit,Stations.get(i).Longit);
            routeTobeSaved += gson.toJson(StLatLng, LatLng.class);
            if (i != Stations.size()-1)
                routeTobeSaved += ";";
        }
        InsertRoute(src,dst,routeTobeSaved);
    }

    public void GetRoutes (String src,String dst){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("src",src));
        parameters.add(new Parameter("dst",dst));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"getroutes.php",parameters);
                //Log.d("doWork",result);
                routes_result = result;
                return routes_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
            }
        });
    }

    public void GetRates (int routeID){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("routeID",Integer.toString(routeID)));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("GET",true,"getrates.php",parameters);
                rate_result = result;
                return rate_result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
            }
        });
    }

    public void InsertRoute (String src,String dst,String routeTobeSaved){
        final ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter("src",src));
        parameters.add(new Parameter("dst",dst));
        parameters.add(new Parameter("routeTobeSaved",routeTobeSaved));
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                String result = dBmanager.sendRequest("POST",true,"insertroute.php",parameters);
                Log.d("result",result);
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {
                Log.d("OnPOSTEXECUTE","Enter");
            }
        });
    }

}
