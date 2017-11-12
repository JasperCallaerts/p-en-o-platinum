import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;
import gui.Cube;
import gui.Graphics;
import gui.Time;
import gui.Window;
import internal.*;

import java.io.IOException;

public class Main {

	/**
	 * Main method for the simulation
	 * @param args
	 * @author Martijn Sauwens
	 */
	public static void main(String[] args) throws IOException, InterruptedException {

		// initialize graphics capabilities
		Graphics graphics = new Graphics();
		
		// Cube needs graphics to be able to initialize cubes
		Cube.setGraphics(graphics);

		// initialize the windows
		Window droneCam = new Window(1000, 1000, 0.5f, 0.4f, "bytestream window", false);
		Window droneView = new Window(960, 1000, 0.0f, 0.4f, "Drone simulator 2017", true);
		Window personView = new Window(960, 1000, 1f, 0.4f, "Drone simulator 2017", true);
//		Window textWindow = new Window(500, 500, 0.5f, 0.5f, "text window", true, droneCam); // Not implemented yet

		// add the windows to graphics
		graphics.addWindow("camera", droneCam);
		graphics.addWindow("third person view", personView);
		graphics.addWindow("drone view", droneView);
//		graphics.addWindow("textWindow", textWindow); // Not implemented yet
		
		
        // drone builder covers all the stuff involving building the drone, adjust parameters there		
        WorldBuilder worldBuilder = new WorldBuilder();
        Drone drone = worldBuilder.DRONE;
        AutoPilot autopilot = new AutoPilot();
        World world = worldBuilder.createWorld();

        // initialize second, third, fourth and fifth block, for testing purposes
        Vector BLOCKPOS = new Vector(2.0f, 6.0f, -40.0f);
    	Vector BLOCKPOS2 = new Vector(4.0f, 10.0f, -60.0f);
    	Vector BLOCKPOS3 = new Vector(5.0f, 8.0f, -80.0f);
    	Vector BLOCKPOS4 = new Vector(0.0f, 0.0f, -100.0f);
        Vector COLOR = new Vector(1.0f, 0.0f,0.0f);
    	
    	Block block1 = new Block(BLOCKPOS);
        Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        block1.setAssocatedCube(cube1);
        
        Block block2 = new Block(BLOCKPOS2);
        Cube cube2 = new Cube(BLOCKPOS2.convertToVector3f(), COLOR.convertToVector3f());
        block2.setAssocatedCube(cube2);
        
        Block block3 = new Block(BLOCKPOS3);
        Cube cube3 = new Cube(BLOCKPOS3.convertToVector3f(), COLOR.convertToVector3f());
        block3.setAssocatedCube(cube3);
        
        Block block4 = new Block(BLOCKPOS4);
        Cube cube4 = new Cube(BLOCKPOS4.convertToVector3f(), COLOR.convertToVector3f());
        block4.setAssocatedCube(cube4);
        
        Block block0 = world.getRandomBlock();
        world.addWorldObject(block1);
        // END for testing purposes
        
        // Put a world in the windows
        droneCam.initWorld(world, true);
        personView.initWorld(world, false);
        droneView.initWorld(world, true);
      
        // set state
        boolean goalNotReached = true;
        boolean configuredAutopilot = false;

//        float elapsedTime = 0.0f;

        // initialize time handler
     	Time.initTime();
     		
        while (!graphics.isTerminated()) {

//            long startTime = System.currentTimeMillis();
        	
        	// update time
        	Time.update();
            
            // render the windows and terminate graphics if all windows are closed
            graphics.renderWindows();
            
            // exit the loop early if graphics is terminated
            if (graphics.isTerminated())
        		break;
            
            //System.out.println(drone.getRoll()*180/Math.PI);
            AutopilotOutputs autopilotOutputs;

            if (goalNotReached && !droneCam.isTerminated()) {

                //pass the outputs to the drone
                try {

                    byte[] cameraImage = droneCam.getCameraView();
                    MainAutopilotInputs autopilotInputs =  new MainAutopilotInputs(drone, cameraImage, (float) Time.getTimePassed()); // elapsedTime vervangen door getTimePassed()
                    if(!configuredAutopilot){
                         autopilotOutputs= autopilot.simulationStarted(new DroneBuilder(true).createConfig(), autopilotInputs);
                        configuredAutopilot = true;
                    }else{
                        autopilotOutputs = autopilot.timePassed(autopilotInputs);
                    }
                    drone.setAutopilotOutputs(autopilotOutputs);

                    world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);

                } catch (SimulationEndedException e) {
                    autopilot.simulationEnded();
                    goalNotReached = false;
                } catch (IOException e) {
                    System.out.println("IO exception");
                }

            }else {
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
            	
                
            }
//            long endTime = System.currentTimeMillis();
//            long timeDiff = endTime - startTime;
//            long timeLeft = FRAME_MILLIS - timeDiff;
            long timeLeft = (long) (FRAME_MILLIS - Time.timeSinceLastUpdate());
            if(timeLeft>0)
                Thread.sleep(timeLeft);
            System.out.println(timeLeft);
            //4autopilot
//            elapsedTime += TIME_STEP*STEPS_PER_ITERATION;


        }
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

}
