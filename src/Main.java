import gui.Renderer;
import gui.Graphics;
import gui.Time;
import internal.*;
import sun.awt.windows.WBufferStrategy;

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
        World world = worldBuilder.createWorld();


        // initialize the renderers

        Renderer renderer = new Renderer(world);

//        Renderer andererenderer = new Renderer(world);

        boolean goalNotReached = true;

        // for testing purposes
        float passed_time = 0;
        // END for testing purposes
        int steps = 0;

        while (true) {

            //first render the images
        	graphics.renderWindows(renderer);
        	
//            long startRenderTime = System.nanoTime();

              System.out.println(worldBuilder.DRONE.getRoll()*180/Math.PI);
//            long endRenderTime = System.nanoTime();
//            double renderTime = (endRenderTime-startRenderTime)*1E-9;
//            System.out.println("render time: " + renderTime);

            if (goalNotReached) {
                //pass the outputs to the drone


                try {
                    byte[] camera = graphics.getWindow("droneWindow").getCameraView();
                    worldBuilder.DRONE.setAPImage(camera);

                    passed_time = passed_time + TIME_STEP;

                    world.advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);

                } catch (SimulationEndedException e) {
                    goalNotReached = false;
                } catch (IOException e) {
                    System.out.println("IO exception");
                }

            }
            //4debugging
            steps += 1;

        }
    }

	// configuration for 20 fps
	private final static float TIME_STEP = 0.001f;
	private final static float FRAMERATE = 20.0f;
	private final static int STEPS_PER_ITERATION = Math.round((1/ FRAMERATE)/TIME_STEP);


}
