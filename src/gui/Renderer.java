package gui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import internal.Drone;
import internal.World;
import internal.WorldObject;
import math.Matrix4f;
import math.Vector3f;

public class Renderer {
	
	private ShaderProgram program;
	
	private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();

	private World world;
	private boolean cameraOnDrone = true;

	private Input input;  
     
    private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float NEAR = 0.01f;
	private static final float FAR = 1000.f;

	/**
     * Initializes the OpenGL state. Creating programs and sets 
     * appropriate state. 
     */
	public Renderer(World world) {
		
		this.world = world;
		this.input = new Input();
		
		program = new ShaderProgram(false, "resources/default.vert", "resources/default.frag");	

        program.init();
        
        try {
			program.createUniform("projectionMatrix");
			program.createUniform("viewMatrix");
	        program.createUniform("modelMatrix");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        float ratio;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			long window = GLFW.glfwGetCurrentContext();
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			GLFW.glfwGetFramebufferSize(window, width, height);
			ratio = (float) width.get() / (float) height.get();
		}
        projectionMatrix = Matrix4f.perspective(FOV, ratio, NEAR, FAR);
        
        checkError();
    }
    
	/**
     * Releases in use OpenGL resources.
     */
    public void release() {
    	
    	for (WorldObject object: world.getObjectSet()) {
    		object.getAssociatedCube().delete();
    	}

    	program.delete();
    	
        checkError();
    }
	
	public Matrix4f getDroneView() {   
		Vector3f orientation = new Vector3f();
		Vector3f dronePosition = new Vector3f();
        for (Drone drone: world.getDroneSet()) {
        	orientation = drone.getOrientation().convertToVector3f();
        	dronePosition = drone.getPosition().convertToVector3f();
        }
        
        Vector3f right = new Vector3f((float) Math.cos(orientation.x), 0, (float) -Math.sin(orientation.x));
		Vector3f up = new Vector3f((float) (Math.sin(orientation.y)*Math.sin(orientation.x)), (float) Math.cos(orientation.y), (float) (Math.sin(orientation.y)*Math.cos(orientation.x)));
		Vector3f look = up.cross(right);
		
		return Matrix4f.viewMatrix(right, up, look, dronePosition);
	}
	
	public void update(boolean cameraOnDrone) {
		if (cameraOnDrone) {
			viewMatrix = getDroneView();
		} else {
			viewMatrix = input.getViewMatrix();
		}
	}

	/**
     * Renders all scene objects.
     */
    public void render(boolean cameraOnDrone) {
    	update(cameraOnDrone);

    	checkError();
        program.bind();
        checkError();
        program.setUniform("projectionMatrix", projectionMatrix);
        program.setUniform("viewMatrix", viewMatrix);
        
        for (WorldObject object: world.getObjectSet()) {
    		program.setUniform("modelMatrix", object.getAssociatedCube().getModelMatrix());
    		object.getAssociatedCube().render();
    	}
        
        program.unbind();
        
        checkError();
    }
    
    /**
     * Utility method which checks for an OpenGL error, throwing an exception if
     * one is found.
     */
    public static void checkError() {
        int err = glGetError();
        switch(err) {
            case GL_NO_ERROR: return;
            case GL_INVALID_OPERATION: throw new RuntimeException("Invalid Operation");
            case GL_INVALID_ENUM: throw new RuntimeException("Invalid Enum");
            case GL_INVALID_VALUE: throw new RuntimeException("Invalid Value");
            case GL_INVALID_FRAMEBUFFER_OPERATION: throw new RuntimeException("Invalid Framebuffer Operation");
            case GL_OUT_OF_MEMORY: throw new RuntimeException("Out of Memory");
        }
    }
}
