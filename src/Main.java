import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import gui.Cube;
import internal.*;
import gui.Window;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		// drone builder covers all the stuff involving building the drone, adjust parameters there
		DroneBuilder builder = new DroneBuilder(true);
		Drone drone = builder.createDrone();
		World world = new World();
		//Todo sync the block with the cube and put them also in the world
		//Cube cube = new Cube();
		//Block cube = new Block();
		world.addWorldObject(drone);

		boolean goalNotReached = true;
		while (goalNotReached) {

			//first render the image
			//Todo implement the render such that it only renders one image if this method is called
			new Window().run();
			//pass the outputs to the drone
			byte[] camera = Window.getCameraView();
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

	}
	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static int STEPS_PER_ITERATION = Math.round(1/20f*TIME_STEP);
}





