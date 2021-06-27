package com.mygdx.leptosim.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by Ben J on 26/01/2016.
 */

//The results class represents the third and final screen in the simulation, which writes files and then allows closure
public class Results implements InputProcessor, Screen {

    //style parameters
    Skin stylo;
    Stage stage;

    //data arraylists for statistics
    public double[] droplist = new double[SimMain.TOTAL_TIME];
    public double[] broodcentrex = new double[SimMain.TOTAL_TIME];
    public double[] broodcentrey = new double[SimMain.TOTAL_TIME];
    public double[] boldscentrex = new double[SimMain.TOTAL_TIME];
    public double[] boldscentrey = new double[SimMain.TOTAL_TIME];
    public static double[] ratio = new double[SimMain.TOTAL_TIME];
    public static double[] nwbolds = new double[SimMain.TOTAL_TIME];
    public static double[] nebolds = new double[SimMain.TOTAL_TIME];
    public static double[] swbolds = new double[SimMain.TOTAL_TIME];
    public static double[] sebolds = new double[SimMain.TOTAL_TIME];

    //setting the statistics in the constructor, passed from the pervious simmain screen
    public Results(double[] droplist, double[] broodcentrex, double[] broodcentrey, double[] boldscentrex, double[] boldscentrey, double[] ratio,
                   double[] nwbolds, double[] nebolds, double[] swbolds, double[] sebolds
    ){
        this.droplist = droplist;
        this.broodcentrex = broodcentrex;
        this.broodcentrey = broodcentrey;
        this.boldscentrex = boldscentrex;
        this.boldscentrey = boldscentrey;
        this.ratio = ratio;
        this.nwbolds = nwbolds;
        this.nebolds = nebolds;
        this.swbolds = swbolds;
        this.sebolds = sebolds;
    }
    Label origin;


    //the method run at startup of the screen
    @Override
    public void show() {

        //defining style and each label and button on the screen
        stylo = new Skin(Gdx.files.internal("stylesheet.json"));
        stage = new Stage(new ScreenViewport());

        final Label titlebut = new Label("SIMULATION COMPLETE \nFILES TIMESTAMPED ON DESKTOP", stylo);
        titlebut.setWidth(200);
        titlebut.setHeight(50);
        titlebut.setPosition(5, Gdx.graphics.getHeight() - 300);
        stage.addActor(titlebut);

        final TextButton confirm = new TextButton("CLOSE", stylo, "default");
        confirm.setWidth(200);
        confirm.setHeight(50);
        confirm.setPosition(Gdx.graphics.getWidth()/2  - 100, 30);
        confirm.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
              Gdx.app.exit();
              return true;
            }
        });
        stage.addActor(confirm);

        stage.getViewport().update(SimMain.WIDTH, SimMain.HEIGHT);
        Gdx.input.setInputProcessor(stage);

        camera.translate(300,250);
        camera.update();

        //WRITE Boulders Dropped CSV
        try {
            FileWriter writa = new FileWriter("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\"+ SimMain.timeStamp + "\\bolsdropd.csv");
            writa.append("time");
            writa.append(',');
            writa.append("#");
            writa.append('\n');

            int dropcount = 0;
            for(double d: droplist){
                writa.append(Integer.toString(dropcount));
                writa.append(',');
                writa.append(Double.toString(d));
                writa.append('\n');
                dropcount++;
            }

            writa.flush();
            writa.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //WRITE CENTROIDS CSV
        try {
            FileWriter writa = new FileWriter("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\"+ SimMain.timeStamp + "\\bolcents.csv");
            writa.append("time");
            writa.append(',');
            writa.append("boldx");
            writa.append(',');
            writa.append("boldy");
            writa.append(',');
            writa.append("broodx");
            writa.append(',');
            writa.append("broody");
            writa.append('\n');

            for(int boliter = 0; boliter < broodcentrex.length; boliter++){
                writa.append(Integer.toString(boliter));
                writa.append(',');
                writa.append(Double.toString(boldscentrex[boliter]));
                writa.append(',');
                writa.append(Double.toString(boldscentrey[boliter]));
                writa.append(',');
                writa.append(Double.toString(broodcentrex[boliter]));
                writa.append(',');
                writa.append(Double.toString(broodcentrey[boliter]));
                writa.append('\n');
            }



            writa.flush();
            writa.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //WRITE RATIO CSV
        try {
            FileWriter writa = new FileWriter("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\"+ SimMain.timeStamp + "\\bolsratio.csv");
            writa.append("time");
            writa.append(',');
            writa.append("ratio");
            writa.append('\n');

            int dropcount = 0;
            for(double d: ratio){
                writa.append(Integer.toString(dropcount));
                writa.append(',');
                writa.append(Double.toString(d));
                writa.append('\n');
                dropcount++;
            }

            writa.flush();
            writa.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //WRITE QUADRANTS CSV
        try {
            FileWriter writa = new FileWriter("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\"+ SimMain.timeStamp + "\\bolquads.csv");
            writa.append("time");
            writa.append(',');
            writa.append("#ne");
            writa.append(',');
            writa.append("#se");
            writa.append(',');
            writa.append("#sw");
            writa.append(',');
            writa.append("#nw");
            writa.append('\n');

            for(int boliter = 0; boliter < nebolds.length; boliter++){
                writa.append(Integer.toString(boliter));
                writa.append(',');
                writa.append(Double.toString(nebolds[boliter]));
                writa.append(',');
                writa.append(Double.toString(sebolds[boliter]));
                writa.append(',');
                writa.append(Double.toString(swbolds[boliter]));
                writa.append(',');
                writa.append(Double.toString(nwbolds[boliter]));
                writa.append('\n');
            }

            writa.flush();
            writa.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    OrthographicCamera camera = new OrthographicCamera(SimMain.WIDTH, SimMain.HEIGHT);
    ShapeRenderer shapeRenderer = new ShapeRenderer();


    //method run every frame after display
    @Override
    public void render(float delta) {
        //render all graphic objects
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }



    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
