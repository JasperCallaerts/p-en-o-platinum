package gui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
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

import math.Matrix4f;
import math.Vector3f;

public class Renderer {

	private static final CharSequence vertexSrc = "#version 330\n"

        		+ "layout (location=0) in vec3 position;\n"
        		+ "layout (location=1) in vec3 inColor;\n"

        		+ "out vec3 Color;\n"

        		+ "uniform mat4 modelMatrix;\n"
        		+ "uniform mat4 viewMatrix;\n"
        		+ "uniform mat4 projectionMatrix;\n"

        		+ "void main()\n"
        		+ "{\n"
        		+ "    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);\n"
        		+ "    Color = inColor;\n"
        		+ "}\n";
	private static final CharSequence fragmentSrc = "#version 330 core\n"

        		+ "in vec3 Color;\n"

    	        + "out vec4 exColor;\n"

    	        + "void main()\n"
    	        + "{\n"
    	        + "    exColor = vec4(Color, 1.0f);\n"
    	        + "}\n";
	
	private ShaderProgram program;
	
	private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f modelMatrix = new Matrix4f();
    
    private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
	private long window;
	private Mesh mesh;
	private Mouse mouse;
    private static final float SPEED = 1f;
    
    private static final float FOV = (float) Math.toRadians(120.0f);
	private static final float NEAR = 0.01f;
	private static final float FAR = 1000.f;
	
	static float[] positions = new float[]{
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
			0.5f, -0.5f, -0.5f
	};
	static float[] colours = new float[]{
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f,
			0.5f, 0.0f, 0.0f
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

	public Renderer(long window) {
		this.window = window;
		mesh = new Mesh();
		program = new ShaderProgram(vertexSrc, fragmentSrc);
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
        
        mesh.init(positions, colours, indices);
        
        checkError();
    }
    
	/**
     * Releases in use OpenGL resources.
     */
    public void release() {
    	mesh.delete();
    	program.delete();
    }

	public void update(double delta) {
		
		mouse.update(window);
		System.out.println(mouse.dx());
		System.out.println(mouse.dy());
		System.out.println("");
		
		Vector3f vec = new Vector3f(0.0f, 0.0f, 0.0f);
        if (isKeyPressed(GLFW_KEY_UP)) {
            vec = new Vector3f(0.0f, 0.0f, -SPEED * (float)delta);
        } else if (isKeyPressed(GLFW_KEY_DOWN)) {
        	vec = new Vector3f(0.0f, 0.0f, SPEED* (float)delta);
        } else if (isKeyPressed(GLFW_KEY_LEFT)) {
        	vec = new Vector3f(-SPEED* (float)delta, 0.0f, 0.0f);
        } else if (isKeyPressed(GLFW_KEY_RIGHT)) {
        	vec = new Vector3f(SPEED* (float)delta, 0.0f, 0.0f);
        } else if (isKeyPressed(GLFW_KEY_SPACE)) {
        	vec = new Vector3f(0.0f, SPEED* (float)delta, 0.0f);
        } else if (isKeyPressed(GLFW_KEY_LEFT_ALT)) {
        	vec = new Vector3f(0.0f, -SPEED* (float)delta, 0.0f);
        }
        
        position = position.add(vec);
        Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);
    	Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    	Vector3f dir = new Vector3f(0.0f, 0.0f, -1.0f);
    	viewMatrix = Matrix4f.viewMatrix(right, up, dir, position);
    	
//    	math.Vector4f teeest = Matrix4f.perspective(FOV, 16.0f/9.0f, NEAR, FAR).multiply(Matrix4f.viewMatrix(right, up, dir, position).multiply(new math.Vector4f(0.5f, 0.5f, 0.5f, 1.0f)));  				
//		System.out.println(teeest.x/teeest.w);
//		System.out.println(teeest.y/teeest.w);
//		System.out.println(teeest.z/teeest.w);
//		System.out.println(teeest.w);
//		System.out.println("");
      
//		System.out.println(position.x);
//		System.out.println(position.y);
//		System.out.println(position.z);
//		System.out.println("");
     
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
        program.setUniform("modelMatrix", modelMatrix);
        
        mesh.render();
        
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
