package com.mygdx.leptosim.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import java.util.Random;

/**
 * Created by Ben J on 25/01/2016.
 */

//The ScreenHandler Class represents an overarching class that manages the three screen objects that make up the simulation.
class ScreenHandler extends Game implements ApplicationListener {

    //The variables for each of the three screen objects
    private Setup preScreen;
    private SimMain simScreen;
    private Results postScreen;

    //Free parameter variables
    public static int NUM_ANTS = 30;
    public static int NUM_BOLDS = 100;
    public static int NUM_RANTS = 15;
    public static float DROP_TIME = 90;
    public static Setup.Direction currentdir = Setup.Direction.NONE;
    public static float windstr = 0.2f;
    public static float PUDDLE_FREQ = 0f;
    public static float PUDDLE_WIDTH = 10;

    //data for csv variables
    public static double[] droplist = new double[SimMain.TOTAL_TIME];
    public static double[] broodcentrex = new double[SimMain.TOTAL_TIME];
    public static double[] broodcentrey = new double[SimMain.TOTAL_TIME];
    public static double[] boldscentrex = new double[SimMain.TOTAL_TIME];
    public static double[] boldscentrey = new double[SimMain.TOTAL_TIME];
    public static double[] ratio = new double[SimMain.TOTAL_TIME];
    public static double[] nwbolds = new double[SimMain.TOTAL_TIME];
    public static double[] nebolds = new double[SimMain.TOTAL_TIME];
    public static double[] swbolds = new double[SimMain.TOTAL_TIME];
    public static double[] sebolds = new double[SimMain.TOTAL_TIME];

    //The main static random number generator for the entire system
    public static long SEED;
    public static Random MAIN_RAND;
    public ScreenHandler(){
        SEED = System.currentTimeMillis();
        MAIN_RAND = new Random(SEED);
    }

    //sets the first screen up when initialised
    @Override
    public void create() {
        setMenuScreen();
    }

    //the method to initialise the second screen, passing along all variables
    public void setGameScreen()
    {
        simScreen=new SimMain(NUM_ANTS, NUM_RANTS, NUM_BOLDS, DROP_TIME, currentdir, windstr, PUDDLE_FREQ, PUDDLE_WIDTH);
        setScreen(simScreen);
    }

    //the method to initialise the first screen
    public void setMenuScreen()
    {
        preScreen=new Setup();
        setScreen(preScreen);
    }

    //the method to initialise the final screen, passing along all variables
    public void setLastScreen(){
        postScreen = new Results(this.droplist, this.broodcentrex, this.broodcentrey, this.boldscentrex, this.boldscentrey, this.ratio,
                this.nwbolds, this.nebolds, this.swbolds, this.sebolds);
        setScreen(postScreen);
    }


    @Override
    public void dispose() {

        super.dispose();
    }

    //the render method checks for a move on boolean in the first and second screens, to signal switching them
    @Override
    public void render() {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(getScreen() instanceof  Setup){
            Setup currentScreen = (Setup)getScreen();
            if (currentScreen.gogame) {
                setGameScreen();
            }
        }

        if(getScreen() instanceof  SimMain){
            SimMain currentScreen = (SimMain)getScreen();
            if (currentScreen.goresults) {
                setLastScreen();
            }
        }



        super.render();
    }

    //resizing the window
    @Override
    public void resize(int width, int height) {

        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }
}