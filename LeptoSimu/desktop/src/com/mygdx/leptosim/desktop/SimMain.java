package com.mygdx.leptosim.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ben J on 13/10/2015.
 */

//The SimMain class represents the second and most important screen that handles running the simulation itself.
public class SimMain implements InputProcessor, Screen {

    //Height and width of the simulation
    final static int HEIGHT = 900;
    final static int WIDTH = 900;

    //Free parameters, includes the total runing time in seconds, set to 5min  (300s)
    int NUM_ANTS;
    int NUM_BOLDS;
    int NUM_RANTS;
    float DROP_TIME;
    final static int TOTAL_TIME = 300;
    static int BOLS_DROPD = 0;

    //Movement constants for the ants walking behaviours
    final static float ANT_SPEED = 0.05f;
    final static float ANT_TURN = 0.004f;
    final static float PI = (float)Math.PI;

    //music file
    Music maintune;

    //camera and rendering objects
    private OrthographicCamera camera;
    private Matrix4 debugMatrix;
    private SpriteBatch batch;
    BitmapFont font;

    //arraylists for holding all instances of types of objects (simobj is used as a generic for both types of boulder)
    private ArrayList<RestAnt> rants = new ArrayList();
    private ArrayList<Ant> ants= new ArrayList();
    private ArrayList<SimObj> bolds = new ArrayList();

    //constants for the physics engine
    World world =new World(new Vector2(0,0f),true);
    Box2DDebugRenderer debugRenderer;
    final float PIXELS_TO_METERS = 100f;
    float torque = 0.0f;
    boolean drawSprite = true;

    //object densities
    public float HARD_BOL_DENSE = 30f;
    public float BUILDER_DENSE = 0.5f;
    public float BROOD_DENSE = 6f;
    public float BOL_DENSE = 3f;
    public float PUD_DENSE = 100f;

    //edge physics bodies
    Body botedgebod;
    Body topedgebod;
    Body leftedgebod;
    Body rightedgebod;

    //arraylists of objects to be added/removed on the next render cycle
    ArrayList<SimObj> additions = new ArrayList();
    ArrayList<SimObj> removables = new ArrayList();

    //booleans to allow the movements of builders and brood to play out
    boolean controlgo = false;
    static boolean broodgo = true;

    //boolean to move to the next scren
    public boolean goresults = false;

    //csv data arrays
    public double[] droplist = new double[TOTAL_TIME];
    public double[] broodcentrex = new double[TOTAL_TIME];
    public double[] broodcentrey = new double[TOTAL_TIME];
    public double[] boldscentrex = new double[TOTAL_TIME];
    public double[] boldscentrey = new double[TOTAL_TIME];
    public double[] ratio = new double[TOTAL_TIME];
    public double[] nwbolds = new double[TOTAL_TIME];
    public double[] nebolds = new double[TOTAL_TIME];
    public double[] swbolds = new double[TOTAL_TIME];
    public double[] sebolds = new double[TOTAL_TIME];

    //wind parameters
    public static Setup.Direction currentdir = Setup.Direction.NONE;
    public static float windstr = 0.2f;
    float currenttime = 0;
    boolean windy = false;

    //puddle parameters
    public float PUD_FREQ = 0f;
    public float PUD_WIDTH = 10;
    public ArrayList<Puddle> puds = new ArrayList();

    //boolean to enable excavation mode
    static boolean excavate = false;

    //set the free parameters in the constructor
    public SimMain(int numants, int numrants, int numbolds, float droptime, Setup.Direction winddir, float windstr, float PUD_FREQ, float PUD_WIDTH){
        NUM_ANTS = numants;
        NUM_RANTS = numrants;
        NUM_BOLDS = numbolds;
        DROP_TIME = droptime;
        this.currentdir = winddir;
        this.windstr = windstr;
        this.windstr = this.windstr/30;
        this.PUD_FREQ = PUD_FREQ;
        this.PUD_WIDTH = PUD_WIDTH;
    }

