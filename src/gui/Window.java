package gui;

import internal.Block;
import internal.CameraImage;
import internal.Drone;
import internal.Pixel;
import internal.World;
import internal.WorldObject;
import math.Matrix3f;
import math.Matrix4f;
import math.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

	// The window handle
	private long windowHandle;
	
	GLCapabilities capabilities;
	
	private int WIDTH;
	private int HEIGHT;

	private Input input;
	private ShaderProgram program;
	
    private float FOV;
	private static final float NEAR = 0.01f;
	private static final float FAR = 1000.f;
	
	private String title;

	private Matrix4f viewMatrix;

	private Matrix4f projectionMatrix;

	private World world;

	private boolean terminated = false;

	private Window dependableWindow = null;

	private Settings setting;

	/**
	 * Creates a window.
     * Initializes the OpenGL state. Creating programs and sets 
     * appropriate state. 
	 * @param visible 
     */
	public Window(int width, int height, float xOffset, float yOffset, String title, Vector3f color, boolean visible) {
		WIDTH = width;
		HEIGHT = height;
		this.title = title;
		
		// Create the window
		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if ( getHandler() == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(getHandler(), (handle, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(handle, true); // We will detect this in the rendering loop
		});
		
		glfwSetInputMode(getHandler(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(getHandler(), pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					getHandler(),
				(int) ((vidmode.width() - pWidth.get(0)) * xOffset),
				(int) ((vidmode.height() - pHeight.get(0)) * yOffset)
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(getHandler());
				
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		if (visible)
			glfwShowWindow(getHandler());
		
		if (visible)
			this.FOV = (float) Math.toRadians(60.0f);
		else
			this.FOV = (float) Math.toRadians(120.0f);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		capabilities = GL.createCapabilities();

		// Set the clear color
		glClearColor(color.x, color.y, color.z, 1.0f);
        
		glfwMakeContextCurrent(NULL);
	}
	
	public void initWindow(Window window) {
		this.setting = Settings.TEXT_WINDOW;
		this.dependableWindow = window;
		
		glfwMakeContextCurrent(getHandler());
		
		program = new ShaderProgram(false, "resources/default.vert", "resources/default.frag");	

        program.init();
        
        input = new Input(setting);
        
        glfwMakeContextCurrent(NULL);
	}
	
	public void initWindow(World world, Settings setting) {
		this.setting = setting;
		this.world = world;
//		if (setting == Settings.DRONE_CAM)
//			this.FOV = (float) Math.toRadians(120.0f);
//		else
//			this.FOV = (float) Math.toRadians(60.0f);
		
		glfwMakeContextCurrent(getHandler());
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		program = new ShaderProgram(false, "resources/3dWorld.vert", "resources/3dWorld.frag");	

        program.init();
        
        input = new Input(setting);
        
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
        
		if (setting == Settings.INDEPENDENT_CAM)
			glfwSetInputMode(getHandler(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwMakeContextCurrent(NULL);
	}

	public void render() {
		if (setting == Settings.TEXT_WINDOW)
			renderText();
		else
			renderFrame();

		// Return false if the user has attempted to close
		// the window or has pressed the ESCAPE key.
		if (glfwWindowShouldClose(getHandler())) {
			terminate();
		}
//		checkError();
	}
	
	private void renderText() {
		// TODO maak een window met text ipv 3d graphics
	}
	
	private void renderFrame() {
		GL.setCapabilities(capabilities);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the buffers

		if (setting == Settings.INDEPENDENT_CAM)
			input.processInput();
		updateMatrices();
	
		program.bind();
        program.setUniform("projectionMatrix", projectionMatrix);
        program.setUniform("viewMatrix", viewMatrix);

        for (WorldObject object: world.getObjectSet()) {
        	for (Cube cube: object.getAssociatedCubes()) {
	        	if (object.getClass() == Block.class)
	        		program.setUniform("modelMatrix", cube.getMatrix());
	        	else 
	        		program.setUniform("modelMatrix", getAdvancedModelMatrix(((Drone) object).getOrientation().convertToVector3f().negate(), cube.getModelMatrix(), cube.getSizeMatrix()));
	    		cube.render();
        	}
    	}
		
		program.unbind();
		
		glfwSwapBuffers(getHandler()); // swap the  buffers
	}
	
	// Free the window callbacks and destroy the window
	public void terminate() {
		program.delete();
		
		glfwFreeCallbacks(getHandler());
		glfwDestroyWindow(getHandler());
		
		/**
	     * Releases in use OpenGL resources.
	     */
		for (WorldObject object: world.getObjectSet()) {
			for (Cube cube: object.getAssociatedCubes())
				cube.delete();
    	}
		
		terminated = true;
	}
	
	public boolean isTerminated() {
		return terminated ;
	}

	public long getHandler() {
		return windowHandle;
	}
	
	public void updateMatrices() {
		switch (setting) {
		case DRONE_CAM: viewMatrix = getDroneView();
						break;
		case DRONE_CHASE_CAM: viewMatrix = getChaseView();
						break;
		default: viewMatrix = input.getViewMatrix();
			break;
		}
		
		projectionMatrix = getProjectionMatrix();
	}
	
	public Matrix4f getAdvancedModelMatrix(Vector3f orientation, Matrix4f modelMatrix, Matrix4f sizeMatrix) {
		
		Matrix3f transformationMatrix = getTransformationMatrix(orientation);
        
        Vector3f right = transformationMatrix.multiply(new Vector3f(1,0,0));
        Vector3f up = transformationMatrix.multiply(new Vector3f(0, 1,0));
        Vector3f look = transformationMatrix.multiply(new Vector3f(0,0, -1));
        
		return modelMatrix.multiply(Matrix4f.viewMatrix(right, up, look, new Vector3f())).multiply(sizeMatrix);
	}
	
	public Matrix4f getChaseView() {
		Vector3f orientation = new Vector3f();
		Vector3f dronePosition = new Vector3f();
        for (Drone drone: world.getDroneSet()) {
        	orientation = drone.getOrientation().convertToVector3f();
        	dronePosition = drone.getPosition().convertToVector3f();
        }
        Matrix3f yawMatrix = new Matrix3f(new Vector3f((float) Math.cos(orientation.x), 0, (float) Math.sin(orientation.x)), new Vector3f(0, 1, 0), new Vector3f((float) -Math.sin(orientation.x), 0, (float) Math.cos(orientation.x)));
        Matrix3f transformationMatrix = yawMatrix.transpose();
        
        Vector3f right = transformationMatrix.multiply(new Vector3f(1,0,0));
        Vector3f up = transformationMatrix.multiply(new Vector3f(0,1,0));
        Vector3f look = transformationMatrix.multiply(new Vector3f(0,0,-1));
        
        Vector3f position = dronePosition.add(new Vector3f(-10 * look.x, 0f, -10 * look.z));
		
		return Matrix4f.viewMatrix(right, up, look, position);
	}
	
	public Matrix4f getDroneView() {   
		Vector3f orientation = new Vector3f();
		Vector3f dronePosition = new Vector3f();
        for (Drone drone: world.getDroneSet()) {
        	orientation = drone.getOrientation().convertToVector3f();
        	dronePosition = drone.getPosition().convertToVector3f();
        }
        
        Matrix3f transformationMatrix = getTransformationMatrix(orientation);
        
        Vector3f right = transformationMatrix.multiply(new Vector3f(1,0,0));
        Vector3f up = transformationMatrix.multiply(new Vector3f(0,1,0));
        Vector3f look = transformationMatrix.multiply(new Vector3f(0,0,-1));

		return Matrix4f.viewMatrix(right, up, look, dronePosition);
	}
	
	public static Matrix3f getTransformationMatrix(Vector3f orientation) {
		Matrix3f pitchMatrix = new Matrix3f(new Vector3f(1, 0, 0), new Vector3f(0, (float) Math.cos(orientation.y), (float) -Math.sin(orientation.y)), new Vector3f(0, (float) Math.sin(orientation.y), (float) Math.cos(orientation.y)));
        Matrix3f yawMatrix = new Matrix3f(new Vector3f((float) Math.cos(orientation.x), 0, (float) Math.sin(orientation.x)), new Vector3f(0, 1, 0), new Vector3f((float) -Math.sin(orientation.x), 0, (float) Math.cos(orientation.x)));
        Matrix3f rollMatrix = new Matrix3f(new Vector3f((float) Math.cos(orientation.z), (float) Math.sin(orientation.z), 0), new Vector3f((float) -Math.sin(orientation.z), (float) Math.cos(orientation.z), 0), new Vector3f(0, 0, 1));
        		
        Matrix3f transformationMatrix = yawMatrix.multiply(pitchMatrix).multiply(rollMatrix);
        transformationMatrix = transformationMatrix.transpose();
        
        return transformationMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
        float ratio;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			long window = GLFW.glfwGetCurrentContext();
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			GLFW.glfwGetFramebufferSize(window, width, height);
			ratio = (float) width.get() / (float) height.get();
		}
        return Matrix4f.perspective(FOV, ratio, NEAR, FAR);
	}

	public boolean uses3d() {
		if (setting == Settings.TEXT_WINDOW)
			return false;
		return true;
	}
	
	public String getTitle() {
		return this.title;
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

	/**
	 * Reads the pixels on the screen and returns them as an array of bytes
	 * @return an byte array containing the image
	 * @author Martijn
	 */
	public byte[] getCameraView() throws IOException {
		glfwMakeContextCurrent(getHandler());

		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(WIDTH * HEIGHT * bpp);
		GL11.glReadPixels(0, 0, WIDTH, HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );

		byte[] imageByteArray = new byte[WIDTH*HEIGHT*bpp];
		buffer.get(imageByteArray);

		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

//		String format = "PNG";
//		File file = new File("image.png");
//
//		for(int x = 0; x < WIDTH; x++)
//		{
//			for(int y = 0; y < HEIGHT; y++)
//			{
//				int i = (x + (WIDTH * y)) * bpp;
//				int r = (imageByteArray[i])& 0xFF;
//				int g = (imageByteArray[i + 1]) & 0xFF;
//				int b =(imageByteArray[i+2])& 0xFF;
//				image.setRGB(x, HEIGHT - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
//			}
//		}
//
//		try {
//			ImageIO.write(image, format, file);
//		} catch (IOException e) { e.printStackTrace(); }

		byte[] rescaledArray = rescale(imageByteArray, WIDTH, CAMERA_WIDTH, HEIGHT, CAMERA_HEIGHT, bpp);

		return rescaledArray;
	}

	/**
	 * Rescales the given image to an image of size (newNbRows*newNbColumns)
	 * @param newNbRows the nb of rows the rescaled image contains
	 * @param newNbColumns the nb of columns the rescaled image contains
	 * @return a new byte array containing the rescaled image
	 * note: algorithm is modified version from the one used in:
	 * http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/
	 */
	public static byte[] rescale(byte[] imageArray, int oldNbRows, int newNbRows,int oldNbColumns, int newNbColumns, int bpp){


		byte[] temp = new byte[newNbRows*newNbColumns*3];
		float xRatio = oldNbColumns/(float)newNbColumns;
		float yRatio = oldNbRows/(float)newNbRows;
		float px, py;

		for(int i = 0; i!= newNbRows; i++){
			for(int j = 0; j!= newNbColumns; j++){
				px = (float) Math.floor(j*xRatio);
				py = (float) Math.floor(i*yRatio);

				for(int k = 0; k != 3; k++) {
					temp[((i * newNbColumns) + j) * 3 + k] = convertToBiasedByte(imageArray[((int) (py * oldNbColumns + px)) * bpp+ k]);
				}

			}
		}

		return temp;
	}

	public static byte convertToBiasedByte(byte glByte){
		byte biasedByte;
		if(glByte <= (byte)0){
			biasedByte = (byte)(glByte - 128);
		}else{
			biasedByte = (byte)(glByte + 128);
		}

		return biasedByte;
	}

	private static int CAMERA_WIDTH = 200;
	private static int CAMERA_HEIGHT = 200;
	private static boolean firstRun = true;

}

//		ByteBuffer buffer = BufferUtils.createByteBuffer(HEIGHT *WIDTH*4);
//		//the array used for storing the pixels
//		byte[] pixelArray = new byte[HEIGHT *WIDTH*4];
//		int[] imageData = new int[CAMERA_WIDTH * CAMERA_HEIGHT];
//		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
//		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
//		//prepare the buffer for filling
//		buffer.clear();
//		//read the pixels on screen in the given window
//		GL11.glReadPixels(0,0, WIDTH, HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
//
//
//		//prepare buffer for reading
//		//transfer al the buffered data to the array
//		buffer.get(pixelArray);
//
//		//rescale the image to the appropriate size
//		byte[] rescaledPixelArray = rescale(pixelArray, WIDTH, CAMERA_WIDTH, HEIGHT, CAMERA_HEIGHT);
//		if(firstRun){
//			for (int l = 0; l < CAMERA_WIDTH; l++) {
//				for (int i1 = 0; i1 < CAMERA_HEIGHT; i1++) {
//					int j1 = l + (CAMERA_HEIGHT - i1 - 1) * CAMERA_WIDTH;
//					int k1 = rescaledPixelArray[j1 * 3 + 0] & 0xff;
//					int l1 = rescaledPixelArray[j1 * 3 + 1] & 0xff;
//					int i2 = rescaledPixelArray[j1 * 3 + 2] & 0xff;
//					int j2 = 0xff000000 | k1 << 16 | l1 << 8 | i2;
//					imageData[l + i1 * CAMERA_WIDTH] = j2;
//				}
//			}
//			BufferedImage bufferedImage = new BufferedImage(CAMERA_WIDTH, CAMERA_HEIGHT, 1);
//			bufferedImage.setRGB(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, imageData, 0, CAMERA_WIDTH);
//			int index = 0;
//			for(byte bytes: pixelArray){
//				System.out.println(bytes);
//				if(index == 200){
//					break;
//				}
//				index++;
//
//			}
//
//			firstRun = false;
//		}
//
//
//
//
//		return rescaledPixelArray;
