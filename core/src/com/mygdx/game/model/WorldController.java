package com.mygdx.game.model;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.mygdx.game.*;
import com.mygdx.game.Entities.Dude;
import com.mygdx.game.maps.MainTileMap;
import com.mygdx.game.screens.gui.Display;
import com.mygdx.game.util.Constants;
import com.mygdx.game.util.MapBodyManager;


/**
 * Created by Ian on 12/21/2014.
 *
 * This class is designed to hold the logic of the game elements and provide access
 * to the other modules as well as control input
 *
 */
public class WorldController implements InputProcessor {

    private static final String TAG = WorldController.class.getName();


    public int score;
    public Level level;
    public int lives;


    //not bein used
    public MainTileMap mainMap;
    public World world;
    public MapBodyManager bodyManager;

    /*
    * Creates the camera helper, a utililty class for camera manipulation
    * */
    public CameraHelper cameraHelper;

    /*
    * The class for displaying the UI
    * Is placed in controller so input has access
    * */
    public Display display;

    /*
    * Actor initialization
    * Dude is only active sprite 3/4/2015
    * */
    public Dude dude;

    public Sprite[] spriteGroup;
    public int selectedSprite;

    /*
    * Environment intitialization
    * Not being used
    * */
    public Sprite[] groundGroup;


    /*
    * Default constructor
    * */
    public WorldController () {
        init();
    }


    /*
    * Build class
    * */
    private void init() {


        Gdx.input.setInputProcessor(this);
        //world = new World(new Vector2(0,0),true);
        /*
        * Body manager to be used for static collisions
        * second argument being 16 is because the game uses a unit/tile scale of 1/16f
        * */
        bodyManager = new MapBodyManager(GameInstance.getInstance().world,16, null, Application.LOG_DEBUG);


        cameraHelper = new CameraHelper();
        cameraHelper.setPosition(Constants.GAME_WORLD / 2, Constants.GAME_WORLD/2);

        //Game info is set in Constants class
        lives = Constants.LIVES_START;

        /*Initiate everything*/
        initActors();
        initUI();
        bodyManager.createPhysics(Assets.instance.mainMap.map, "Obstacles");
    }

    /*
    * Initiate Tiled Map
    * */
    public void initMap(){
        mainMap = new MainTileMap();
    }


    public void initUI(){
        display = new Display();
    }

    /*
    * Initiate level loader
    * TODO change to load tiled maps

    private void initLevel () {
        score = 0;
        level = new Level(Constants.LEVEL_01);
    }

    * */


    /*
    * Create actors, for now specifically the dude actor
    * */
    private void initActors(){
        dude = new Dude(0);
        dude.setRegion(Assets.instance.dudeAsset.body);
        dude.getBody().setTransform(dude.position.x + dude.getWidth(), dude.position.y + dude.getHeight(), 0);
    }


     /*
    * Create model and needed resources
    * This is the earliest attemp at game stuff
    * Uncomment code in other classes to see what it does
    * */
    private void initTestObjects(){

        //array to hold actors
        spriteGroup = new Sprite[5];

        //array to hold environment
        groundGroup = new Sprite[20];

        //add textures
        ArrayMap<String, TextureRegion> regions = new ArrayMap<String, TextureRegion>();
        regions.put("face", Assets.instance.dudeAsset.body);
        regions.put("ground", Assets.instance.ground.ground);


        /*
        * Go through the ground objects
        * */
        for(int i = 0; i < groundGroup.length; i++){
            Sprite spr = new Sprite(regions.get("ground"));

            int x = 0;
            int y = 0;

            spr.setSize(1, 1);
            spr.setScale(1, 1);

            if(i < 5){
                y = 0;
            }

            if(i < 9){
                y = 1;
            }

            spr.setPosition(i, y);
            groundGroup[i] = spr;

        }


        /*
        * Go through sprites and make everything which is needed
        * */
        for (int i = 0; i < spriteGroup.length; i++) {
            Sprite spr = new Sprite(regions.get("face"));

            // Define sprite size to be 1m x 1m in game world
            //TODO learn more about using m as unit of space in game world
            spr.setSize(1, 1);

            spr.setScale(1, 1);

            // Set origin to sprite's center
            spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);

            // Calculate random position for sprite
            float randomX = MathUtils.random(-2.0f, 2.0f);
            float randomY = MathUtils.random(-2.0f, 2.0f);
            spr.setPosition(randomX, randomY);

            // Put new sprite into array
            spriteGroup[i] = spr;
        }



