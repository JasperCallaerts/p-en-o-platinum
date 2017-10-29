import gui.Window;
import internal.*;

import java.io.IOException;

public class Main {

	/**
	 * Main method for the simulation
	 * @param args
	 * @author Martijn Sauwens
	 */
	public static void main(String[] args) {
		
		// drone builder covers all the stuff involving building the drone, adjust parameters there
		World  world = new WorldBuilder().createWorld();
		
		// initialize a window
		Window window = new Window(world);

		boolean goalNotReached = true;
		while (goalNotReached) {
			
			//first render the image
			window.renderFrame();
			//pass the outputs to the drone
			byte[] camera = Window.getCameraView();
			WorldBuilder.DRONE.setAPImage(camera);
			try {
				world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);
			} catch (SimulationEndedException e) {
				goalNotReached = false;
			} catch (IOException e) {
				//ignore
			}

		}
		// close the window
		window.terminate();
	}
	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)*TIME_STEP);


}
