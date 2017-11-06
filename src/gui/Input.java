package gui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

import org.lwjgl.glfw.GLFW;

import math.Matrix4f;
import math.Vector3f;

public class Input {
	
    private Vector3f position = new Vector3f();
	private Mouse mouse;

	private float yaw = 0;
	private float pitch = 0;
	
    private static final float SPEED = 4f;
    private static final float TURN_SPEED = 0.1f;
    
    private Vector3f right;
    private Vector3f up;
    private Vector3f look;
    
    Input() {
    	mouse = new Mouse(GLFW.glfwGetCurrentContext());
    }

    /**
     * Processes input.
     */
	private void processInput() {
		
		double delta = Window.getDeltaTime();
		
		mouse.update();
		yaw = yaw - mouse.dx() * TURN_SPEED * (float)delta;
		pitch = pitch + mouse.dy() * TURN_SPEED * (float)delta;

		right = new Vector3f((float) Math.cos(yaw), 0, (float) -Math.sin(yaw));
		up = new Vector3f((float) (Math.sin(pitch)*Math.sin(yaw)), (float) Math.cos(pitch), (float) (Math.sin(pitch)*Math.cos(yaw)));
		look = up.cross(right);
		
		Vector3f vec = new Vector3f();
        if (isKeyPressed(GLFW_KEY_UP) || isKeyPressed(GLFW_KEY_W)) {
            vec = vec.add(look);
        } 
        if (isKeyPressed(GLFW_KEY_DOWN) || isKeyPressed(GLFW_KEY_S)) {
        	vec = vec.add(look.negate());
        }
        if (isKeyPressed(GLFW_KEY_LEFT) || isKeyPressed(GLFW_KEY_A)) {
        	vec = vec.add(right.negate());
        }
        if (isKeyPressed(GLFW_KEY_RIGHT) || isKeyPressed(GLFW_KEY_D)) {
        	vec = vec.add(right);
        }
        if (isKeyPressed(GLFW_KEY_SPACE)) {
        	vec = vec.add(up);
        } 
        if (isKeyPressed(GLFW_KEY_LEFT_ALT)) {
        	vec = vec.add(up.negate());
        }
        
        position = position.add(vec.scale(SPEED * (float)delta));
	}
	
	public Matrix4f getViewMatrix() {
		processInput();
		return Matrix4f.viewMatrix(right, up, look, position);
	}
	
	private boolean isKeyPressed(int keyCode) {
		return glfwGetKey(GLFW.glfwGetCurrentContext(), keyCode) == GLFW_PRESS;
	}
}