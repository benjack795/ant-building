package com.mygdx.leptosim.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by Ben J on 24/01/2016.
 */

//The setup class is the first main screen displayed. It handles the initial interface for setting variables, and passes them on.
public class Setup implements InputProcessor, Screen {
    //Style classes used in libGDX
    private Stage stage;
    private Skin stylo;
    private Table table;

    //boolean for going to the next screen
    public boolean gogame = false;

    //free parameters
    int NUM_ANTS = 75;
    int NUM_BOLDS = 70;
    int NUM_RANTS = 85;
    float DROP_TIME = 18;

    //free parameters for the wind, and the direction enum
    public enum Direction {NONE, NORTH, EAST, SOUTH,WEST}
    public static Direction currentdir = Direction.NONE;
    public float WIND_STR = 0.2f;

    //free parameters for the puddles
    public float PUDDLE_WIDTH = 60f;
    public float PUDDLE_FREQ = 0f;

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
    public void show() {

        //setting up all the stage objects
        stylo = new Skin(Gdx.files.internal("stylesheet.json"));
        stage = new Stage(new ScreenViewport());
        table = new Table();

        table.setWidth(stage.getWidth());
        table.align(Align.left);
        table.setPosition(10, Gdx.graphics.getHeight()/2);

        //setting up all the labels and text fields into a table
        //their actions when clicked are also defined
        //this is usually cycling through set values
        final Label titlebut = new Label("LEPTOTHORAX Nest Building Simulation", stylo);
        titlebut.setWidth(200);
        titlebut.setHeight(50);
        stage.addActor(titlebut);

        Label numbants = new Label("Num. Build Ants", stylo);
        final TextField antsfield = new TextField(Integer.toString(NUM_ANTS),stylo);
        numbants.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(NUM_ANTS <= 95){
                    NUM_ANTS = NUM_ANTS + 5;
                }else{
                    NUM_ANTS = 0;
                }
                antsfield.setText(Integer.toString(NUM_ANTS));
                return true;
            }
        });

        Label numrests = new Label("Num. Brood", stylo);
        final TextField broodfield = new TextField(Integer.toString(NUM_RANTS),stylo);
        numrests.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(NUM_RANTS <= 95){
                    NUM_RANTS = NUM_RANTS + 5;
                }else{
                    NUM_RANTS = 0;
                }
                broodfield.setText(Integer.toString(NUM_RANTS));
                return true;
            }
        });

        Label numbolds = new Label("Num. Boulders", stylo);
        final TextField boldsfield = new TextField(Integer.toString(NUM_BOLDS),stylo);
        numbolds.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(NUM_BOLDS <= 490){
                    NUM_BOLDS = NUM_BOLDS + 10;
                }else{
                    NUM_BOLDS = 0;
                }
                boldsfield.setText(Integer.toString(NUM_BOLDS));
                return true;
            }
        });

        Label dropper = new Label("Dropping Time (s)", stylo);
        final TextField dropfield = new TextField(Float.toString(DROP_TIME/60),stylo);
        dropper.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(DROP_TIME <= 330){
                    DROP_TIME = DROP_TIME + 30;
                }else{
                    DROP_TIME = 0;
                }
                dropfield.setText(Float.toString(DROP_TIME/60));
                return true;
            }
        });

        Label winddir = new Label("Wind Direction" , stylo);
        winddir.setColor(Color.CYAN);
        final TextField windfield = new TextField(currentdir.toString() ,stylo);
        winddir.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                switch(currentdir){
                    case NONE:
                        currentdir = Direction.NORTH;
                        break;
                    case NORTH:
                        currentdir = Direction.EAST;
                        break;
                    case EAST:
                        currentdir = Direction.SOUTH;
                        break;
                    case SOUTH:
                        currentdir = Direction.WEST;
                        break;
                    case WEST:
                        currentdir = Direction.NONE;
                        break;
                }
                windfield.setText(currentdir.toString());
                return true;
            }
        });

        Label windstrlab = new Label("Wind Force" , stylo);
        windstrlab.setColor(Color.CYAN);
        final TextField windstrfield = new TextField("LOW" ,stylo);
        windstrlab.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(WIND_STR == 0.2f) {
                    WIND_STR = 0.5f;
                    windstrfield.setText("MEDIUM");
                }
                else if(WIND_STR == 0.5f) {
                    WIND_STR = 2f;
                    windstrfield.setText("HIGH");
                }
                else if(WIND_STR == 2f) {
                    WIND_STR = 0.2f;
                    windstrfield.setText("LOW");
                }
                return true;
            }
        });

        Label pudwidlab = new Label("Maximum Puddle Width", stylo);
        pudwidlab.setColor(Color.BLUE);
        final TextField pudwidfield = new TextField("60",stylo);
        pudwidlab.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(PUDDLE_WIDTH <= 140){
                    PUDDLE_WIDTH = PUDDLE_WIDTH + 10;
                }else{
                    PUDDLE_WIDTH = 60;
                }
                pudwidfield.setText(Integer.toString((int)PUDDLE_WIDTH));
                return true;
            }
        });

        Label pudfreqlab = new Label("Puddle Frequency", stylo);
        pudfreqlab.setColor(Color.BLUE);
        final TextField pudfreqfield = new TextField("NONE",stylo);
        pudfreqlab.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(PUDDLE_FREQ == 0){
                    PUDDLE_FREQ = 15;
                    pudfreqfield.setText("LOW");
                }
                else if(PUDDLE_FREQ == 15){
                    PUDDLE_FREQ = 45;
                    pudfreqfield.setText("MEDIUM");
                }else if(PUDDLE_FREQ == 45){
                    PUDDLE_FREQ = 80;
                    pudfreqfield.setText("HIGH");
                }else if(PUDDLE_FREQ == 80){
                    PUDDLE_FREQ = 0;
                    pudfreqfield.setText("NONE");
                }
                 return true;
            }
        });

        Label excalab = new Label("Excavation Mode?", stylo);
        excalab.setColor(Color.BROWN);
        final TextField excafield = new TextField(Boolean.toString(SimMain.excavate),stylo);
        excalab.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                SimMain.excavate = !SimMain.excavate;
                excafield.setText(Boolean.toString(SimMain.excavate));
                return true;
            }
        });

        Label actbrolab = new Label("Active Brood?", stylo);
        actbrolab.setColor(Color.LIME);
        final TextField actbrofield = new TextField(Boolean.toString(SimMain.broodgo),stylo);
        actbrolab.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                SimMain.broodgo = !SimMain.broodgo;
                actbrofield.setText(Boolean.toString(SimMain.broodgo));
                return true;
            }
        });

        //SETTING SEED
        Label seedlab = new Label("SEED", stylo);
        seedlab.setColor(Color.RED);
        final TextField seedfield = new TextField(Long.toString(ScreenHandler.SEED),stylo);
        seedlab.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //set the seed to the contents of the textfield when the label is clicked
                ScreenHandler.SEED = Long.valueOf(seedfield.getText()).longValue();
                ScreenHandler.MAIN_RAND.setSeed(ScreenHandler.SEED);
                seedfield.setText("SEED SET");
                return true;
            }
        });


        Label explain = new Label("[CLICK LABEL/ENTER TEXT TO ADJUST VALUE]", stylo);

        //handling the table layout
        table.padTop(30);
        table.add(explain).padBottom(30);
        table.row();
        table.add(seedlab).pad(0,0,30,30);
        table.add(seedfield).padBottom(30);
        table.row();
        table.add(numbants).pad(0,0,30,30);
        table.add(antsfield).padBottom(30);
        table.row();
        table.add(numrests).pad(0,0,30,30);
        table.add(broodfield).padBottom(30);
        table.row();
        table.add(numbolds).pad(0,0,30,30);
        table.add(boldsfield).padBottom(30);
        table.row();
        table.add(dropper).pad(0,0,30,30);
        table.add(dropfield).padBottom(30);
        table.row();
        table.add(winddir).pad(0,0,30,30);
        table.add(windfield).padBottom(30);
        table.row();
        table.add(windstrlab).pad(0,0,30,30);
        table.add(windstrfield).padBottom(30);
        table.row();
        table.add(pudfreqlab).pad(0,0,30,30);
        table.add(pudfreqfield).padBottom(30);
        table.row();
        table.add(pudwidlab).pad(0,0,30,30);
        table.add(pudwidfield).padBottom(30);
        table.row();
        table.add(excalab).pad(0,0,30,30);
        table.add(excafield).padBottom(30);
        table.row();
        table.add(actbrolab).pad(0,0,30,30);
        table.add(actbrofield).padBottom(30);
        table.row();

        titlebut.setPosition(Gdx.graphics.getWidth()/2 - 100, Gdx.graphics.getHeight() - 80);
        stage.addActor(table);

        final TextButton confirm = new TextButton("START", stylo, "default");
        confirm.setWidth(200);
        confirm.setHeight(50);
        confirm.setPosition(Gdx.graphics.getWidth()/2  - 100, 30);
        confirm.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //when the start button is clicked, transfer the values back to screenhandler and set the move-on boolean
                //this was fixed to handle continuous values by setting the values to the text in the box, and not the variable itself
                //this allowed for easy custom values by changing the text in the box
                ScreenHandler.NUM_ANTS = Integer.parseInt(antsfield.getText());
                ScreenHandler.NUM_BOLDS = Integer.parseInt(boldsfield.getText());
                ScreenHandler.NUM_RANTS = Integer.parseInt(broodfield.getText());
                ScreenHandler.DROP_TIME = Float.parseFloat(dropfield.getText())*60;
                ScreenHandler.currentdir = Setup.currentdir;
                ScreenHandler.windstr = WIND_STR;
                ScreenHandler.PUDDLE_FREQ = PUDDLE_FREQ;
                ScreenHandler.PUDDLE_WIDTH = PUDDLE_WIDTH;
                gogame = true;
                return true;
            }
        });
        stage.addActor(confirm);

        stage.getViewport().update(SimMain.WIDTH, SimMain.HEIGHT);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        //render the graphics every cycle
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
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
