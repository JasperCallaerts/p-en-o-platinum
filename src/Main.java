import gui.Window;
import internal.Drone;
import internal.DroneBuilder;
import internal.SimulationEndedException;
import internal.World;

import java.io.IOException;

public class Main {

	/**
	 * Main method for the simulation
	 * @param args
	 * @author Martijn Sauwens
	 */
	public static void main(String[] args) {
		// initialize a window
		Window window = new Window();
		
		// drone builder covers all the stuff involving building the drone, adjust parameters there
		DroneBuilder builder = new DroneBuilder(true);
		Drone drone = builder.createDrone();
		//initialize a new world
		World world = new World();
		//Todo sync the block with the cube and put them also in the world
		//Cube cube = new Cube();
		//Block cube = new Block();
		world.addWorldObject(drone);

		boolean goalNotReached = true;
		while (goalNotReached) {
			
			//first render the image
			new Window().renderFrame();
			//pass the outputs to the drone
			byte[] camera = Window.getCameraView();
			drone.setAPImage(camera);
			//Todo update the camera input of the drone!
			//drone.getAutopilot().update();
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
