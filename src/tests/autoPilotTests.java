package tests;
import org.junit.Test;

import Autopilot.AutopilotConfig;
import Autopilot.AutopilotInputs;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import internal.AutoPilot;
import internal.AutoPilotCamera;
import internal.AutoPilotInputs;
import internal.Drone;
import internal.HorizontalWing;
import internal.Pixel;
import internal.SquareMatrix;
import internal.Vector;
import internal.VerticalWing;
import internal.Wing;
import junit.framework.Assert;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

/**
 * 
 * @author Anthony Rathé
 * 
 */
// TODO autopilot debuggen
public class autoPilotTests {

	
	
	public AutoPilot AP = new AutoPilot();
	public AutopilotConfig APConfig;
	public AutopilotInputs APInputs;
	
	private final static float RED_H_VALUE = 0.0f;
    private final static float RED_S_VALUE = 1.0f;
    private final static float Z_AXIS_V_VALUE = 0.7f;
    private final static float EPSILON  = 1E-5f;
	
	public float delta = 0.000001f;
	
	/**
	 * Creates a byte array that simulates an image (101x101 with the red cube at a certain location.
	 * @param x
	 * @param y
	 * @return
	 */
	static byte[] createImage(int x, int y) {
		ArrayList<Byte> image = new ArrayList<Byte>();
		for(int row = -50; row < 50; row++) {
			for(int col = -50; col < 50; col++) {
				if (col == x && row == y && false) {
					// Add red RGB bytes
					image.add("ÿ".getBytes()[0]);
					image.add("0".getBytes()[0]);
					image.add("0".getBytes()[0]);
				}else {
					// Add blue RGB bytes
					image.add("0".getBytes()[0]);
					image.add("0".getBytes()[0]);
					image.add("ÿ".getBytes()[0]);
				}
			}
		}
		byte[] imageArray = new byte[image.size()];
		for(int i = 0; i < image.size(); i++) {
			imageArray[i] = image.get(i);
		}
		return imageArray;
	}
	
	@Before
	public final void assignCamera() {
		AP.setAPCamera(new AutoPilotCamera(autoPilotTests.createImage(0, 0), 1f, 1f, 100, 100, "a"));
	}
	@Test
	public final void autoPilotOutputsTest() throws IOException{
		Pixel redPixel = new Pixel((byte) (255 + 128), (byte) 128, (byte) 128);
		Vector HSV = new Vector(redPixel.convertToHSV());
        float hValue = HSV.getxValue();
        float sValue = HSV.getyValue();
        float vValue = HSV.getzValue();
        
        System.out.println(hValue);
        System.out.println(sValue);
        System.out.println(vValue);
        
        System.out.println(redPixel.getGreenInt());
        
		System.out.println(Pixel.isEqualFloat(hValue, RED_H_VALUE, EPSILON) && Pixel.isEqualFloat(sValue, RED_S_VALUE, EPSILON) && Pixel.isEqualFloat(vValue, Z_AXIS_V_VALUE, EPSILON));
		APInputs = new AutoPilotInputs(autoPilotTests.createImage(1, 6), 0f, 0f, 0f, 0f, 0f, 0f, 0f);
		assertEquals(AP.simulationStarted(APConfig, APInputs).getHorStabInclination(), (float)Math.PI/3, delta);
		
	}
	
	
	
	
	
}


