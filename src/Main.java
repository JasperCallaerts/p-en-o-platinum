import gui.Renderer;
import gui.Window;
import internal.*;
import sun.awt.windows.WBufferStrategy;

import java.io.IOException;

public class Main {

	/**
	 * Main method for the simulation
	 * @param args
	 * @author Martijn Sauwens
	 */
	public static void main(String[] args) {

		// drone builder covers all the stuff involving building the drone, adjust parameters there
		WorldBuilder worldBuilder = new WorldBuilder();
		World  world = worldBuilder.createWorld();


		// initialize the windows
		Window testWindow = new Window(920, 1000, 1f, 0.4f, "test window");
		Window droneWindow = new Window(1000, 1000, 0.0f, 0.4f, "Drone simulator 2017");


		// initialize the renderers
		Renderer renderer = new Renderer(droneWindow.getHandler(), world);
		Renderer testRenderer = new Renderer(testWindow.getHandler(), world);

		boolean goalNotReached = true;

		//---------------------------
		// for testing purposes
		float passed_time = 0;
		// END for testing purposes
		//---------------------------

		while (true) {

			//first render the images
			droneWindow.renderFrame(renderer);
			testWindow.renderFrame(testRenderer);

			if (goalNotReached) {
				//pass the outputs to the drone
				byte[] camera = droneWindow.getCameraView();
				worldBuilder.DRONE.setAPImage(camera);

				try {
					
					//------------------------
					// For testing purposes
					if (passed_time == 0) {
						world.getDrone().counterClockRollStart();
						//world.getDrone().increaseThrust(1.2);
					}else if (passed_time >= 0.001){
						world.getDrone().stopRoll();
					}
					System.out.println(world.getDrone().getLeftWingInclination() + " : " + world.getDrone().getRightWingInclination());
					passed_time = passed_time + TIME_STEP;
					
					// END for testing purposes
					//-------------------------
					world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);

				} catch (SimulationEndedException e) {
					goalNotReached = false;
				} catch (IOException e) {
					System.out.println("IO exception");
				}
			}
		}
	}
	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);


}