        // Set first sprite as selected one
        selectedSprite = 0;
     }






    /*
    * This method is called constantly and updates the model and view
    *
    * Important note about handleDebugInput()
    * this class is called with a helper class to ensure all
    * input has been processed before rendering this class needs to be
    * called first to ensure user input is handled BEFORE logic is executed
    * ORDER IS IMPORTANT
    * */
    public void update (float deltaTime) {

        //updateTestObjects(deltaTime);


        // Create an array to be filled with the bodies
// (better don't create a new one every time though)
        Array<Body> bodies = new Array<Body>();
// Now fill the array with all bodies
        GameInstance.getInstance().world.getBodies(bodies);

        for (Body b : bodies) {
            // Get the body's user data - in this example, our user
            // data is an instance of the Entity class
            //Entity e = (Entity) b.getUserData();

            if (dude != null) {
                // Update the entities/sprites position and angle
                dude.setPosition(b.getPosition().x, b.getPosition().y);
                // We need to convert our angle from radians to degrees
                //dude.setRotation(MathUtils.radiansToDegrees * b.getAngle());
            }
        }

        handleDebugInput(deltaTime);
        //dude.getBody().setTransform(dude.position.x + dude.getWidth(), dude.position.y + dude.getHeight(), 0);
        cameraHelper.update(deltaTime);
        //Gdx.app.debug(TAG, dude.getVelocity().toString());
        display.setText(dude.getVelocity().toString());
        display.update();
        dude.update(deltaTime);
        GameInstance.getInstance().world.step(1/45f, 2, 6);
    }


    /*
    * Updates the local model from earlier code
    * */
    private void updateTestObjects(float deltaTime) {
        // Get current rotation from selected sprite
        //float rotation = spriteGroup[selectedSprite].getRotation();
        // Rotate sprite by 90 degrees per second
        //rotation += 90 * deltaTime;
        // Wrap around at 360 degrees
        // TODO ???
        //rotation %= 360;
        // Set new rotation value to selected sprite
        //spriteGroup[selectedSprite].setRotation(rotation);


    }



    /*
    * Enables all kinds of awesome things that we can control when
    * used in conjunction with the CameraHelper class
    * */
    private void handleDebugInput (float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        float inputForce = 20;

        float sprMoveSpeed = 10 * deltaTime;
        if(Gdx.input.isKeyPressed(Keys.W)) dude.getBody().applyForceToCenter(0, inputForce, true);
        if(Gdx.input.isKeyPressed(Keys.A)) dude.getBody().applyForceToCenter(-inputForce, 0, true);
        if(Gdx.input.isKeyPressed(Keys.S)) dude.getBody().applyForceToCenter(0, -inputForce, true);
        if(Gdx.input.isKeyPressed(Keys.D)) dude.getBody().applyForceToCenter(inputForce, 0, true);


        // Camera Controls (move)
        float camMoveSpeed = 5 * deltaTime;
        float camMoveSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) camMoveSpeed *=
                camMoveSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveCamera(-camMoveSpeed,
                0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveCamera(camMoveSpeed,
                0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) moveCamera(0, camMoveSpeed);

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveCamera(0,
                -camMoveSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE))
            cameraHelper.setPosition(0, 0);


        // Camera Controls (zoom)
        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *=
                camZoomSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Keys.COMMA))
            cameraHelper.addZoom(camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(
                -camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
    }



    /*
    * Uses CameraHelper to control the camera position
    * */
    public void moveCamera (float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;



        cameraHelper.setPosition(x, y);
    }


    /*
    * Method to actually move a game object
    * TODO fold this and other sprite methods to a new class
    * */
    private void moveSelectedSprite (float x, float y) {
        spriteGroup[selectedSprite].translate(x, y);
    }


    /*
    * Inputs
    * */

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        // Reset game world
        if (keycode == Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        }

        //change text
        if (keycode == Keys.B){
            display.setText(dude.randomText());

        }


        // Select next sprite
        else if (keycode == Keys.SPACE) {
            selectedSprite = (selectedSprite + 1) % spriteGroup.length;

            // Update camera's target to follow the currently
            // selected sprite
            if (cameraHelper.hasTarget()) {
                cameraHelper.setTarget(spriteGroup[selectedSprite]);
            }

            Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " selected");
            }
            // Toggle camera follow
            else if (keycode == Keys.ENTER) {
                //cameraHelper.setTargetAbstract(cameraHelper.hasTarget() ? null : dude);
            cameraHelper.setTargetAbstract(dude);
                Gdx.app.debug(TAG, "Camera follow enabled: " +
                        cameraHelper.hasTargetAbstract());
            }

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
