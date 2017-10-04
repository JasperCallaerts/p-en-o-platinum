package internal;

import java.util.Map;

/**
 * 
 * @author r0637882
 *
 */
public class Drone {
	Drone(){
		
	}
	

	
	public void move(float time){
		
	}
	
	/**
	 * returns the center of mass for a given collection of point masses in space
	 * @param massPositions a map containing the positions and the mass of the point-masses
	 * @return sum(vector*mass)/sum(mass) (center of mass equation)
	 */
	public static Vector centerOfMass(Map<Vector, Float> massPositions){
		
		Vector sumVector = new Vector();
		float totalMass = (float) 0.0;
		for (Vector massPosition: massPositions.keySet()){
			
			 float mass = massPositions.get(massPosition);
			 totalMass += mass;
			 
			 sumVector = sumVector.vectorSum(massPosition.scalarMult(mass));
		}
		
		return sumVector.scalarMult(1/totalMass);
	}
	
	/**
	 * returns the moment of inertia for a given collection of point masses and a center of mass
	 * for formula see https://socratic.org/questions/how-do-you-find-moment-of-inertia-of-three-point-masses
	 */
	public static float momentOfInerita(Map<Vector, Float> massPositions, Vector centerOfMass){
		
		float inertia = 0;
		
		for(Vector massPosition: massPositions.keySet()){
			float mass = massPositions.get(massPosition);
			Vector diffVector = massPosition.vectorDifference(centerOfMass);
			float squaredDistance = diffVector.scalarProduct(diffVector);
			inertia += squaredDistance*mass;
		}
		return inertia;
	}
	
	
	
	/**
	 * Getter for the velocity of the drone
	 * @return
	 */
	public Vector getVelocity() {
		return velocity;
	}


	/**
	 * Setter for the velocity of the drone
	 * @param velocity
	 * @throws IllegalArgumentException if the speed is invalid
	 */
	public void setVelocity(Vector velocity) throws IllegalArgumentException {
		if(this.isValidVelocity(velocity)){
			this.velocity = velocity;
		}else{
			throw new IllegalArgumentException(VELOCITY_ERROR);
		}
	}
	
	public boolean isValidVelocity(Vector velocity){
		return velocity.getSize() <= LIGHTSPEED;
	}


	/**
	 * getter for the maximum Thrust of the drone
	 * @return
	 */
	public float getMaxThrust() {
		return maxThrust;
	}
	
	
	/**
	 * getter for the thrust of the drone
	 * @return
	 * @Basic
	 */
	public float getThrust() {
		return thrust;
	}


	/**
	 * Setter for the thrust of the drone
	 * @param thrust the desired new thrust
	 * @throws IllegalArgumentexception if the new thrust is not valid
	 * @Basic
	 */
	public void setThrust(float thrust) throws IllegalArgumentException {
		if(this.canHaveAsThrust(thrust)){
			this.thrust = thrust;
		}else{
			throw new IllegalArgumentException(Drone.THRUST_OUT_OF_RANGE);
		}
	}

	
	/**
	 * Checks if the new thrust is allowed
	 * @param thrust the thrust to be checked
	 * @return true if and only if the thrust is in the range [0, this.getMaxThrust]
	 */
	public boolean canHaveAsThrust(float thrust){
		
		return thrust>=0 && thrust <= this.getMaxThrust();
	}



	/**
	 * A variable containing the position of the drone
	 */
	private Vector position;
	
	/**
	 * A variable containing the velocity of the drone
	 */
	private Vector velocity;
	
	/**
	 * A variable containing the acceleration of the drone
	 */
	private Vector acceleration;
	
	/**
	 * A variable containing the orientation of the drone
	 */
	private Vector Orientation;
	
	/**
	 * A variable containing the rotation vector of the drone
	 */
	private Vector rotationVector;
	
	/**
	 * A variable containing the angular acceleration of the drone
	 */
	private Vector angularAccelerationVector;
	
	/**
	 * a variable containing the center of mass of the drone
	 */
	private Vector massCenter;
	
	
	/**
	 * A variable containing the inertia moment of the drone
	 */
	private float inertiaMoment;
	
	/**
	 * A variable containing the maximum thrust of the drone 
	 * @Immutable
	 */
	private float maxThrust;
	
	/**
	 * A variable containing the current thrust of the drone
	 */
	private float thrust;
	
	/*
	 * Constants
	 */
	/**
	 * Constant: light speed, used in velocity check, maybe redundant
	 */
	private static float LIGHTSPEED = 300000000;
	
	/*
	 * Error Messages:
	 */
	
	private final static String THRUST_OUT_OF_RANGE = "The thrust is out of range: [0, this.maxThrust]";
	
	private final static String VELOCITY_ERROR = "The velocity exceeds the upper limit";
}
