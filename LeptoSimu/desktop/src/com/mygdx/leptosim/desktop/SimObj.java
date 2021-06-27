package com.mygdx.leptosim.desktop;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Ben J on 14/10/2015.
 */

//The SimObj class is an abstract superclass for all objects in the simulation
//It is most commonly used when iterating through groups of objects of multiple types
public class SimObj {

    //basic object parameters
    protected float x;
    protected float y;
    protected int width;
    protected int height;

    //libGDX object classes, each object has a physics body, a sprite, and a visibility boolean
    protected Body body;
    protected Sprite sprite;
    protected  boolean visible = true;

}