import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;
import gui.Cube;
import gui.Graphics;
import gui.Time;
import gui.Window;
import internal.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        
        List<Block> blocks = new ArrayList<Block>();

        // INITIALIZE BLOCKS, for testing purposes
        /*
        float radius = 20f;
        int nbOfBlocks = 10;
        Vector COLOR = new Vector(1.0f, 0.0f,0.0f);
        
        
        for (int i = 1; i < nbOfBlocks + 1; i++) {
        	Vector position = new Vector(0.0f, (float)(radius - radius*Math.cos((2*Math.PI/(float)nbOfBlocks)*i)), -(float)(radius*Math.sin((2*Math.PI/(float)nbOfBlocks)*i)));
        	Block block = new Block(position);
        	Cube cube = new Cube(position.convertToVector3f(), COLOR.convertToVector3f());
        	block.setAssocatedCube(cube);
        	blocks.add(block);
        }*/
        
        Vector COLOR = new Vector(1.0f, 0.0f,0.0f);
        BlockCoordinatesParser parser = new BlockCoordinatesParser(COORDINATES_FILEPATH);
        for (Vector position : parser.getCoordinates()) {
        	Block block = new Block(position);
        	Cube cube = new Cube(position.convertToVector3f(), COLOR.convertToVector3f());
        	block.setAssocatedCube(cube);
        	blocks.add(block);
        }
        
        // END for testing purposes
        
        world.addWorldObject(blocks.get(0));
        world.addWorldObject(blocks.get(1));
        
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
            	
            	// For testing purposes: add block after block, ensuring only two blocks exist at one time
            	if (blocks.size() >= 1) {
            		goalNotReached = true;
            	}
            	
            	if(blocks.size() > 2){
            		world.addWorldObject(blocks.get(2));
            	}

            	world.removeWorldObject(blocks.get(0));
            	blocks.remove(0);
            	
                
            	
                
            }
//            long endTime = System.currentTimeMillis();
//            long timeDiff = endTime - startTime;
//            long timeLeft = FRAME_MILLIS - timeDiff;
            long timeLeft = (long) (FRAME_MILLIS - Time.timeSinceLastUpdate());
            if(timeLeft>0)
                Thread.sleep(timeLeft);
            //System.out.println(timeLeft);
            //4autopilot
//            elapsedTime += TIME_STEP*STEPS_PER_ITERATION;


        }
    }

	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);
	private final static long FRAME_MILLIS = 50;
	private final static String COORDINATES_FILEPATH = "src/internal/blockCoordinates.txt";

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
