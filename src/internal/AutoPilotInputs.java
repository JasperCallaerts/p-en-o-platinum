package internal;

/**
 * 
 * @author Anthony Rathe
 *
 */
public class AutoPilotInputs implements Autopilot.AutopilotInputs{
	public AutoPilotInputs(byte[] image, float xPosition, float yPosition, float zPosition, float heading, float pitch, float roll, float elapsedTime) {
		this.image = image;
		this.xPostion = xPosition;
		this.yPosition = yPosition;
		this.zPosition = zPosition;
		this.heading = heading;
		this.pitch = pitch;
		this.roll = roll;
		this.elapsedTime = elapsedTime;
	}
	
	public byte[] getImage() {
		return this.image;
	}
	
	public float getX() {
		return this.xPostion;
	}
	
	public float getY() {
		return this.yPosition;
	}
	
	public float getZ() {
		return this.zPosition;
	}
	
	public float getHeading() {
		return this.heading;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getRoll() {
		return this.roll;
	}
	
	public float getElapsedTime() {
		return this.elapsedTime;
	}
	
	
	private final byte[] image;
	private final float xPostion;
	private final float yPosition;
	private final float zPosition;
	private final float heading;
	private final float pitch;
	private final float roll;
	private final float elapsedTime;
}
