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
		
		
		// initialize a window
		Window droneWindow = new Window(1000, 1000, 0.2f, 0.5f, "Drone simulator 2017");
//		Window testWindow = new Window(500, 500, 0.9f, 0.1f, "test window");
		
		// initialize a renderer
		Renderer renderer = new Renderer(droneWindow.getHandler(), world);
		
		boolean goalNotReached = true;
		while (goalNotReached) {
			
			//first render the image
			droneWindow.renderFrame(renderer);
			//pass the outputs to the drone
			byte[] camera = droneWindow.getCameraView();
			worldBuilder.DRONE.setAPImage(camera);

			try {
				world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);
			} catch (SimulationEndedException e) {
				goalNotReached = false;
			} catch (IOException e) {
				System.out.println("IO exception");
			}

		}
		while (true) {
			droneWindow.renderFrame(renderer);
		}
	}
	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);


}
