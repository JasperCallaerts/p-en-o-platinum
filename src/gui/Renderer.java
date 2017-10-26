package gui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
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

import math.Matrix4f;
import math.Vector3f;

public class Renderer {
	
	private ShaderProgram program;
	
	private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    
    private Vector3f position = new Vector3f(0, 0, 0);
	private long window;
	private Mouse mouse;
	private Cube cube;

	private float yaw = 0;
	private float pitch = 0;
    private static final float SPEED = 1f;
    private static final float TURN_SPEED = 0.1f;
    
    private static final Vector3f ABSOLUTE_RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);
    private static final Vector3f ABSOLUTE_UP = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f ABSOLUTE_FRONT = new Vector3f(0.0f, 0.0f, -1.0f);
     
    private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float NEAR = 0.01f;
	private static final float FAR = 1000.f;

	public Renderer(long window) {
		this.window = window;
		program = new ShaderProgram(false, "resources/default.vert", "resources/default.frag");
		cube = new Cube(program);
		mouse = new Mouse(window);
	}

	/**
     * Initializes the OpenGL state. Creating programs, VAOs and VBOs and sets 
     * appropriate state. 
     */
    public void init() {
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
        
        cube.init();
        
        checkError();
    }
    
	/**
     * Releases in use OpenGL resources.
     */
    public void release() {
    	cube.delete();
    	program.delete();
    }

	public void update(double delta) {

		mouse.update(window);
		yaw = yaw - mouse.dx() * TURN_SPEED * (float)delta;
		pitch = pitch + mouse.dy() * TURN_SPEED * (float)delta;

		Vector3f right = new Vector3f((float) Math.cos(yaw), 0, (float) -Math.sin(yaw));
		Vector3f up = new Vector3f((float) (Math.sin(pitch)*Math.sin(yaw)), (float) Math.cos(pitch), (float) (Math.sin(pitch)*Math.cos(yaw)));
		Vector3f look = up.cross(right);
		
		Vector3f vec = new Vector3f(0.0f, 0.0f, 0.0f);
        if (isKeyPressed(GLFW_KEY_UP)) {
            vec = vec.add(look);
        } 
        if (isKeyPressed(GLFW_KEY_DOWN)) {
        	vec = vec.add(look.negate());
        }
        if (isKeyPressed(GLFW_KEY_LEFT)) {
        	vec = vec.add(right.negate());
        }
        if (isKeyPressed(GLFW_KEY_RIGHT)) {
        	vec = vec.add(right);
        }
        if (isKeyPressed(GLFW_KEY_SPACE)) {
        	vec = vec.add(up);
        } 
        if (isKeyPressed(GLFW_KEY_LEFT_ALT)) {
        	vec = vec.add(up.negate());
        }
        
        position = position.add(vec.scale(SPEED * (float)delta));
        viewMatrix = Matrix4f.viewMatrix(right, up, look, position);
        
//        cube.update(new Vector3f((float) (Math.cos(glfwGetTime())*delta), (float) (Math.sin(glfwGetTime())*delta), 0f));
        cube.update(new Vector3f());
	}
	
	private boolean isKeyPressed(int keyCode) {
		return glfwGetKey(window, keyCode) == GLFW_PRESS;
	}

	/**
     * Renders all scene objects.
     */
    public void render() {

        program.bind();
        program.setUniform("projectionMatrix", projectionMatrix);
        program.setUniform("viewMatrix", viewMatrix);
        
        cube.render();
        
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
