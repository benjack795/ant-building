package com.mygdx.leptosim.desktop;

/**
 * Created by Ben J on 03/12/2015.
 */

//The boulder class represents a loose boulder object in the
//simulation, to be picked up by builders.
public class Boulder extends SimObj {

    //variables for referring to the location of the
    //holder of the boulder object.
    public float holdingx = 0;
    public float holdingy = 0;

    public Boulder(){
        //setting the height and width
        width = 10;
        height = 10;
    }
}
