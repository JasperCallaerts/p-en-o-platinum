package gui;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import java.util.HashMap;

import org.lwjgl.glfw.GLFWErrorCallback;

public class Graphics {
	
	HashMap<String, Window> windows =  new HashMap<String, Window>();
	
	public Graphics() {	
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
		glfwWindowHint(GLFW_SAMPLES, 4);
	}
	
	public void addWindow(String key, int width, int height, float xOffset, float yOffset, String title, boolean droneCamera) {
		windows.put(key, new Window(width, height, xOffset, yOffset, title, droneCamera));
	}
	
	public void addWindow(String key, Window window) {
		windows.put(key, window);
	}
	
	public void renderWindows(Renderer renderer) {
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
		
		Time.update();
		
		for (String key: windows.keySet()) {
			
			boolean succes = windows.get(key).renderFrame(renderer);
			if (!succes) {
				windows.get(key).terminate();
				windows.remove(key);
				break;
			}
		}
		if (windows.keySet().size() == 0) {
			renderer.release();
		}
	}
	
	public void renderWindow(String key, Renderer renderer) {
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();

		Time.update();

		Window window = windows.get(key);
		if (window != null) {
			boolean succes = window.renderFrame(renderer);
			if (!succes) {
				Renderer.checkError();
				window.terminate();
				windows.remove(key);
				
			}
		} else {
			renderer.release();
		}
	}

	// Terminate GLFW and free the error callback
	public void terminate() {
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	public Window getWindow(String key) {
		return windows.get(key);
	}
}
