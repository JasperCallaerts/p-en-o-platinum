

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
	
	// the starting time
	private double firstTime;
	
	// the time of the previous update
	private double lastTime;
	
	// updates per second
	private int ups;
	
    // counter for the ups calculation.
    private int upsCount;
    
    // time for the ups calculation.
    private double timeCount;
	
	// time passed since last update
	private double delta;
	

	public Timer() {
		lastTime = getTime();
		firstTime = getTime();
	}
	
	public void update() {
		double time = getTime();
        delta = (time - lastTime);
        lastTime = time;
        
        timeCount += delta;
        if (timeCount > 1f) {
            ups = upsCount;
            upsCount = 0;

            timeCount -= 1f;
        }
    }

	private double getTime() {
		return glfwGetTime();
	}
	
	double getDelta() {
		return delta;
	}
	
	// time passed since this timer was made
	double getTimePassed() {
		return getTime() - firstTime;
	}
	
	int getUps() {
		if (ups > 0) {
			return ups;
		} else {
			return upsCount;
		}
	}
}
