package tests;
import org.junit.Test;
import static org.junit.Assert.assertNotEquals;

import internal.AutoPilot;
import internal.Drone;
import internal.HorizontalWing;
import internal.SquareMatrix;
import internal.Vector;
import internal.VerticalWing;
import internal.Wing;
import junit.framework.Assert;

import static org.junit.Assert.assertEquals;

import org.junit.Before;

/**
 * 
 * @author Jonathan
 *
 */
public class droneTest {

	public float PI = (float) Math.PI;
	
	public static Drone drone1,drone2;
	public static Wing wing1,wing2,wing3,wing4;
	public Vector position = new Vector(1,0,2);
	public Vector velocity = new Vector(1,1,0);
	public Vector orientation = new Vector(PI,PI/2,0);
	public Vector rotation = new Vector(1,1,1);
	
	public Vector relPosWing1 = new Vector(1f,0f,0f);
	public Vector relPosWing2 = new Vector(-1f,0f,0f);
	public Vector relPosWing3 = new Vector(0f,0f,-1f);
	public Vector relPosWing4 = new Vector(0f,0f,-2f);
	
	public AutoPilot AP = new AutoPilot();
	
	public float delta = 0.000001f;
	
	
	
	HorizontalWing wingTest1 = new HorizontalWing(relPosWing1,20,20,1,0.5f);
	
	@Before
	public void wingSetup(){
		//liftslope/mass/maxincl/incl
		wing1 = new HorizontalWing(relPosWing1,5,10,1,0.5f);
		wing2 = new HorizontalWing(relPosWing2,5,10,1,0.5f);
		wing3 = new HorizontalWing(relPosWing3,10,5,1f,0.5f);
		wing4 = new VerticalWing  (relPosWing4,5,5,1,0.5f);
	}
	
	
	@Before
	public void setupDrones(){
		//dronemass/enginemass/maxthrust
		drone1 = new Drone(50, 10, 30, position, velocity, orientation, 
				rotation, wing2, wing1, wing3, wing4, AP); 
	}
	
	@Test
	public final void droneOnWorldTest(){
		Vector v = drone1.droneOnWorld(orientation);
		assertEquals(v.getxValue(), -PI, delta);
		assertEquals(v.getyValue(), 0, delta);
		assertEquals(v.getzValue(), -PI/2, delta);
	}
	

	
	
	@Test
	public final void getTotalMassTest(){
		float m = drone1.getTotalMass();
		float mass = 50f+10f+10f+10f+5f+5f;
		float mass2 = 50f+10f+10f+10f+5f+5f+0.0001f;
		assertEquals(mass, m, delta);
		assertNotEquals(mass2, m, delta);
	}
	
	
	@Test
	public final void setInertiaTensorTest(){
		float[] m = {5+20+22.5f,0,0,
		             0,5+20+22.5f+10+10,0,
		             0,0,10+10};
		SquareMatrix inert = drone1.getInertiaTensor();
		assertEquals(new SquareMatrix(m), inert);
	}
	
	@Test
	public final void setLeftWingTest(){
		drone1.setLeftWing(wingTest1);
		assertEquals(drone1.getLeftWing(), wingTest1);
		assertEquals(drone1.getLeftWing().getMass(),20,delta);
	}
	
	@Test
	public final void setRightWingTest(){		
		drone1.setRightWing(wingTest1);
		assertEquals(drone1.getRightWing(), wingTest1);
		assertEquals(drone1.getRightWing().getLiftSlope(),20,delta);
	}
	
	@Test
	public final void setOrientationTest(){
		float h = 5;
		float p = 3;
		float r = 4;
		drone1.setOrientation(new Vector(h,p,r));
		assertEquals(drone1.getHeading(), h, delta);
		assertEquals(drone1.getPitch(), p, delta);
		assertEquals(drone1.getRoll(), r, delta);
		
	}
	
	@Test
	public final void setThrustTest(){
		drone1.setThrust(30);
		assertEquals(drone1.getThrust(), 30,delta);		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public final void setThrustTest2(){
		drone1.setThrust(31);
	}
	
	@Test 
	public final void getGravityTest(){
		Vector g = drone1.getGravity();
		Vector v = new Vector(0f,(float)(-drone1.getTotalMass()*9.8106),0f);
		assertEquals(g.getyValue(), v.getyValue(), 0.001);
		assertEquals(g.getxValue(), v.getxValue(), 0.001);
		assertEquals(g.getzValue(), v.getzValue(), 0.001);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public final void canHaveAsDroneMassTest(){
		drone2 = new Drone(0, 10, 30, position, velocity, orientation, 
				rotation, wing2, wing1, wing3, wing4, AP);
		drone2.canHaveAsDroneMass(drone2.getDroneMass());
	}
	
	@Test (expected = IllegalArgumentException.class)
	public final void setVelocityTest(){
		float a = 1000000000;
		Vector v = new Vector(a,a,a);
		drone1.setVelocity(v);
	}
	
	@Test
	public final void setVelocityTest2(){
		float a = 100;
		Vector v = new Vector(a,a,a);
		drone1.setVelocity(v);
		assertEquals(drone1.getVelocity(), v);		
	}
	
	
	
}

