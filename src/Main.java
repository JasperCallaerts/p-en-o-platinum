import Autopilot.Autopilot;
import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;
import gui.Renderer;
import gui.Graphics;
import gui.Time;
import internal.*;


import java.io.IOException;

public class Main {

	/**
	 * Main method for the simulation
	 * @param args
	 * @author Martijn Sauwens
	 */
	public static void main(String[] args) throws IOException {

		Graphics graphics = new Graphics();
		Time.initTime();

		// initialize the windows
		//        graphics.addWindow("window2", 920, 1000, 1f, 0.4f, "secondary window", false);
		graphics.addWindow("droneWindow", 1000, 1000, 0.0f, 0.4f, "Drone simulator 2017", true);

        // drone builder covers all the stuff involving building the drone, adjust parameters there
        WorldBuilder worldBuilder = new WorldBuilder();
        Drone drone = worldBuilder.DRONE;
        AutoPilot autopilot = new AutoPilot();
        World world = worldBuilder.createWorld();



        // initialize the renderers

        Renderer renderer = new Renderer(world);

//        Renderer andererenderer = new Renderer(world);

        boolean goalNotReached = true;
        boolean configuredAutopilot = false;

        // END for testing purposes
        float elapsedTime = 0.0f;

        while (true) {

            //first render the images

              graphics.renderWindows(renderer);
              //System.out.println(drone.getRoll()*180/Math.PI);
              AutopilotOutputs autopilotOutputs;

            if (goalNotReached) {
                //pass the outputs to the drone
                try {

                    byte[] cameraImage = droneWindow.getCameraView();
                    MainAutopilotInputs autopilotInputs =  new MainAutopilotInputs(drone, cameraImage, elapsedTime);
                    if(!configuredAutopilot){
                         autopilotOutputs= autopilot.simulationStarted(new DroneBuilder(true).createConfig(), autopilotInputs);
                        configuredAutopilot = false;
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

            }
            //4autopilot
            elapsedTime += TIME_STEP*STEPS_PER_ITERATION;

        }
    }

	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);

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
