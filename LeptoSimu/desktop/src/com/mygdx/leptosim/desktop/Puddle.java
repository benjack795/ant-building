package com.mygdx.leptosim.desktop;

/**
 * Created by Ben J on 07/02/2016.
 */

//The puddle class represents a puddle object in the simulation.
public class Puddle extends  SimObj {
    //Time the puddle will be on screen
    int lifetime = 900;

    //diameter variable, set in constructor
    float diam = 0;
    public Puddle(float diam){
        this.diam = diam;
    }
}
