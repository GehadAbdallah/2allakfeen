package com.example.gp.a2allakfeendemo.GraphConstr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shosho on 18/04/2017.
 */

public class District implements Serializable {
    public ArrayList<GraphNode> DistrictNodes;
    public String DName;

    public District()
    {
        DistrictNodes = new ArrayList<>();
    }
}
