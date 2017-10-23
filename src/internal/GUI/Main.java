package internal.GUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import internal.AutoPilot;
import internal.AutoPilotCamera;
import internal.Block;
import internal.Drone;
import internal.HorizontalWing;
import internal.Vector;
import internal.VerticalWing;
import internal.World;

public class Main {
	static String windowTitle = "Beste PenO Team";
	static int width = 1280;
	static int height = 720;
	static GUI gui;
	static Window window;
	
	
	static float[] vertices = new float[]{
            // VO
            -0.5f,  0.5f,  0.5f,
            // V1
            -0.5f, -0.5f,  0.5f,
            // V2
             0.5f, -0.5f,  0.5f,
            // V3
             0.5f,  0.5f,  0.5f,
            // V4
            -0.5f,  0.5f, -0.5f,
            // V5
             0.5f,  0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
             0.5f, -0.5f, -0.5f,
	};
	static float[] colours = new float[]{
			0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
	};
	static int[] indices = new int[]{
			// Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            4, 0, 3, 5, 4, 3,
            // Right face
            3, 2, 7, 5, 3, 7,
            // Left face
            6, 1, 0, 6, 0, 4,
            // Bottom face
            2, 1, 6, 2, 6, 7,
            // Back face
            7, 6, 4, 7, 4, 5,
	};
       
    

       public static void main(String[] args) {
		gui = new GUI();
		window = new Window(windowTitle, width, height);
	    try {
	        init();
	        loop();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        cleanup();
	    }
	}
	
	/**
	 * @author Anthony Rathé & ...
	 * @throws Exception
	 */
    protected static void init() throws Exception {
        window.init();
        gui.init(window, vertices, colours, indices);
        
        // Create World
    	World world = new World();
    	// Create world objects
    	Block redBlock = new Block(0f, 0f, 0f, 0f, 0f, 0f);
    	HorizontalWing leftWing = new HorizontalWing(new Vector(0f, 0f, 0f), 1f, 1f, 1f, 1f);
    	HorizontalWing rightWing = new HorizontalWing(new Vector(0f, 0f, 0f), 1f, 1f, 1f, 1f);
    	HorizontalWing horizontalStab = new HorizontalWing(new Vector(0f, 0f, 0f), 1f, 1f, 1f, 1f);
    	VerticalWing verticalStab = new VerticalWing(new Vector(0f, 0f, 0f), 1f, 1f, 1f, 1f);
    	AutoPilot AP = new AutoPilot();
    	AP.setAPCamera(new AutoPilotCamera());
    	Drone drone = new Drone(
    			1f, 
    			1f, 
    			20f, 
    			new Vector(0f,0f,0f), 
    			new Vector(0f,0f,0f), 
    			new Vector(0f,0f,0f), 
    			new Vector(0f,0f,0f), 
    			rightWing, 
    			leftWing, 
    			horizontalStab, 
    			verticalStab, 
    			AP);
    	// Add world objects
    	world.addWorldObject(redBlock);
    	world.addWorldObject(drone);
    	
    	// Create vertices array
    	float[] vertices = new float[world.getBlockSet().size()];
    	int i = 0;
    	for(Block block : world.getBlockSet()){
    		for (Float coord : Block.getVertices()){
    			vertices[i++] = coord;
    		}
    	}
    	
    }

    protected static void loop() throws Exception {
        boolean running = true;
        while (running && !window.windowShouldClose()) {
            input();
            update(0.0f);
            render();
        }
    }
    
    protected static void input() {
        gui.input(window);
    }

    protected static void update(float interval) {
        gui.update(interval);
    }

    protected static void render() {
        gui.render(window);
        window.update();
    }
    
    protected static void cleanup() {
    	gui.cleanup();
    }
}
