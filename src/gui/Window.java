package gui;

import internal.CameraImage;
import internal.Pixel;
import internal.World;
import math.Matrix4f;

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
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

	// The window handle
	private long windowHandle;
	
	GLCapabilities capabilities;
	
	private int WIDTH;
	private int HEIGHT;

	private Input input;

	private boolean droneView;  
	
    private static final float FOV = (float) Math.toRadians(60.0f);
	private static final float NEAR = 0.01f;
	private static final float FAR = 1000.f;
	
	private String title;

	public Window(int width, int height, float xOffset, float yOffset, String title, boolean cameraOnDrone) {
		this.droneView = cameraOnDrone;
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
		glfwShowWindow(getHandler());
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		capabilities = GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		input = new Input();
	}

	public boolean renderFrame(Renderer renderer) {
		// Make the OpenGL context current
		glfwMakeContextCurrent(getHandler());
		GL.setCapabilities(capabilities);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		renderer.render(this);
		
		glfwSwapBuffers(getHandler()); // swap the color buffers
		Renderer.checkError();
		
		// Run the rendering unless the user has attempted to close
		// the window or has pressed the ESCAPE key.
		if (glfwWindowShouldClose(getHandler())) {
			terminate();
			return false;
		}
		return true;
	}
	
	// Free the window callbacks and destroy the window
	public void terminate() {
		Renderer.checkError();
		glfwFreeCallbacks(getHandler());
		Renderer.checkError();
		glfwDestroyWindow(getHandler());
		Renderer.checkError();
	}

	public long getHandler() {
		return windowHandle;
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
	
	public Matrix4f getViewMatrix() {
		return input.getViewMatrix();
	}
	
	public boolean cameraIsOnDrone() {
		return droneView;
	}
	
	public String getTitle() {
		return this.title;
	}

	/**
	 * Reads the pixels on the screen and returns them as an array of bytes
	 * @return an byte array containing the image
	 * @author Martijn
	 */
	public byte[] getCameraView() throws IOException {

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
