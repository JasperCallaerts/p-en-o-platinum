import Autopilot.AutopilotConfig;
import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;
import gui.Cube;
import gui.Graphics;
import gui.Time;
import gui.Window;
import internal.*;
import math.Vector3f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Martijn on 13/11/2017.
 * supervised by Bart
 */
public class TestbedMain {

    //Todo add coments
    public TestbedMain(){
        //todo introduce getters and setters for instance variables
        // initialize graphics capabilities
        graphics = new Graphics();

        // Cube needs graphics to be able to initialize cubes
        Cube.setGraphics(graphics);

        // Construct the windows
        droneCam = new Window(200, 200, 0.5f, 0.4f, "bytestream window", new Vector3f(1.0f, 1.0f, 1.0f), false);
        droneView = new Window(960, 1000, 0.0f, 0.4f, "Drone simulator 2017", new Vector3f(1.0f, 1.0f, 1.0f), true);
        personView = new Window(960, 1000, 1f, 0.4f, "Drone simulator 2017", new Vector3f(0.5f, 0.8f, 1.0f), true);

        // add the windows to graphics
        graphics.addWindow("camera", droneCam);
        graphics.addWindow("third person view", personView);
        graphics.addWindow("drone view", droneView);
//		graphics.addWindow("textWindow", textWindow); // Not implemented yet



        // drone builder covers all the stuff involving building the drone, adjust parameters there
        WorldBuilder worldBuilder = new WorldBuilder();
        drone = worldBuilder.DRONE;
        world = worldBuilder.createWorld();

        // initialize second, third, fourth and fifth block, for testing purposes
        Vector BLOCKPOS = new Vector(2.0f, 6.0f, -40.0f);
        Vector BLOCKPOS2 = new Vector(4.0f, 10.0f, -60.0f);
        Vector BLOCKPOS3 = new Vector(5.0f, 8.0f, -80.0f);
        Vector BLOCKPOS4 = new Vector(0.0f, 0.0f, -100.0f);
        Vector COLOR = new Vector(1.0f, 0.0f,0.0f);

        block1 = new Block(BLOCKPOS);
        Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        block1.setAssocatedCube(cube1);

        block2 = new Block(BLOCKPOS2);
        Cube cube2 = new Cube(BLOCKPOS2.convertToVector3f(), COLOR.convertToVector3f());
        block2.setAssocatedCube(cube2);

        block3 = new Block(BLOCKPOS3);
        Cube cube3 = new Cube(BLOCKPOS3.convertToVector3f(), COLOR.convertToVector3f());
        block3.setAssocatedCube(cube3);

        block4 = new Block(BLOCKPOS4);
        Cube cube4 = new Cube(BLOCKPOS4.convertToVector3f(), COLOR.convertToVector3f());
        block4.setAssocatedCube(cube4);

        block0 = world.getRandomBlock();
        world.addWorldObject(block1);
        // END for testing purposes

        // Initialize the windows
        droneCam.initWorldWindow(world, true);
        personView.initWorldWindow(world, false);
        droneView.initWorldWindow(world, true);
//      textWindow.initTextWindow(droneCam);


        Time.initTime();

    }


    public AutopilotInputs testbedStep(AutopilotOutputs autopilotOutputs) throws InterruptedException, IOException {
        AutopilotInputs autopilotInputs = null;
        // update time
        Time.update();

        // warning: assure every iteration terminates with a non null autopilotInputs, the
        // writer and handler can't handle it
        if (goalNotReached && !droneCam.isTerminated()) {
            System.out.println("entered the next state if");
            //pass the outputs to the drone
            byte[] oldImage = droneCam.getCameraView();
            try {
                // elapsedTime replace by getTimePassed()
                if(!firstRun) {
                    drone.setAutopilotOutputs(autopilotOutputs);
                    world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);
                }else{
                    firstRun = false;
                    System.out.println("first run");
                }
                // render the windows and terminate graphics if all windows are closed
                graphics.renderWindows();
                byte[] cameraImage = droneCam.getCameraView();
//              System.out.println(FRAME_MILLIS - Time.timeSinceLastUpdate());
                autopilotInputs = new MainAutopilotInputs(drone, cameraImage, (float) Time.getTimePassed());

            } catch (SimulationEndedException e) {
                goalNotReached = false;

                System.out.println("removing blocks");
                // Entire else clause is for testing purposes only
                if (world.hasWorldObject(block0)) {
                    world.removeBlocks();
                    world.addWorldObject(block1);
                    world.addWorldObject(block2);
                    goalNotReached = true;
                }else if (world.hasWorldObject(block1)) {
                    world.removeBlocks();
                    world.addWorldObject(block2);
                    world.addWorldObject(block3);
                    goalNotReached = true;
                }else if (world.hasWorldObject(block2)) {
                    world.removeBlocks();
                    world.addWorldObject(block3);
                    world.addWorldObject(block4);
                    goalNotReached = true;
                }else if (world.hasWorldObject(block3)) {
                    world.removeBlocks();
                    world.addWorldObject(block4);
                    goalNotReached = true;
                }
                autopilotInputs = new MainAutopilotInputs(drone, oldImage, (float) Time.getTimePassed());
            } catch (IOException e) {
                System.out.println("IO exception");
            }
        }

        long timeLeft = (long) (FRAME_MILLIS - Time.timeSinceLastUpdate());
        if(timeLeft>0)
            Thread.sleep(timeLeft);
        System.out.println(timeLeft);

        return autopilotInputs;
    }

    public AutopilotConfig getConfig(){
        return drone.getAutopilotConfig();
    }

    public boolean isFirstRun() {
        return firstRun;
    }


    // configuration for 20 fps
    private final static float TIME_STEP = 0.001f;
    private final static float FRAMERATE = 20.0f;
    private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);
    private final static long FRAME_MILLIS = 50;

    // Todo documentatie toevoegen (als je een katje bent)
    private static class MainAutopilotInputs implements AutopilotInputs {

        public MainAutopilotInputs(Drone drone, byte[] cameraImage, float elapsedTime) {
            this.drone = drone;
            this.cameraImage = cameraImage;
            this.elapsedTime = elapsedTime;
        }

        @Override
        public byte[] getImage() {
            return cameraImage;
        }

        @Override
        public float getX() {
            return drone.getPosition().getxValue();
        }

        @Override
        public float getY() {
            return drone.getPosition().getyValue();
        }

        @Override
        public float getZ() {
            return drone.getPosition().getzValue();
        }

        @Override
        public float getHeading() {
            return drone.getHeading();
        }

        @Override
        public float getPitch() {
            return drone.getPitch();
        }

        @Override
        public float getRoll() {
            return drone.getRoll();
        }

        @Override
        public float getElapsedTime() {
            return elapsedTime;
        }

        private Drone drone;

        private byte[] cameraImage;

        private float elapsedTime;
    }



    private World world;
    private Graphics graphics;
    private Drone drone;

    private Block block0;
    private Block block1;
    private Block block2;
    private Block block3;
    private Block block4;

    private Window droneCam;
    private Window droneView;
    private Window personView;
//	Window textWindow = new Window(500, 500, 0.5f, 0.5f, "text window", new Vector3f(0.0f, 0.0f, 0.0f), true, droneCam); // Not implemented yet



    /*
    flags
     */
    private boolean goalNotReached = true;
    private boolean firstRun = true;

}
