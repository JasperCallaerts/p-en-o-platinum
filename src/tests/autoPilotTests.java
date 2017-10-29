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
import internal.AutoPilotConfig;
import internal.AutoPilotInputs;
import internal.Block;
import internal.Drone;
import internal.HorizontalWing;
import internal.Pixel;
import internal.SquareMatrix;
import internal.Vector;
import internal.VerticalWing;
import internal.Wing;
import internal.World;
import junit.framework.Assert;
import math.Vector3f;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

/**
 * 
 * @author Anthony Rathé
 * 
 */
// TODO autopilot debuggen
public class autoPilotTests {

	public World world;
	public Block redBlock;
	public AutoPilot AP;
	public AutoPilotCamera APCamera;
	public AutopilotConfig APConfig;
	public AutopilotInputs APInputs;
	public static Drone drone;
	public static Wing wing1,wing2,wing3,wing4;
	
	public Vector position = new Vector(100,0,2);
	public Vector velocity = new Vector(1,1,0);
	public Vector orientation = new Vector((float)Math.PI,(float)Math.PI/2,0);
	public Vector rotation = new Vector(1,1,1);
	
	public Vector relPosWing1 = new Vector(1,0,0);
	public Vector relPosWing2 = new Vector(-1,0,0);
	public Vector relPosWing3 = new Vector(0,0f,-1f);
	public Vector relPosWing4 = new Vector(0,0,-2);
	
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
				if (col == x && row == y) {
					// Add red RGB bytes
					image.add((byte) (255 + 128));
					image.add((byte) (128));
					image.add((byte) (128));
				}else {
					// Add non-red RGB bytes
					image.add((byte) (128));
					image.add((byte) (128));
					image.add((byte) (128));
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
	public void setup(){
		//liftslope/mass/maxincl/incl
		wing1 = new HorizontalWing(relPosWing1,5,10,1,0.5f);
		wing2 = new HorizontalWing(relPosWing2,5,10,1,0.5f);
		wing3 = new HorizontalWing(relPosWing3,10,5,1f,0.5f);
		wing4 = new VerticalWing  (relPosWing4,5,5,1,0.5f);
		
		APConfig = new AutoPilotConfig(delta, delta, delta, delta, delta, delta, delta, delta, delta, delta, delta, delta, delta, 101, 101);
		AP = new AutoPilot();
		drone = new Drone(50f, 10f, position, velocity, orientation, 
				rotation, wing2, wing1, wing3, wing4, AP); 
		
		world = new World();
		redBlock = new Block(new Vector3f(0f,0f,0f), new Vector3f(255, 0, 0));
		world.addWorldObject(redBlock);
		world.addWorldObject(drone);
		
		AutopilotInputs initialInputs = drone.updateAutopilotInput(1f);
		byte[] initialImage = initialInputs.getImage();
		APCamera = new AutoPilotCamera(initialImage, 1f, 1f, 101, 101);
		AP.setAPCamera(APCamera);
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
	
	@Test
	public final void autoPilotDroneInstructionTest() throws IOException{
		System.out.println(drone.getPosition());
		world.advanceWorldState(10f, 1);
		
	}
	
	
	
	
	
}