    //format the timestamp for the datafolder
    static String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss").format(new Date());
    @Override
    public void show() {
        //create the timestamped folder on the desktop
        File dir = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\" + timeStamp);
        dir.mkdir();

        //play the music
        maintune = Gdx.audio.newMusic(Gdx.files.internal("rsarabian2.mp3"));
        maintune.setLooping(true);
        maintune.play();

        //define the textures for the sprites of each type of object
        Texture antext = new Texture("antext.png");
        final Texture anboltext = new Texture("antwithrock.png");
        Texture botext = new Texture("boulder.png");
        Texture resttext = new Texture("resttext.png");

        //Initialise brood
        for(int count = 0; count < NUM_RANTS; count++){
            RestAnt rant = new RestAnt();
            rant.width = 14;
            rant.height = 14;
            rant.sprite = new Sprite(resttext);
            batch = new SpriteBatch();
            rant.sprite.setPosition(ScreenHandler.MAIN_RAND.nextInt(100)-50,ScreenHandler.MAIN_RAND.nextInt(100)-150);

            //DynamicBody
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.linearDamping = 2f;
            bodyDef.angularDamping = 2f;
            bodyDef.position.set((rant.sprite.getX() + rant.sprite.getWidth()/2)/PIXELS_TO_METERS, (rant.sprite.getY() + rant.sprite.getHeight()/2)/PIXELS_TO_METERS);
            rant.body = world.createBody(bodyDef);

            //Fixture
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(rant.sprite.getWidth()/2 / PIXELS_TO_METERS,  rant.sprite.getHeight()/2 /PIXELS_TO_METERS);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = BROOD_DENSE;
            rant.body.createFixture(fixtureDef);

            rants.add(rant);
            shape.dispose();
        }

        //Initialise builders
        for(int count = 0; count < NUM_ANTS; count++){
            Ant ant = new Ant();
            ant.width = 14;
            ant.height = 14;
            ant.sprite = new Sprite(antext);
            batch = new SpriteBatch();
            ant.sprite.setPosition(ScreenHandler.MAIN_RAND.nextInt(200)-100,ScreenHandler.MAIN_RAND.nextInt(100)-300);

            //DynamicBody
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.linearDamping = 2f;
            bodyDef.angularDamping = 2f;
            bodyDef.position.set((ant.sprite.getX() + ant.sprite.getWidth()/2)/PIXELS_TO_METERS, (ant.sprite.getY() + ant.sprite.getHeight()/2)/PIXELS_TO_METERS);
            ant.body = world.createBody(bodyDef);

            //Fixture
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(ant.sprite.getWidth()/2 / PIXELS_TO_METERS,  ant.sprite.getHeight()/2 /PIXELS_TO_METERS);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = BUILDER_DENSE;
            ant.body.createFixture(fixtureDef);

            ants.add(ant);
            shape.dispose();
        }

        //change the boulder count if in excavation mode
        if(excavate){
            NUM_BOLDS = 300;
        }

        //Initialise the loose boulders into the simulation
        for(int count = 0; count < NUM_BOLDS; count++){
            Boulder bol = new Boulder();
            bol.sprite = new Sprite(botext);
            batch = new SpriteBatch();
            //adjust their positions based on whether or not in excavation mode
            if(excavate)
            {
                bol.sprite.setPosition(ScreenHandler.MAIN_RAND.nextInt(900)-450, ScreenHandler.MAIN_RAND.nextInt(900)-450);
            }else{
                bol.sprite.setPosition(ScreenHandler.MAIN_RAND.nextInt(900)-450, ScreenHandler.MAIN_RAND.nextInt(330) +120);
            }

            //DynamicBody
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.linearDamping = 2f;
            bodyDef.angularDamping = 2f;
            bodyDef.position.set((bol.sprite.getX() + bol.sprite.getWidth()/2)/PIXELS_TO_METERS, (bol.sprite.getY() + bol.sprite.getHeight()/2)/PIXELS_TO_METERS);
            bol.body = world.createBody(bodyDef);

            //Fixture
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(bol.sprite.getWidth()/2 / PIXELS_TO_METERS,  bol.sprite.getHeight()/2 /PIXELS_TO_METERS);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = BOL_DENSE;
            bol.body.createFixture(fixtureDef);

            bolds.add(bol);
            shape.dispose();
        }

        //Initialise Edges
        float w = Gdx.graphics.getWidth()/PIXELS_TO_METERS;
        float h = (Gdx.graphics.getHeight()/PIXELS_TO_METERS);

        //BOTTOM EDGE
        BodyDef botedgedef = new BodyDef();
        botedgedef.type = BodyDef.BodyType.StaticBody;

        botedgedef.position.set(0,(h-12)/PIXELS_TO_METERS);
        FixtureDef botedgefix = new FixtureDef();

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-w/2,-h/2,w/2,-h/2);
        botedgefix.shape = edgeShape;

        botedgebod = world.createBody(botedgedef);
        botedgebod.createFixture(botedgefix);
        edgeShape.dispose();

        //TOP EDGE
        BodyDef topedgedef = new BodyDef();
        topedgedef.type = BodyDef.BodyType.StaticBody;
        topedgedef.position.set(0,(12-h) /PIXELS_TO_METERS);
        FixtureDef topedgefix = new FixtureDef();

        EdgeShape edgeShape2 = new EdgeShape();
        edgeShape2.set(-w/2,h/2,w/2,h/2);
        topedgefix.shape = edgeShape2;

