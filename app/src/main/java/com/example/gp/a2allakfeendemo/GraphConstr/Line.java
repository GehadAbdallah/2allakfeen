package com.example.gp.a2allakfeendemo.GraphConstr;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by shosho on 19/04/2017.
 */

public class Line implements Serializable {
    @Expose
    public String line; //htb2a string
    @Expose
    public int type; //1 for metro, 2 for bus
    @Expose
    public int order;
}
