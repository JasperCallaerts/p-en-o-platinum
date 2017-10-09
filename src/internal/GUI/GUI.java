package internal.GUI;

import internal.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class GUI {

    private int displxInc = 0;

    private int displyInc = 0;

    private int displzInc = 0;

    private int scaleInc = 0;

    private final Renderer renderer;

    private Object[] objects;

    public GUI() {
        renderer = new Renderer();
    }

    public void init(Window window, float[] positions, float[] colours, int[] indices) throws Exception {
        renderer.init(window);
        // Create the Mesh
        Mesh mesh = new Mesh(positions, colours, indices);
        Object object = new Object(mesh);
        objects = new Object[] { object };
    }

    public void input(Window window) {
        displyInc = 0;
        displxInc = 0;
        displzInc = 0;
        scaleInc = 0;
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displyInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displyInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displxInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displxInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            displzInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            displzInc = 1;
        } else if (window.isKeyPressed(GLFW_KEY_Z)) {
            scaleInc = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            scaleInc = 1;
        }
    }

    public void update(float interval) {
        for (Object object : objects) {
            // Update position
            Vector itemPos = object.getPosition();
            float posx = itemPos.getxValue() + displxInc * 0.01f;
            float posy = itemPos.getyValue() + displyInc * 0.01f;
            float posz = itemPos.getzValue() + displzInc * 0.01f;
            object.setPosition(posx, posy, posz);
            
            // Update scale
            float scale = object.getScale();
            scale += scaleInc * 0.05f;
            if ( scale < 0 ) {
                scale = 0;
            }
            object.setScale(scale);
            
            // Update rotation angle
            float rotation = object.getRotation().getxValue() + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            object.setRotation(rotation, rotation, rotation);            
        }
    }

    public void render(Window window) {
        renderer.render(window, objects);
    }

    public void cleanup() {
        renderer.cleanup();
        for (Object object : objects) {
        	object.getMesh().cleanUp();
        }
    }

}