        topedgebod = world.createBody(topedgedef);
        topedgebod.createFixture(topedgefix);
        edgeShape2.dispose();

        //LEFT EDGE
        BodyDef leftedgedef = new BodyDef();
        leftedgedef.type = BodyDef.BodyType.StaticBody;
        leftedgedef.position.set((6-w)/PIXELS_TO_METERS,0);
        FixtureDef leftedgefix = new FixtureDef();

        EdgeShape edgeShape3 = new EdgeShape();
        edgeShape3.set(-w/2,-h/2,-w/2,h/2);
        leftedgefix.shape = edgeShape3;

        leftedgebod = world.createBody(leftedgedef);
        leftedgebod.createFixture(leftedgefix);
        edgeShape3.dispose();

        //RIGHT EDGE
        BodyDef rightedgedef = new BodyDef();
        rightedgedef.type = BodyDef.BodyType.StaticBody;
        rightedgedef.position.set((w-6)/PIXELS_TO_METERS,0);
        final FixtureDef rightedgefix = new FixtureDef();

        EdgeShape edgeShape4 = new EdgeShape();
        edgeShape4.set(w/2,-h/2,w/2,h/2);
        rightedgefix.shape = edgeShape4;

        rightedgebod = world.createBody(rightedgedef);
        rightedgebod.createFixture(rightedgefix);
        edgeShape4.dispose();

        Gdx.input.setInputProcessor(this);

        //setting renderer objects
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(WIDTH,HEIGHT);

        //setting the font
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        //defining collision behaviours
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                //bouncing off the edges
                if (contact.getFixtureA().getBody() == leftedgebod) {
                    Body b = contact.getFixtureB().getBody();
                    b.applyForceToCenter(new Vector2(2, 0), true);
                }

                if (contact.getFixtureA().getBody() == rightedgebod) {
                    Body b = contact.getFixtureB().getBody();
                    b.applyForceToCenter(new Vector2(-2, 0), true);
                }

                if (contact.getFixtureA().getBody() == topedgebod) {
                    Body b = contact.getFixtureB().getBody();
                    b.applyForceToCenter(new Vector2(0, -2), true);
                }

                if (contact.getFixtureA().getBody() == botedgebod) {
                    Body b = contact.getFixtureB().getBody();
                    b.applyForceToCenter(new Vector2(0, 2), true);
                }

                //picking up boulders on collision
                if (contact.getFixtureA().getDensity() == BUILDER_DENSE && contact.getFixtureB().getDensity() == BOL_DENSE){
                    Ant chosenant = new Ant();
                    Boulder chosenbold = new Boulder();

                    for(Ant a: ants){
                        if(contact.getFixtureA().getBody() == a.body){
                            chosenant = a;
                        }
                    }
                    for(SimObj b: bolds){
                        if(b instanceof Boulder){
                            if(contact.getFixtureB().getBody() == b.body){
                                chosenbold = (Boulder)b;
                            }
                        }
                    }

                    //changing the ant sprite and removing the boulder
                    if(chosenant.holding == false){
                        chosenant.sprite = new Sprite(anboltext);
                        chosenant.holding = true;
                        removables.add(chosenbold);
                        bolds.remove(chosenbold);
                    }


                }

                //starting the timer on a collision with brood
                if (contact.getFixtureA().getDensity() == BROOD_DENSE && contact.getFixtureB().getDensity() == BUILDER_DENSE){
                    Ant chosenant = new Ant();
                    for(Ant a: ants){
                        if(a.body == contact.getFixtureB().getBody()){
                            chosenant = a;
                        }
                    }
                    chosenant.dropenabled = true;
                    Body b = contact.getFixtureB().getBody();
                    b.applyForceToCenter(-MathUtils.cos(b.getAngle() + (PI/2)),-MathUtils.sin(b.getAngle() + (PI/2)),true);
                    contact.getFixtureB().getBody().applyTorque(ANT_TURN*3f, true);
                }

                //bouncing builders off each other
                if (contact.getFixtureA().getDensity() == BUILDER_DENSE && contact.getFixtureB().getDensity() == BUILDER_DENSE){
                    Body a = contact.getFixtureA().getBody();
                    Body b = contact.getFixtureB().getBody();

                    a.applyForceToCenter(-MathUtils.cos(a.getAngle() + (PI/2)),-MathUtils.sin(a.getAngle() + (PI/2)),true);
                    contact.getFixtureA().getBody().applyTorque(ANT_TURN*3f, true);

                    b.applyForceToCenter(-MathUtils.cos(b.getAngle() + (PI/2)),-MathUtils.sin(b.getAngle() + (PI/2)),true);
                    contact.getFixtureB().getBody().applyTorque(ANT_TURN*3f, true);
                }

