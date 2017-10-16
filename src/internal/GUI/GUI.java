package internal.GUI;

import static org.lwjgl.glfw.GLFW.*;

public class GUI {

    private int front = 0;
    private int right = 0;
    private int up = 0;
    private int pitch = 0;
    private int jaw = 0;
    private int roll = 0;
    
    private Mesh mesh;

    private final Renderer renderer;

    public GUI() {
        renderer = new Renderer();
    }

    public void init(Window window, float[] positions, float[] colours, int[] indices) throws Exception {
        renderer.init(window);
        // Create the Mesh
        mesh = new Mesh(positions, colours, indices);
    }

    public void input(Window window) {
        front = 0;
        right = 0;
        up = 0;
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            front = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            front = -1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            right = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            right = 1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            up = 1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_ALT)) {
            up = -1;
        }
    }

    public void update(float interval) {
    	
    }

    public void render(Window window) {
        renderer.render(window, mesh);
    }

    public void cleanup() {
        renderer.cleanup();
        mesh.cleanUp();
    }

}
