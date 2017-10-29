package gui;

import internal.CameraImage;
import internal.Pixel;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

	// The window handle
	private long window;
	private Renderer renderer;
	private double time;
	private double previousTime;

	public Window() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will not be resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); 

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Project X!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.5f, 0.8f, 1.0f, 1.0f);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		Renderer renderer = new Renderer(window);
		renderer.init();
	}

	public void renderFrame() {	
		// Run the rendering unless the user has attempted to close
		// the window or has pressed the ESCAPE key.
		if (glfwWindowShouldClose(window)) {
			terminate();
		}

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		renderer.update(getDeltaTime());
		renderer.render();

		glfwSwapBuffers(window); // swap the color buffers

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}
	
	public void terminate() {
		renderer.release();
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public void updateTime() {
		this.previousTime = this.time;
		this.time = glfwGetTime();
	}
	
	public double getDeltaTime() {
		return time - previousTime;
	}

	/**
	 * Reads the pixels on the screen and returns them as an array of bytes
	 * @return an byte array containing the image
	 * @author Martijn
	 */
	public static byte[] getCameraView(){
		ByteBuffer buffer = BufferUtils.createByteBuffer(HEIGHT *WIDTH*3);
		//the array used for storing the pixels
		byte[] pixelArray = new byte[HEIGHT *WIDTH*3];
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		//prepare the buffer for filling
		buffer.clear();
		//read the pixels on screen in the given window
		GL11.glReadPixels(0,0, WIDTH, HEIGHT, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);


		//prepare buffer for reading
		buffer.clear();
		//transfer al the buffered data to the array
		buffer.get(pixelArray);

		//rescale the image to the appropriate size
		byte[] rescaledPixelArray = rescale(pixelArray, WIDTH, CAMERA_WIDTH, HEIGHT, CAMERA_HEIGHT);

		return rescaledPixelArray;
	}

	/**
	 * Rescales the given image to an image of size (newNbRows*newNbColumns)
	 * @param newNbRows the nb of rows the rescaled image contains
	 * @param newNbColumns the nb of columns the rescaled image contains
	 * @return a new byte array containing the rescaled image
	 * note: algorithm is modified version from the one used in:
	 * http://tech-algorithm.com/articles/nearest-neighbor-image-scaling/
	 */
	public static byte[] rescale(byte[] imageArray, int oldNbRows, int newNbRows,int oldNbColumns, int newNbColumns){


		byte[] temp = new byte[newNbRows*newNbColumns*3];
		float xRatio = oldNbColumns/(float)newNbColumns;
		float yRatio = oldNbRows/(float)newNbRows;
		float px, py;

		for(int i = 0; i!= newNbRows; i++){
			for(int j = 0; j!= newNbColumns; j++){
				px = (float) Math.floor(j*xRatio);
				py = (float) Math.floor(i*yRatio);

				for(int k = 0; k != 3; k++) {
					temp[((i * newNbColumns) + j) * 3 + k] = imageArray[((int) (py * oldNbColumns + px)) * 3 + k];
				}

			}
		}

		return temp;
	}

	private static int WIDTH = 1000;
	private static int HEIGHT = 1000;
	private static int CAMERA_WIDTH = 200;
	private static int CAMERA_HEIGHT = 200;

}