                //dropping boulders on collision with a dropped boulder
                if (contact.getFixtureA().getDensity() == BUILDER_DENSE && contact.getFixtureB().getDensity() == HARD_BOL_DENSE){
                    Body b = contact.getFixtureA().getBody();
                    b.applyForceToCenter(-2* MathUtils.cos(b.getAngle() + (PI/2)),-2* MathUtils.sin(b.getAngle() + (PI/2)),true);
                    contact.getFixtureA().getBody().applyTorque(ANT_TURN*3f, true);

                    for(Ant a :ants){
                        if(a.body == b){
                            if(a.holding){
                                a.holding = false;
                                //changing the ants sprite back and creating a droppped boulder to the side
                                HardBoulder bol = new HardBoulder();
                                bol.sprite = new Sprite();
                                bol.holdingx = a.body.getPosition().x + 0.2f* MathUtils.cos(a.body.getAngle());
                                bol.holdingy = a.body.getPosition().y + 0.2f* MathUtils.sin(a.body.getAngle());
                                bolds.add(bol);
                                additions.add(bol);
                            }
                        }
                    }
                }

                //bouncing off puddles
                if (contact.getFixtureA().getDensity() == BUILDER_DENSE && contact.getFixtureB().getDensity() == PUD_DENSE){
                    Body b = contact.getFixtureA().getBody();
                    b.applyForceToCenter(-2* MathUtils.cos(b.getAngle() + (PI/2)),-2* MathUtils.sin(b.getAngle() + (PI/2)),true);
                    contact.getFixtureA().getBody().applyTorque(ANT_TURN*3f, true);
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        //setting the builders to move
        controlgo = true;
        for(Ant a: ants){
            a.droptime = 0;
        }


    }


    @Override
    public void resize(int width, int height) {

    }


    //time elapsed parameter
    private float elapsed = 0;

