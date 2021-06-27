package com.mygdx.leptosim.desktop;

/**
 * Created by Ben J on 14/10/2015.
 */

//The ant class represents a builder ant agent, inheriting from
//the SimulationObject superclass.
public class Ant extends SimObj {

    //constants for the ant, holding boulder boolean
    //dropping timer and ability to drop boulders.
    boolean holding = true;
    public int droptime = 601;
    public boolean dropenabled = false;

    public Ant() {
        //setting the width and height of the agent
        width = 14;
        height = 14;
    }

}
