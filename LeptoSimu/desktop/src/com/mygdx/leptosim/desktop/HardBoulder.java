package com.mygdx.leptosim.desktop;

/**
 * Created by Ben J on 19/01/2016.
 */

//The HardBoulder class represents a placed boulder in
//the simulation.
public class HardBoulder extends SimObj{

    //the location of the previous holder of the boulder
    public float holdingx = 0;
    public float holdingy = 0;

    public HardBoulder(){
        //setting the height and width
        width = 10;
        height = 10;
    }
}