    @Override
    public void render(float delta) {

        //rendering graphics per cycle
        Texture antext = new Texture("antext.png");
        final Texture anboltext = new Texture("antwithrock.png");
        Texture botext = new Texture("boulder.png");
        Texture hobotext = new Texture("hardboulder.png");
        camera.update();
        world.step(1f/60f, 6, 2);
        Gdx.gl.glClearColor(0.6f, 0.6f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);




        //Removing garbage
        for(SimObj b: removables){
                b.visible = false;
            if(b instanceof  Puddle){
                puds.remove(b);
            }
            if(b.body != null){
                world.destroyBody(b.body);
            }

        }
        removables.clear();

        //Handling additions
        for(SimObj bol: additions){
            if(bol instanceof Boulder){
                Boulder nubol = (Boulder)bol;
                nubol.sprite = new Sprite(botext);
                //DynamicBody
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                bodyDef.linearDamping = 2f;
                bodyDef.angularDamping = 2f;
                bodyDef.position.set(nubol.holdingx, nubol.holdingy);
                nubol.body = world.createBody(bodyDef);

                //Fixture
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(nubol.sprite.getWidth()/2 / PIXELS_TO_METERS,  nubol.sprite.getHeight()/2 /PIXELS_TO_METERS);
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = BOL_DENSE;
                nubol.body.createFixture(fixtureDef);
                shape.dispose();
            }

            if(bol instanceof HardBoulder){
                //initialising  a hardboulder if it needs to be added
                HardBoulder nubol = (HardBoulder)bol;
                nubol.sprite = new Sprite(hobotext);
                //DynamicBody
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                bodyDef.linearDamping = 2f;
                bodyDef.angularDamping = 2f;
                bodyDef.position.set(nubol.holdingx, nubol.holdingy);
                nubol.body = world.createBody(bodyDef);

                //Fixture
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(nubol.sprite.getWidth()/2 / PIXELS_TO_METERS,  nubol.sprite.getHeight()/2 /PIXELS_TO_METERS);
                FixtureDef fixtureDef2 = new FixtureDef();
                fixtureDef2.shape = shape;
                fixtureDef2.density = HARD_BOL_DENSE;
                nubol.body.createFixture(fixtureDef2);
                shape.dispose();

                BOLS_DROPD++;
            }

            if(bol instanceof Puddle){
                //initialising a puddle if it needs to be added
                Puddle nupud = (Puddle)bol;
                Texture puddletext = new Texture("puddle.png");
                nupud.sprite = new Sprite(puddletext);

                //DynamicBody
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.DynamicBody;
                bodyDef.linearDamping = 2f;
                bodyDef.angularDamping = 2f;
                bodyDef.position.set(6*ScreenHandler.MAIN_RAND.nextFloat() - 3,6*ScreenHandler.MAIN_RAND.nextFloat() - 3);
                nupud.body = world.createBody(bodyDef);

                //Fixture
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(nupud.diam/2 / PIXELS_TO_METERS,  nupud.diam/2  /PIXELS_TO_METERS);
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = PUD_DENSE;
                nupud.body.createFixture(fixtureDef);
                shape.dispose();
            }


        }
        additions.clear();


        //Updating positions
        for(RestAnt r: rants){
            r.body.applyTorque(torque, true);
            r.sprite.setPosition((r.body.getPosition().x * PIXELS_TO_METERS) - r.sprite.getWidth()/2 , (r.body.getPosition().y * PIXELS_TO_METERS) -r.sprite.getHeight()/2 );
            r.sprite.setRotation((float)Math.toDegrees(r.body.getAngle()));
        }
        //applying forces to each object type
        for(Ant a: ants){
            //updating builder sprite if necessary
            if(!a.holding){
                a.sprite = new Sprite(antext);
            }
            else{
                a.sprite = new Sprite(anboltext);
            }
            a.body.applyTorque(torque, true);
            a.sprite.setPosition((a.body.getPosition().x * PIXELS_TO_METERS) - a.sprite.getWidth()/2 , (a.body.getPosition().y * PIXELS_TO_METERS) -a.sprite.getHeight()/2 );
            a.sprite.setRotation((float)Math.toDegrees(a.body.getAngle()));
        }
        for(SimObj b: bolds){
            b.body.applyTorque(torque, true);
            b.sprite.setPosition((b.body.getPosition().x * PIXELS_TO_METERS) - b.sprite.getWidth()/2 , (b.body.getPosition().y * PIXELS_TO_METERS) -b.sprite.getHeight()/2 );
            b.sprite.setRotation((float)Math.toDegrees(b.body.getAngle()));
        }
        for(Puddle p: puds){
            p.body.applyTorque(torque, true);
            p.sprite.setPosition((p.body.getPosition().x * PIXELS_TO_METERS) - p.sprite.getWidth()/2 , (p.body.getPosition().y * PIXELS_TO_METERS) -p.sprite.getHeight()/2 );
            p.sprite.setRotation((float)Math.toDegrees(p.body.getAngle()));
        }

        //scale down the physics to the camera area
        batch.setProjectionMatrix(camera.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                PIXELS_TO_METERS, 0);

        //draw all sprites
        batch.begin();
        if(drawSprite){
            for(Ant a: ants){
                if(a.visible){
                    batch.draw(a.sprite, a.sprite.getX(), a.sprite.getY(),a.sprite.getOriginX(),a.sprite.getOriginY(),
                            a.sprite.getWidth(), a.sprite.getHeight(), a.sprite.getScaleX(), a.sprite.getScaleY(), a.sprite.getRotation());
                }
            }
            for(RestAnt r: rants){
                if(r.visible){
                    batch.draw(r.sprite, r.sprite.getX(), r.sprite.getY(),r.sprite.getOriginX(),r.sprite.getOriginY(),
                            r.sprite.getWidth(), r.sprite.getHeight(), r.sprite.getScaleX(), r.sprite.getScaleY(), r.sprite.getRotation());
                }
            }
            for(SimObj b: bolds){
                if(b.visible){
                    batch.draw(b.sprite, b.sprite.getX(), b.sprite.getY(),b.sprite.getOriginX(),b.sprite.getOriginY(),
                            b.sprite.getWidth(), b.sprite.getHeight(), b.sprite.getScaleX(), b.sprite.getScaleY(), b.sprite.getRotation());
                }
            }
            for(Puddle p: puds){
                if(p.visible){
                    batch.draw(p.sprite, p.sprite.getX()+100-p.diam/2, p.sprite.getY()+100-p.diam/2,p.sprite.getOriginX(),p.sprite.getOriginY(),
                            p.diam, p.diam, p.sprite.getScaleX(), p.sprite.getScaleY(), p.sprite.getRotation());

                }
            }
        }
        //only draw certain sprites if the nestview is on, for debugging
        else{
            for(SimObj b: bolds){
                if(b.visible && b instanceof HardBoulder){
                    batch.draw(b.sprite, b.sprite.getX(), b.sprite.getY(),b.sprite.getOriginX(),b.sprite.getOriginY(),
                            b.sprite.getWidth(), b.sprite.getHeight(), b.sprite.getScaleX(), b.sprite.getScaleY(), b.sprite.getRotation());
                }
            }
            for(RestAnt r: rants){
                if(r.visible){
                    batch.draw(r.sprite, r.sprite.getX(), r.sprite.getY(),r.sprite.getOriginX(),r.sprite.getOriginY(),
                            r.sprite.getWidth(), r.sprite.getHeight(), r.sprite.getScaleX(), r.sprite.getScaleY(), r.sprite.getRotation());
                }
            }
        }

        //CONTROL BEHAVIOUR FOR BUILDERS
        if(controlgo){
            float dice;
            //probability distribution
            for(Ant a: ants){
                dice = ScreenHandler.MAIN_RAND.nextInt(100);
                if(dice < 61){
                    a.body.applyForceToCenter(ANT_SPEED* MathUtils.cos(a.body.getAngle() + (PI/2)),ANT_SPEED* MathUtils.sin(a.body.getAngle() + (PI/2)),true);
                }
                else if(dice < 81){
                    a.body.applyTorque(ANT_TURN,true);
                }
                else if(dice > 80){
                    a.body.applyTorque(-ANT_TURN,true);
                }
            }
        }

        //BROOD CONTROL BEHAVIOUR
        if(broodgo){
            //CENTROID REST ANTS
            double broodx = 0;
            double broody = 0;
            for(RestAnt r: rants){
                broodx += r.body.getPosition().x;
                broody += r.body.getPosition().y;
            }
            double broodcentroidx = broodx/rants.size();
            double broodcentroidy = broody/rants.size();

            double dice;
            for(RestAnt b: rants){

                //DISTANCE FROM CENTROID
                double eucdistcent = Math.sqrt((Math.pow(b.body.getPosition().x - broodcentroidx,2)+ Math.pow(b.body.getPosition().y - broodcentroidy,2)));

                //thresholding the walkprobability (DISCUSSION)
                double walkprob  = 1 - (eucdistcent/2);
                if(walkprob < 0){
                    walkprob = 0;
                }
                walkprob = walkprob * 100;

                dice = ScreenHandler.MAIN_RAND.nextInt(100);
                if(dice < walkprob){
                    b.body.applyForceToCenter(0.6f*ANT_SPEED* MathUtils.cos(b.body.getAngle() + (PI/2)),0.6f*ANT_SPEED* MathUtils.sin(b.body.getAngle() + (PI/2)),true);
                }
                else if(dice < (walkprob + ((100-walkprob)/2))){
                    b.body.applyTorque(ANT_TURN*5,true);
                }
                else if(dice > (walkprob + ((100-walkprob)/2))){
                    b.body.applyTorque(-ANT_TURN*5,true);
                }
            }
        }

        //DRAWING THE HUD LABEL
        font.draw(batch,
                "ElapsedTime " + (int)elapsed/60 + "s   " + BOLS_DROPD + "/" + (NUM_BOLDS + NUM_ANTS) + " Placed   PAUSE WITH [P]" , -440, -432 );

        batch.end();

        //INCREMENTING BUILDERS DROP TIMER
        for(Ant a :ants){
            if(a.droptime == DROP_TIME){
                a.droptime = 0;
                a.dropenabled = false;
                if(a.holding){
                    a.holding = false;

                    HardBoulder bol = new HardBoulder();
                    bol.sprite = new Sprite();
                    bol.holdingx = a.body.getPosition().x + 0.2f* MathUtils.cos(a.body.getAngle());
                    bol.holdingy = a.body.getPosition().y + 0.2f* MathUtils.sin(a.body.getAngle());
                    bolds.add(bol);
                    additions.add(bol);
                }
            }
            if(a.dropenabled){
                a.droptime++;
            }

        }

        //WIND
        Array<Body> bods = new Array();
        world.getBodies(bods);
        int dice = ScreenHandler.MAIN_RAND.nextInt(100);
        //MAKING SURE THE WIND IS APPLIED FOR ONE SECOND
        if(elapsed == currenttime + 60){
            windy = false;
        }
        if(dice < 11 && elapsed % 60 == 0){
            windy = true;
            currenttime = elapsed;
        }
        //applying wind force
        if(windy){
            switch(currentdir){
                case NONE:
                    break;
                case NORTH:
                    for(Body b: bods){
                        b.applyForceToCenter(0,windstr, true);
                    }
                    break;
                case EAST:
                    for(Body b: bods){
                        b.applyForceToCenter(windstr,0, true);
                    }
                    break;
                case SOUTH:
                    for(Body b: bods){
                        b.applyForceToCenter(0,-windstr, true);
                    }
                    break;
                case WEST:
                    for(Body b: bods){
                        b.applyForceToCenter(-windstr,0, true);
                    }
                    break;
            }
        }

        //PUDDLES
        if((int)elapsed % 180 == 0){
            int puddlecheck = ScreenHandler.MAIN_RAND.nextInt(100);
            if(puddlecheck < PUD_FREQ){
                //ADD PUDDLE
                Puddle pud = new Puddle(ScreenHandler.MAIN_RAND.nextInt(50)+(int)PUD_WIDTH - 50);
                pud.sprite = new Sprite();
                puds.add(pud);
                additions.add(pud);
            }
        }
        //DRAINING THE PUDDLES LIFETIMERS
        for(Puddle p: puds){
            p.lifetime--;
            if(p.lifetime == 0){
                //remove puddle if out of time
                removables.add(p);
            }
        }


        //GETTING STATS PER SECOND
        if((int)elapsed % 60 == 0){
            //TOTAL BOULDERS DROPPED
            droplist[(int)elapsed/60] = BOLS_DROPD;

            //CENTROID REST ANTS
            double totalbroodx = 0;
            double totalbroody = 0;
            for(RestAnt r: rants){
                totalbroodx += r.body.getPosition().x;
                totalbroody += r.body.getPosition().y;
            }
            broodcentrex[(int)elapsed/60] = totalbroodx/rants.size();
            broodcentrey[(int)elapsed/60] = totalbroody/rants.size();

            //CENTROID DROPPEDBOULDERS
            double totaldroppedx = 0;
            double totaldroppedy = 0;
            double droppedcount = 0;
            for(SimObj b: bolds){
                if (b instanceof HardBoulder) {
                    if(b.body != null){
                        totaldroppedx += b.body.getPosition().x;
                        totaldroppedy += b.body.getPosition().y;
                        droppedcount++;
                    }

                }
            }
            if(droppedcount != 0){

                boldscentrex[(int)elapsed/60] = totaldroppedx/droppedcount;
                boldscentrey[(int)elapsed/60] = totaldroppedy/droppedcount;
            }
            else{
                boldscentrex[(int)elapsed/60] = 0;
                boldscentrey[(int)elapsed/60] = 0;
            }


            //HEIGHT TO WIDTH RATIO
            double highestboldx = Integer.MIN_VALUE;
            double lowestboldx = Integer.MAX_VALUE;
            double highestboldy = Integer.MIN_VALUE;
            double lowestboldy = Integer.MAX_VALUE;
            double finalratio = 0;

            for(SimObj b: bolds){
                if (b instanceof HardBoulder) {
                    if(b.body !=null){
                        if(b.body.getPosition().x > highestboldx){
                            highestboldx = b.body.getPosition().x;
                        }
                        if(b.body.getPosition().x < lowestboldx){
                            lowestboldx = b.body.getPosition().x;
                        }
                        if(b.body.getPosition().y > highestboldy){
                            highestboldy = b.body.getPosition().y;
                        }
                        if(b.body.getPosition().y < lowestboldy){
                            lowestboldy = b.body.getPosition().y;
                        }
                    }
                }
            }

            double xrange = highestboldx - lowestboldx;
            double yrange = highestboldy - lowestboldy;
            finalratio = yrange/xrange;
            ratio[(int)elapsed/60] = finalratio;

            //DROPPED BOULDERS PERQUADRANT
            double nwcount = 0;
            double necount = 0;
            double swcount = 0;
            double secount = 0;
            for(SimObj b: bolds){
                if (b instanceof HardBoulder) {
                    if(b.body != null){
                        if(b.body.getPosition().x < broodcentrex[(int)elapsed/60] && b.body.getPosition().y >= broodcentrey[(int)elapsed/60]){
                            nwcount++;
                        }
                        if(b.body.getPosition().x >= broodcentrex[(int)elapsed/60] && b.body.getPosition().y >= broodcentrey[(int)elapsed/60]){
                            necount++;
                        }
                        if(b.body.getPosition().x < broodcentrex[(int)elapsed/60] && b.body.getPosition().y < broodcentrey[(int)elapsed/60]){
                            swcount++;
                        }
                        if(b.body.getPosition().x >= broodcentrex[(int)elapsed/60] && b.body.getPosition().y < broodcentrey[(int)elapsed/60]){
                            secount++;
                        }
                    }

                }
            }
            nwbolds[(int)elapsed/60] = nwcount;
            nebolds[(int)elapsed/60] = necount;
            swbolds[(int)elapsed/60] = swcount;
            sebolds[(int)elapsed/60] = secount;

            //POSITION CSV FILE GENERATE
            this.generateCsvFile("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\"+ timeStamp + "\\" + ((int)elapsed/60) + "s.csv");
        }

        debugRenderer.render(world, debugMatrix);
        elapsed++;

        //IF OUT OF TIME
        if((int)elapsed/60 == TOTAL_TIME){

            //passing data to the screenhandler to send to the results screen
            ScreenHandler.droplist = this.droplist;
            ScreenHandler.boldscentrex = this.boldscentrex;
            ScreenHandler.boldscentrey = this.boldscentrey;

            //setting the values from before a boulder centroid exists to the first real value to not skew the graph
            for(int xcounter = 0; xcounter < boldscentrex.length; xcounter++){
                if(boldscentrex[xcounter] != 0){
                    for(int xinnercount = 0; xinnercount < xcounter; xinnercount++){
                        boldscentrex[xinnercount] = boldscentrex[xcounter];
                    }
                    break;
                }
            }
            for(int ycounter = 0; ycounter < boldscentrey.length; ycounter++){
                if(boldscentrey[ycounter] != 0){
                    for(int yinnercount = 0; yinnercount < ycounter; yinnercount++){
                        boldscentrey[yinnercount] = boldscentrey[ycounter];
                    }
                    break;
                }
            }

            ScreenHandler.broodcentrex = this.broodcentrex;
            boldscentrex[0] = boldscentrex[1];
            boldscentrey[0] = boldscentrey[1];
            ScreenHandler.broodcentrey = this.broodcentrey;
            ScreenHandler.ratio = this.ratio;
            ScreenHandler.nebolds = this.nebolds;
            ScreenHandler.nwbolds = this.nwbolds;
            ScreenHandler.sebolds = this.sebolds;
            ScreenHandler.swbolds = this.swbolds;

            //write the recipe csv with all the constants
            PrintWriter writer = null;
            try {
                writer = new PrintWriter("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\"+ timeStamp + "\\info.txt", "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            writer.println("SEED");
            writer.println(ScreenHandler.SEED);
            writer.println();
            writer.println("#BUILDERS");
            writer.println(NUM_ANTS);
            writer.println();
            writer.println("#BROOD");
            writer.println(NUM_RANTS);
            writer.println();
            writer.println("#BOULDERS");
            writer.println(NUM_BOLDS);
            writer.println();
            writer.println("DROPPING TIME");
            writer.println(DROP_TIME/60);
            writer.println();
            writer.println("WIND DIRECTION");
            writer.println(currentdir.toString());
            writer.println();
            writer.println("WIND FORCE");
            if(windstr*30 == 0.2f){
                writer.println("LOW");
            }
            else if(windstr*30 == 0.5f){
                writer.println("MEDIUM");
            }
            else if(windstr*30 == 2f){
                writer.println("HIGH");
            }
            writer.println();
            writer.println("PUDDLE FREQ");
            if(PUD_FREQ == 0){
                writer.println("NO");
            }
            else if(PUD_FREQ == 15){
                writer.println("LOW");
            }
            else if(PUD_FREQ == 45){
                writer.println("MEDIUM");
            }
            else if(PUD_FREQ == 80){
                writer.println("HIGH");
            }
            writer.println();
            writer.println("MAX PUDDLE WIDTH");
            writer.println(PUD_WIDTH);
            writer.println();
            writer.println("EXCAVATION MODE");
            writer.println(excavate);
            writer.println();
            writer.println("ACTIVE BROOD");
            writer.println(broodgo);
            writer.println();
            writer.close();



            maintune.dispose();
            world.dispose();
            goresults = true;
        }
    }

    //method for writing positional csv, called every second
    private void generateCsvFile(String sFileName)
    {
        try
        {
            FileWriter writer = new FileWriter(sFileName);

            writer.append("Type");
            writer.append(',');
            writer.append("X");
            writer.append(',');
            writer.append("Y");
            writer.append('\n');

            //three columns, type of object, x and y
            //checking the object type by density
            Array<Fixture> fixs = new Array();
            world.getFixtures(fixs);
            for(Fixture f: fixs){
                if(f.getDensity() == BUILDER_DENSE){
                    //implement a holding check
                    for(Ant a: ants){
                        for(Fixture fix: a.body.getFixtureList()){
                            if(fix == f){
                                if(!a.holding){
                                    writer.append("1");
                                }
                                else{
                                    writer.append("1.5");
                                }
                            }
                        }
                    }
                }
                else if(f.getDensity() == BROOD_DENSE){
                    writer.append("2");
                }
                else if(f.getDensity() == BOL_DENSE){
                    writer.append("3");
                }
                else if(f.getDensity() == HARD_BOL_DENSE){
                    writer.append("4");
                }
                else if(f.getDensity() == PUD_DENSE){
                    writer.append("5");
                }
                else{
                    writer.append("0");
                }
                writer.append(',');
                writer.append(Float.toString(f.getBody().getPosition().x));
                writer.append(',');
                writer.append(Float.toString(f.getBody().getPosition().y));
                writer.append('\n');
            }


            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
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
        world.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {


        //change the views to only brood and the nest for testing
        if(keycode == Input.Keys.ESCAPE){
            drawSprite = !drawSprite;
        }

        //pause and play rendering with p
        if(keycode == Input.Keys.P) {
            if (Gdx.graphics.isContinuousRendering()) {
                Gdx.graphics.setContinuousRendering(false);
            } else {
                Gdx.graphics.setContinuousRendering(true);
                Gdx.graphics.requestRendering();
            }
        }

        return true;
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
}
