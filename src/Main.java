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
		
		// initialize a window
		Window window = new Window(world);

		boolean goalNotReached = true;
		while (goalNotReached) {
			
			//first render the image
			window.renderFrame();
			//pass the outputs to the drone

			try {
				byte[] camera = Window.getCameraView();
				worldBuilder.DRONE.setAPImage(camera);
				world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);
			} catch (SimulationEndedException e) {
				goalNotReached = false;
			} catch (IOException e) {
				System.out.println("IO exception");
			}

		}
	}
	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);


}
